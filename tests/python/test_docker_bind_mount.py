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
# AIMetrics: [
#     {
#     "timestamp": "2026-04-11T06:00:00",
#     "name": "Cursor CLI",
#     "version": "2026.02.13-41ac335",
#     "model": "Claude 4.6 Opus (Thinking)",
#     "contribution": {
#       "value": 100,
#       "units": "%"
#       }
#     }
#   ]
#

"""
Integration tests for Docker bind mount support with file:// data resources.

Tests that a file:// data resource is correctly linked to a
DockerBindMountStorage and that the resulting bind mount is
passed to the Docker container at runtime.

Requires:
  - A running Calycopis broker service with the 'docker' profile active.
  - The CONTAINER_HOST environment variable set in the broker environment.
  - The calycopis_client Python package installed.
  - A local test file accessible to the broker at the path specified
    by the BIND_MOUNT_TEST_FILE environment variable.

Usage:
  BIND_MOUNT_TEST_FILE=/path/to/file.txt pytest tests/python/test_docker_bind_mount.py -v
  CALYCOPIS_URL=http://host:port BIND_MOUNT_TEST_FILE=/path/to/file.txt pytest tests/python/test_docker_bind_mount.py -v

Timeouts:
  PHASE_TIMEOUT controls how long (seconds) the tests wait for a session
  to transition between phases.  Default is 120 s, which is long enough
  to cover the broker's processing loop interval plus container pull time.
"""

import os

import pytest

from calycopis_client.wrappers.execution_client import ExecutionBrokerClient
from calycopis_client.models import (
    ExecutionRequest,
    OfferSetResponse,
    SimpleExecutionSessionPhase,
)
from calycopis_client.models.docker_container import DockerContainer
from calycopis_client.models.docker_image_spec import DockerImageSpec
from calycopis_client.models.simple_compute_resource import SimpleComputeResource
from calycopis_client.models.simple_compute_cores import SimpleComputeCores
from calycopis_client.models.simple_compute_memory import SimpleComputeMemory
from calycopis_client.models.simple_data_resource import SimpleDataResource
from calycopis_client.models.simple_volume_mount import SimpleVolumeMount
from calycopis_client.models.component_metadata import ComponentMetadata


# ---------------------------------------------------------------------------
# Configuration
# ---------------------------------------------------------------------------

CALYCOPIS_URL = os.environ.get("CALYCOPIS_URL", "http://localhost:8082")

BIND_MOUNT_TEST_FILE = os.environ.get(
    "BIND_MOUNT_TEST_FILE",
    "/home/Zarquan/temp/random.txt",
)

PHASE_TIMEOUT = float(os.environ.get("PHASE_TIMEOUT", "120"))

DOCKER_CONTAINER_KIND = (
    "https://www.purl.org/ivoa.net/EB/schema/v1.0/types/executable/docker-container-1.0"
)
SIMPLE_COMPUTE_KIND = (
    "https://www.purl.org/ivoa.net/EB/schema/v1.0/types/compute/simple-compute-resource-1.0"
)
SIMPLE_DATA_KIND = (
    "https://www.purl.org/ivoa.net/EB/schema/v1.0/types/data/simple-data-resource-1.0"
)
SIMPLE_VOLUME_KIND = (
    "https://www.purl.org/ivoa.net/EB/schema/v1.0/types/volume/simple-volume-mount-1.0"
)

CANTLIEI_IMAGE = "ghcr.io/zarquan/heliophorus-cantliei:sha-831ee57"
CANTLIEI_DIGEST = "sha256:6e495692cc6f1cae2023f261f433d4691aa70b19416730f8301e45fbb74bc526"


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

def _make_bind_mount_request(
    name: str = "bind-mount-test",
    pause_seconds: int = 10,
    file_url: str = None,
) -> ExecutionRequest:
    """Create an ExecutionRequest with a file:// data resource and a bind mount volume.

    This produces a request equivalent to the curl test in the notes file:
    an executable + a file:// data resource + a compute resource with a
    volume mount referencing the data resource.
    """
    if file_url is None:
        file_url = f"file://{BIND_MOUNT_TEST_FILE}"

    return ExecutionRequest(
        executable=DockerContainer(
            kind=DOCKER_CONTAINER_KIND,
            meta=ComponentMetadata(name=f"{name}-exec"),
            image=DockerImageSpec(
                locations=[CANTLIEI_IMAGE],
                digest=CANTLIEI_DIGEST,
            ),
            command=[str(pause_seconds), "0"],
        ),
        data=[
            SimpleDataResource(
                kind=SIMPLE_DATA_KIND,
                meta=ComponentMetadata(name=f"{name}-data"),
                location=file_url,
            ),
        ],
        compute=SimpleComputeResource(
            kind=SIMPLE_COMPUTE_KIND,
            meta=ComponentMetadata(name=f"{name}-compute"),
            volumes=[
                SimpleVolumeMount(
                    kind=SIMPLE_VOLUME_KIND,
                    resource=f"{name}-data",
                    path="/input",
                    mode="READONLY",
                ),
            ],
        ),
    )


# ===========================================================================
# Offer-set creation tests
# ===========================================================================

class TestBindMountOfferSet:
    """
    Tests that the offer-set endpoint correctly handles file:// data
    resources and creates the expected storage and volume entities.
    """

    def test_offerset_with_file_data_returns_yes(self, client):
        """
        An offer-set request with a file:// data resource should
        return result=YES with at least one offer.
        """
        request = _make_bind_mount_request("bind-offerset")
        response = client.submit_execution(request, follow_redirect=True)
        assert response.result == "YES", (
            f"Expected YES, got {response.result}"
        )
        assert response.offers is not None
        assert len(response.offers) > 0

    def test_offerset_offer_has_storage(self, client):
        """
        The offer should include a storage resource created for
        the bind mount (named 'BindMount' by the linker).
        """
        request = _make_bind_mount_request("bind-storage")
        response = client.submit_execution(request, follow_redirect=True)
        offer = response.offers[0]
        assert offer.storage is not None, (
            "Offer should include storage resources"
        )
        assert len(offer.storage) > 0, (
            "Offer should have at least one storage resource"
        )

    def test_offerset_offer_has_volume_mount(self, client):
        """
        The offer's compute resource should include a volume mount
        linking the data resource to the /input path.
        """
        request = _make_bind_mount_request("bind-volume")
        response = client.submit_execution(request, follow_redirect=True)
        offer = response.offers[0]
        assert offer.compute is not None, "Offer should have compute"
        assert offer.compute.volumes is not None, (
            "Compute should have volumes"
        )
        assert len(offer.compute.volumes) > 0, (
            "Compute should have at least one volume mount"
        )
        volume = offer.compute.volumes[0]
        assert volume.path == "/input", (
            f"Volume mount path should be /input, got {volume.path}"
        )
        assert volume.mode == "READONLY", (
            f"Volume mount mode should be READONLY, got {volume.mode}"
        )


# ===========================================================================
# Lifecycle tests — accepting and processing a bind mount session
# ===========================================================================

class TestBindMountLifecycle:
    """
    Tests that a session with a file:// bind mount progresses through
    the lifecycle after being accepted.

    Each phase transition is guarded by its own timeout so that a
    stuck session is detected quickly rather than hanging for the
    full test duration.
    """

    def test_bind_mount_session_progresses_beyond_accepted(self, client):
        """
        After accepting an offer the session should leave ACCEPTED
        within PHASE_TIMEOUT seconds.  A ComponentNotFoundException
        in the processing loop would leave it stuck at ACCEPTED.
        """
        request = _make_bind_mount_request(
            "bind-lifecycle",
            pause_seconds=30,
        )
        response = client.submit_execution(request, follow_redirect=True)
        assert response.result == "YES"
        assert len(response.offers) > 0

        offer = response.offers[0]
        offer_uuid = offer.meta.uuid

        client.set_session_phase(
            offer_uuid,
            SimpleExecutionSessionPhase.ACCEPTED,
        )

        result = client.wait_for_phase(
            offer_uuid,
            target_phases=[
                SimpleExecutionSessionPhase.PREPARING,
                SimpleExecutionSessionPhase.AVAILABLE,
                SimpleExecutionSessionPhase.RUNNING,
                SimpleExecutionSessionPhase.COMPLETED,
                SimpleExecutionSessionPhase.FAILED,
            ],
            timeout=PHASE_TIMEOUT,
            interval=5.0,
        )
        assert result.phase != SimpleExecutionSessionPhase.ACCEPTED, (
            f"Session should have progressed beyond ACCEPTED, "
            f"but is still at {result.phase}. This indicates the "
            f"processing loop failed to find a component."
        )

    def test_bind_mount_session_reaches_running(self, client):
        """
        The session should reach RUNNING (container launched) within
        PHASE_TIMEOUT seconds of being accepted.  Guards against the
        preparation stage hanging without launching a container.
        """
        request = _make_bind_mount_request(
            "bind-running",
            pause_seconds=30,
        )
        response = client.submit_execution(request, follow_redirect=True)
        assert response.result == "YES"
        assert len(response.offers) > 0

        offer = response.offers[0]
        offer_uuid = offer.meta.uuid

        client.set_session_phase(
            offer_uuid,
            SimpleExecutionSessionPhase.ACCEPTED,
        )

        result = client.wait_for_phase(
            offer_uuid,
            target_phases=[
                SimpleExecutionSessionPhase.RUNNING,
                SimpleExecutionSessionPhase.COMPLETED,
                SimpleExecutionSessionPhase.FAILED,
            ],
            timeout=PHASE_TIMEOUT,
            interval=5.0,
        )
        assert result.phase in (
            SimpleExecutionSessionPhase.RUNNING,
            SimpleExecutionSessionPhase.COMPLETED,
        ), (
            f"Session should reach RUNNING or COMPLETED, "
            f"got {result.phase}"
        )

    def test_bind_mount_session_completes(self, client):
        """
        A session with a file:// bind mount should progress all the
        way to COMPLETED.  Uses a short container duration (10 s) and
        a per-phase timeout so a stuck transition is caught quickly.
        """
        request = _make_bind_mount_request(
            "bind-complete",
            pause_seconds=10,
        )
        response = client.submit_execution(request, follow_redirect=True)
        assert response.result == "YES"
        assert len(response.offers) > 0

        offer = response.offers[0]
        offer_uuid = offer.meta.uuid

        client.set_session_phase(
            offer_uuid,
            SimpleExecutionSessionPhase.ACCEPTED,
        )

        phase_sequence = [
            (
                "ACCEPTED -> PREPARING",
                [
                    SimpleExecutionSessionPhase.PREPARING,
                    SimpleExecutionSessionPhase.AVAILABLE,
                    SimpleExecutionSessionPhase.RUNNING,
                    SimpleExecutionSessionPhase.COMPLETED,
                    SimpleExecutionSessionPhase.FAILED,
                ],
            ),
            (
                "PREPARING -> RUNNING",
                [
                    SimpleExecutionSessionPhase.RUNNING,
                    SimpleExecutionSessionPhase.COMPLETED,
                    SimpleExecutionSessionPhase.FAILED,
                ],
            ),
            (
                "RUNNING -> COMPLETED",
                [
                    SimpleExecutionSessionPhase.COMPLETED,
                    SimpleExecutionSessionPhase.FAILED,
                ],
            ),
        ]

        for label, targets in phase_sequence:
            result = client.wait_for_phase(
                offer_uuid,
                target_phases=targets,
                timeout=PHASE_TIMEOUT,
                interval=5.0,
            )
            if result.phase == SimpleExecutionSessionPhase.FAILED:
                pytest.fail(
                    f"Session FAILED during {label} transition"
                )
            if result.phase == SimpleExecutionSessionPhase.COMPLETED:
                break

        assert result.phase == SimpleExecutionSessionPhase.COMPLETED, (
            f"Session should reach COMPLETED, got {result.phase}"
        )
