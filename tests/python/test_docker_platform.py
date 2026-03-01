#
# <meta:header>
#   <meta:licence>
#     Copyright (c) 2026, Manchester (http://www.manchester.ac.uk/)
#
#     This information is free software: you can redistribute it and/or modify
#     it under the terms of the GNU General Public License as published by
#     the Free Software Foundation, either version 3 of the License, or
#     (at your option) any later version.
#
#     This information is distributed in the hope that it will be useful,
#     but WITHOUT ANY WARRANTY; without even the implied warranty of
#     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#     GNU General Public License for more details.
#
#     You should have received a copy of the GNU General Public License
#     along with this software. If not, see <http://www.gnu.org/licenses/>.
#   </meta:licence>
# </meta:header>
#

"""
Integration tests for the Docker platform implementation.

These tests verify the broker can create offers, accept sessions,
and execute Docker containers via the Docker/Podman platform.

The test container is Heliophorus-cantliei, a simple Alpine container
that waits for a configurable number of seconds and then exits.
https://github.com/Zarquan/Heliophorus-cantliei

Requires:
  - A running Calycopis broker service with the 'docker' profile active.
  - The CONTAINER_HOST environment variable set in the broker environment.
  - The calycopis_client Python package installed.
  - Network access to ghcr.io to pull the test container image.

Usage:
  pytest tests/python/test_docker_platform.py -v
  CALYCOPIS_URL=http://host:port pytest tests/python/test_docker_platform.py -v
"""

import os
import time

import pytest

from calycopis_client.wrappers.execution_client import ExecutionBrokerClient
from calycopis_client.models import (
    OfferSetRequest,
    OfferSetResponse,
    SimpleExecutionSessionPhase,
)
from calycopis_client.models.docker_container import DockerContainer
from calycopis_client.models.docker_image_spec import DockerImageSpec
from calycopis_client.models.simple_compute_resource import SimpleComputeResource
from calycopis_client.models.simple_compute_cores import SimpleComputeCores
from calycopis_client.models.simple_compute_memory import SimpleComputeMemory
from calycopis_client.models.component_metadata import ComponentMetadata


# ---------------------------------------------------------------------------
# Configuration
# ---------------------------------------------------------------------------

CALYCOPIS_URL = os.environ.get("CALYCOPIS_URL", "http://localhost:8082")

DOCKER_CONTAINER_KIND = (
    "https://www.purl.org/ivoa.net/EB/schema/v1.0/types/executable/docker-container-1.0"
)
SIMPLE_COMPUTE_KIND = (
    "https://www.purl.org/ivoa.net/EB/schema/v1.0/types/compute/simple-compute-resource-1.0"
)

# Heliophorus-cantliei test container: waits N seconds then exits.
CANTLIEI_IMAGE = "ghcr.io/zarquan/heliophorus-cantliei:sha-c9572b0"
CANTLIEI_DIGEST = "sha256:4911760109f78976d2a95a6491a8d8c77bfee9fd1498b9a4b7dd5b7515826689"


# ---------------------------------------------------------------------------
# Fixtures
# ---------------------------------------------------------------------------

def _server_reachable() -> bool:
    """Return True if the Calycopis broker is responding."""
    import urllib.request
    import urllib.error
    try:
        urllib.request.urlopen(CALYCOPIS_URL, timeout=5)
        return True
    except urllib.error.HTTPError:
        return True
    except Exception:
        return False


pytestmark = pytest.mark.skipif(
    not _server_reachable(),
    reason=f"Calycopis broker not reachable at {CALYCOPIS_URL}",
)


@pytest.fixture(scope="module")
def client() -> ExecutionBrokerClient:
    """Create a shared ExecutionBrokerClient for the test module."""
    return ExecutionBrokerClient(host=CALYCOPIS_URL)


# ---------------------------------------------------------------------------
# Helper functions
# ---------------------------------------------------------------------------

def _make_cantliei_executable(name: str = "cantliei-test", pause_seconds: int = 10) -> DockerContainer:
    """Create a Heliophorus-cantliei DockerContainer executable.

    Args:
        name: Human-readable name for the executable.
        pause_seconds: Number of seconds the container should pause before exiting.
    """
    return DockerContainer(
        kind=DOCKER_CONTAINER_KIND,
        meta=ComponentMetadata(name=name),
        image=DockerImageSpec(
            locations=[CANTLIEI_IMAGE],
            digest=CANTLIEI_DIGEST,
        ),
        command=[str(pause_seconds)],
    )


def _submit(client: ExecutionBrokerClient, request: OfferSetRequest) -> OfferSetResponse:
    """Submit a request and return the OfferSetResponse."""
    response = client.submit_execution(request, follow_redirect=True)
    assert isinstance(response, OfferSetResponse), (
        f"Expected OfferSetResponse, got {type(response)}"
    )
    return response


def _assert_accepted(response: OfferSetResponse, msg: str = ""):
    """Assert that the response result is YES with at least one offer."""
    assert response.result == "YES", (
        f"Expected YES, got {response.result}. {msg} "
        f"Messages: {response.meta.messages if response.meta else 'none'}"
    )
    assert response.offers is not None and len(response.offers) > 0, (
        f"Expected at least one offer. {msg}"
    )


def _assert_rejected(response: OfferSetResponse, msg: str = ""):
    """Assert that the response result is NO (validation failed)."""
    assert response.result == "NO", (
        f"Expected NO, got {response.result}. {msg} "
        f"Messages: {response.meta.messages if response.meta else 'none'}"
    )


# ===========================================================================
# Offer creation tests
# ===========================================================================

class TestDockerPlatformOffers:
    """
    Tests that the Docker platform can create valid offers for
    the Heliophorus-cantliei test container.
    """

    def test_cantliei_basic_offer(self, client):
        """
        Submitting the cantliei container with no explicit compute
        should produce a YES response with at least one offer.
        """
        request = OfferSetRequest(
            executable=_make_cantliei_executable("cantliei-basic", pause_seconds=5),
        )
        response = _submit(client, request)
        _assert_accepted(response, "Basic cantliei offer should be accepted")

        offer = response.offers[0]
        assert offer.executable is not None, "Offer should include an executable"
        assert offer.compute is not None, "Offer should include a compute resource"
        assert offer.phase == SimpleExecutionSessionPhase.OFFERED

    def test_cantliei_with_compute(self, client):
        """
        Submitting the cantliei container with explicit compute resource
        limits should produce a YES response.
        """
        request = OfferSetRequest(
            executable=_make_cantliei_executable("cantliei-with-compute", pause_seconds=5),
            compute=SimpleComputeResource(
                kind=SIMPLE_COMPUTE_KIND,
                meta=ComponentMetadata(name="cantliei-compute"),
                cores=SimpleComputeCores(min=1, max=2),
                memory=SimpleComputeMemory(min=1, max=2),
            ),
        )
        response = _submit(client, request)
        _assert_accepted(response, "Cantliei with compute should be accepted")

        offer = response.offers[0]
        assert offer.compute is not None
        assert offer.compute.cores is not None
        assert offer.compute.memory is not None

    def test_cantliei_compute_over_limit_rejected(self, client):
        """
        Requesting compute resources that exceed the Docker platform
        limits (min cores > 16) should be rejected.
        """
        request = OfferSetRequest(
            executable=_make_cantliei_executable("cantliei-over-limit", pause_seconds=5),
            compute=SimpleComputeResource(
                kind=SIMPLE_COMPUTE_KIND,
                meta=ComponentMetadata(name="over-limit-compute"),
                cores=SimpleComputeCores(min=32, max=32),
            ),
        )
        response = _submit(client, request)
        _assert_rejected(response, "Min cores > 16 should be rejected")

    def test_cantliei_offer_has_executable_image(self, client):
        """
        The offer's executable should preserve the Docker image details
        from the request.
        """
        request = OfferSetRequest(
            executable=_make_cantliei_executable("cantliei-image-check", pause_seconds=5),
        )
        response = _submit(client, request)
        _assert_accepted(response)

        offer = response.offers[0]
        executable = offer.executable
        assert executable is not None

        # The executable should be a DockerContainer with image info.
        assert executable.image is not None, "Executable should have image info"
        assert executable.image.locations is not None
        assert CANTLIEI_IMAGE in executable.image.locations
        assert executable.image.digest == CANTLIEI_DIGEST

    def test_cantliei_offer_has_session_uuid(self, client):
        """
        Each offer should have a session UUID assigned in its metadata.
        """
        request = OfferSetRequest(
            executable=_make_cantliei_executable("cantliei-uuid-check", pause_seconds=5),
        )
        response = _submit(client, request)
        _assert_accepted(response)

        offer = response.offers[0]
        assert offer.meta is not None, "Offer should have metadata"
        assert offer.meta.uuid is not None, "Offer should have a UUID"


# ===========================================================================
# Session lifecycle tests
# ===========================================================================

class TestDockerPlatformSessionLifecycle:
    """
    Tests for the full session lifecycle on the Docker platform:
    OFFERED -> ACCEPTED -> PREPARING -> AVAILABLE -> RUNNING -> COMPLETED

    These tests accept an offer, pass a specific pause duration to the
    cantliei container via the command property, and verify that the
    session transitions through the expected phases and completes at
    approximately the expected time.
    """

    def test_accept_offer(self, client):
        """
        Accepting an offered session should transition it to ACCEPTED.
        """
        request = OfferSetRequest(
            executable=_make_cantliei_executable("cantliei-accept", pause_seconds=10),
        )
        response = _submit(client, request)
        _assert_accepted(response)

        offer = response.offers[0]
        session_uuid = offer.meta.uuid

        session = client.set_session_phase(
            session_uuid,
            SimpleExecutionSessionPhase.ACCEPTED,
        )
        assert session.phase in (
            SimpleExecutionSessionPhase.ACCEPTED,
            SimpleExecutionSessionPhase.PREPARING,
        ), (
            f"After accepting, session should be ACCEPTED or PREPARING, "
            f"got {session.phase}"
        )

    def test_session_reaches_preparing(self, client):
        """
        After accepting, the session should eventually reach PREPARING
        as the broker begins to prepare the Docker container.
        """
        request = OfferSetRequest(
            executable=_make_cantliei_executable("cantliei-preparing", pause_seconds=30),
        )
        response = _submit(client, request)
        _assert_accepted(response)

        offer = response.offers[0]
        session_uuid = offer.meta.uuid

        client.set_session_phase(
            session_uuid,
            SimpleExecutionSessionPhase.ACCEPTED,
        )

        session = client.wait_for_phase(
            session_uuid,
            target_phases=[
                SimpleExecutionSessionPhase.PREPARING,
                SimpleExecutionSessionPhase.AVAILABLE,
                SimpleExecutionSessionPhase.RUNNING,
                SimpleExecutionSessionPhase.COMPLETED,
                SimpleExecutionSessionPhase.FAILED,
            ],
            timeout=60.0,
            interval=2.0,
        )
        assert session.phase in (
            SimpleExecutionSessionPhase.PREPARING,
            SimpleExecutionSessionPhase.AVAILABLE,
            SimpleExecutionSessionPhase.RUNNING,
            SimpleExecutionSessionPhase.COMPLETED,
        ), (
            f"Session should reach PREPARING or beyond, got {session.phase}"
        )

    def test_session_reaches_available(self, client):
        """
        After preparation completes, the session should reach AVAILABLE,
        meaning the Docker container has been created and started.
        """
        request = OfferSetRequest(
            executable=_make_cantliei_executable("cantliei-available", pause_seconds=30),
        )
        response = _submit(client, request)
        _assert_accepted(response)

        offer = response.offers[0]
        session_uuid = offer.meta.uuid

        client.set_session_phase(
            session_uuid,
            SimpleExecutionSessionPhase.ACCEPTED,
        )

        session = client.wait_for_phase(
            session_uuid,
            target_phases=[
                SimpleExecutionSessionPhase.AVAILABLE,
                SimpleExecutionSessionPhase.RUNNING,
                SimpleExecutionSessionPhase.COMPLETED,
                SimpleExecutionSessionPhase.FAILED,
            ],
            timeout=600.0,
            interval=5.0,
        )
        assert session.phase in (
            SimpleExecutionSessionPhase.AVAILABLE,
            SimpleExecutionSessionPhase.RUNNING,
            SimpleExecutionSessionPhase.COMPLETED,
        ), (
            f"Session should reach AVAILABLE or beyond, got {session.phase}"
        )

    def test_session_completes_with_timed_container(self, client):
        """
        Run the cantliei container for a known duration and verify that
        the time from RUNNING to COMPLETED is consistent with the
        requested pause, independent of scheduling delays.

        The test waits (with a generous timeout) for the session to
        reach AVAILABLE or RUNNING -- which signals that the container
        has actually started -- and only then begins the timing check.
        """
        pause_seconds = 20
        # Generous budget for broker scheduling / image pulls before
        # the container starts.
        scheduling_timeout = 600.0
        # Overhead on top of pause_seconds for monitor poll intervals,
        # container startup/teardown and session state propagation.
        completion_overhead = 60.0

        request = OfferSetRequest(
            executable=_make_cantliei_executable(
                "cantliei-timed-complete",
                pause_seconds=pause_seconds,
            ),
        )
        response = _submit(client, request)
        _assert_accepted(response)

        offer = response.offers[0]
        session_uuid = offer.meta.uuid

        client.set_session_phase(
            session_uuid,
            SimpleExecutionSessionPhase.ACCEPTED,
        )

        # Phase 1 -- wait for the container to actually start.
        session = client.wait_for_phase(
            session_uuid,
            target_phases=[
                SimpleExecutionSessionPhase.AVAILABLE,
                SimpleExecutionSessionPhase.RUNNING,
                SimpleExecutionSessionPhase.COMPLETED,
                SimpleExecutionSessionPhase.FAILED,
            ],
            timeout=scheduling_timeout,
            interval=5.0,
        )
        assert session.phase in (
            SimpleExecutionSessionPhase.AVAILABLE,
            SimpleExecutionSessionPhase.RUNNING,
            SimpleExecutionSessionPhase.COMPLETED,
        ), (
            f"Session should reach AVAILABLE/RUNNING, got {session.phase}"
        )

        # Phase 2 -- the container is running; start the clock.
        running_time = time.monotonic()

        session = client.wait_for_phase(
            session_uuid,
            target_phases=[
                SimpleExecutionSessionPhase.COMPLETED,
                SimpleExecutionSessionPhase.FAILED,
            ],
            timeout=float(pause_seconds + completion_overhead),
            interval=5.0,
        )
        elapsed = time.monotonic() - running_time

        assert session.phase == SimpleExecutionSessionPhase.COMPLETED, (
            f"Session should reach COMPLETED, got {session.phase}"
        )

        assert elapsed >= pause_seconds, (
            f"Session completed too quickly ({elapsed:.1f}s) for a "
            f"{pause_seconds}s container"
        )
        assert elapsed <= pause_seconds + completion_overhead, (
            f"Session took too long ({elapsed:.1f}s) for a "
            f"{pause_seconds}s container (max allowed "
            f"{pause_seconds + completion_overhead}s)"
        )

    def test_short_and_long_containers(self, client):
        """
        Run two containers with different pause durations and verify
        the shorter one completes before the longer one.
        """
        short_pause = 10
        long_pause = 30
        wait_timeout = 600.0

        short_request = OfferSetRequest(
            executable=_make_cantliei_executable(
                "cantliei-short",
                pause_seconds=short_pause,
            ),
        )
        long_request = OfferSetRequest(
            executable=_make_cantliei_executable(
                "cantliei-long",
                pause_seconds=long_pause,
            ),
        )

        short_response = _submit(client, short_request)
        long_response = _submit(client, long_request)
        _assert_accepted(short_response)
        _assert_accepted(long_response)

        short_uuid = short_response.offers[0].meta.uuid
        long_uuid = long_response.offers[0].meta.uuid

        client.set_session_phase(
            short_uuid, SimpleExecutionSessionPhase.ACCEPTED,
        )
        client.set_session_phase(
            long_uuid, SimpleExecutionSessionPhase.ACCEPTED,
        )

        short_start = time.monotonic()

        short_session = client.wait_for_phase(
            short_uuid,
            target_phases=[
                SimpleExecutionSessionPhase.COMPLETED,
                SimpleExecutionSessionPhase.FAILED,
            ],
            timeout=wait_timeout,
            interval=5.0,
        )
        short_elapsed = time.monotonic() - short_start

        long_session = client.wait_for_phase(
            long_uuid,
            target_phases=[
                SimpleExecutionSessionPhase.COMPLETED,
                SimpleExecutionSessionPhase.FAILED,
            ],
            timeout=wait_timeout,
            interval=5.0,
        )
        long_elapsed = time.monotonic() - short_start

        assert short_session.phase == SimpleExecutionSessionPhase.COMPLETED, (
            f"Short session should reach COMPLETED, got {short_session.phase}"
        )
        assert long_session.phase == SimpleExecutionSessionPhase.COMPLETED, (
            f"Long session should reach COMPLETED, got {long_session.phase}"
        )

        assert short_elapsed < long_elapsed, (
            f"Short container ({short_pause}s, elapsed {short_elapsed:.1f}s) "
            f"should complete before long container "
            f"({long_pause}s, elapsed {long_elapsed:.1f}s)"
        )


# ===========================================================================
# Session cancellation test
# ===========================================================================

class TestDockerPlatformCancellation:
    """
    Tests for cancelling a Docker session.
    """

    def test_cancel_offered_session(self, client):
        """
        Rejecting an offered session should transition it to REJECTED.
        """
        request = OfferSetRequest(
            executable=_make_cantliei_executable("cantliei-reject", pause_seconds=5),
        )
        response = _submit(client, request)
        _assert_accepted(response)

        offer = response.offers[0]
        session_uuid = offer.meta.uuid

        session = client.set_session_phase(
            session_uuid,
            SimpleExecutionSessionPhase.REJECTED,
        )
        assert session.phase == SimpleExecutionSessionPhase.REJECTED, (
            f"After rejecting, session should be REJECTED, got {session.phase}"
        )

    def test_cancel_accepted_session(self, client):
        """
        Cancelling an accepted session should transition it to CANCELLED.
        """
        request = OfferSetRequest(
            executable=_make_cantliei_executable("cantliei-cancel", pause_seconds=5),
        )
        response = _submit(client, request)
        _assert_accepted(response)

        offer = response.offers[0]
        session_uuid = offer.meta.uuid

        client.set_session_phase(
            session_uuid,
            SimpleExecutionSessionPhase.ACCEPTED,
        )

        session = client.set_session_phase(
            session_uuid,
            SimpleExecutionSessionPhase.CANCELLED,
        )
        assert session.phase == SimpleExecutionSessionPhase.CANCELLED, (
            f"After cancelling, session should be CANCELLED, got {session.phase}"
        )


# ===========================================================================
# Multiple offers test
# ===========================================================================

class TestDockerPlatformMultipleOffers:
    """
    Tests for handling multiple concurrent offer requests
    on the Docker platform.
    """

    def test_independent_sessions(self, client):
        """
        Two independent offer requests should produce independent sessions
        with distinct UUIDs.
        """
        request_a = OfferSetRequest(
            executable=_make_cantliei_executable("cantliei-session-a", pause_seconds=5),
        )
        request_b = OfferSetRequest(
            executable=_make_cantliei_executable("cantliei-session-b", pause_seconds=5),
        )
        response_a = _submit(client, request_a)
        response_b = _submit(client, request_b)

        _assert_accepted(response_a)
        _assert_accepted(response_b)

        uuid_a = response_a.offers[0].meta.uuid
        uuid_b = response_b.offers[0].meta.uuid

        assert uuid_a != uuid_b, (
            "Independent sessions should have different UUIDs"
        )

    def test_accept_one_reject_other(self, client):
        """
        From two offers, accepting one and rejecting the other should
        produce independent outcomes.
        """
        request = OfferSetRequest(
            executable=_make_cantliei_executable("cantliei-dual", pause_seconds=10),
        )
        response = _submit(client, request)
        _assert_accepted(response)

        offer = response.offers[0]
        session_uuid = offer.meta.uuid

        # Accept this one.
        session = client.set_session_phase(
            session_uuid,
            SimpleExecutionSessionPhase.ACCEPTED,
        )
        assert session.phase in (
            SimpleExecutionSessionPhase.ACCEPTED,
            SimpleExecutionSessionPhase.PREPARING,
        )

        # Submit a second request and reject it.
        request2 = OfferSetRequest(
            executable=_make_cantliei_executable("cantliei-dual-reject", pause_seconds=5),
        )
        response2 = _submit(client, request2)
        _assert_accepted(response2)

        offer2 = response2.offers[0]
        session2 = client.set_session_phase(
            offer2.meta.uuid,
            SimpleExecutionSessionPhase.REJECTED,
        )
        assert session2.phase == SimpleExecutionSessionPhase.REJECTED

        # The first session should still be progressing.
        session1_check = client.get_session(session_uuid)
        assert session1_check.phase != SimpleExecutionSessionPhase.REJECTED, (
            "First session should not be affected by rejecting the second"
        )
