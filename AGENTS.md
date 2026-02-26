# Calycopis - Execution Broker prototype
This project implements the IVOA Execution Broker service.

## High-level overview

* The Execution Broker interface is intended provide an abstract interface that provides a common API for a range of different execution platforms, OpenStack, Docker, Kubernetes, Slurm, Panda etc
* An Execution Broker service takes an execution request (what to run + required resources) and returns a set of offers for execution sessions that the platform canb execute.
* When a user accepts an offer, the Execution Broker prepares and executes the task on the platform, providing a common abstract interface to monitor the status and get access to the execution.

### Main concepts:

 * Offer sets: Offers for execution sessions that the broker can provide for a given request (OfferSetRequest → OfferSetResponse).
 * Execution sessions: Concrete instance of an execution session.
 * Resources: Executables, compute, storage, volumes, and data resources (S3, Rucio, IVOA, SKAO).
 * Lifecycle: Phases and schedules for components and sessions.

### API surface – endpoints & behavior
 * `POST /offersets` (OfferSetPost)
   * Request body: OfferSetRequest in application/json, application/xml, or application/yaml.
   * Responses:
     * 303 See Other with required Location header pointing to /offersets/{uuid}.
     * 200 OK with OfferSetResponse directly.
   * Implications:
     * Clients must be prepared for both immediate data (200) and async-style redirect (303) patterns.
     * Good for implementations that might be slow and prefer to hand back a polling URL.
 * `GET /offersets/{uuid}` (OfferSetGet)
   * Path param uuid with uuid format.
   * Returns OfferSetResponse in the same three media types.
   * Represents a stable, queryable resource for a particular offer set.
 * `GET /sessions/{uuid}` (ExecutionSessionGet)
   * Retrieves an execution session by UUID.
   * Response type is ExecutionSessionResponseFour which is a oneOf of SimpleExecutionSession and ScheduledExecutionSession.
   * Design choice: server returns polymorphic session; clients must look at the kind discriminator.
 * `POST /sessions/{uuid}` (ExecutionSessionPost)
   * Path param uuid is a session identifier.
   * Request body: AbstractUpdate (discriminated union of specific update types).
   * Response: again ExecutionSessionResponseFour.
   * Pattern: "patch by update-command" rather than JSON Patch – the kind of update and path decide what to change.
 * Security
   * No securitySchemes defined: spec is transport/auth agnostic right now.
   * Project plan includes support for OAuth2 / bearer

### Data model – core building blocks

 * Polymorphism via discriminators
   * AbstractExecutable → DockerContainer, SingularityContainer, JupyterNotebook (URI kind values as mapping keys).
   * AbstractComputeResource → SimpleComputeResource.
   * AbstractStorageResource → SimpleStorageResource.
   * AbstractVolumeMount → SimpleVolumeMount.
   * AbstractDataResource → SimpleDataResource, S3DataResource, RucioDataResource, IvoaDataResource, SkaoDataResource.
   * AbstractExecutionSession → SimpleExecutionSession, ScheduledExecutionSession.
   * AbstractOption and AbstractUpdate also use discriminators for option/update variants.
 * Base component model
   * AbstractComponent supplies kind (URI) and meta (ComponentMetadata).
   * ComponentMetadata adds identifiers, human text, timestamps, messages, and options.
   * LifecycleComponent embeds phase and schedule for components with state.
 * Execution composition
   * ExecutionRequestComponents and SimpleExecutionComponents share the same structure:
     * executable (AbstractExecutable),
     * compute (AbstractComputeResource),
     * storage (AbstractStorageResourceList),
     * volumes (AbstractVolumeMountList),
     * data (AbstractDataResourceList).
   * This gives a uniform way to describe what an execution needs vs. what an execution session actually has.
 * Offer sets
   * OfferSetRequest = ExecutionRequestComponents + optional RequestedScheduleBlock.
   * OfferSetResponse = AbstractComponent + fields:
     * result (YES/NO) with semantics about whether the service can handle the request.
     * Optional description.
     * offers: array of AbstractExecutionSession (polymorphic sessions).

### Scheduling & lifecycle

 * Lifecycle states
   * LifecyclePhase and SimpleExecutionSessionPhase give rich state machines (INITIAL/WAITING/PREPARING/AVAILABLE/RUNNING/RELEASING/COMPLETED/FAILED/CANCELLED etc.).
   * SimpleExecutionSessionPhase includes extra states (e.g. OFFERED, ACCEPTED, REJECTED, EXPIRED) on top of generic lifecycle phases to support the offer lifecycle.
 * Lifecycle vs schedule structures
   * LifecycleSchedule and ScheduledExecutionSchedule both have preparing, available, releasing fields referencing _StartDuration types.

### Executables & runtime environment

 * DockerContainer
   * Includes image (DockerImageSpec), privileged, entrypoint, environment (NameValueMap), and network (DockerNetworkSpec).
   * DockerImageSpec includes locations array, digest, and platform (DockerPlatformSpec).
   * DockerNetworkSpec → ports array → DockerNetworkPort (with internal and external ports, protocol, access flag, path).
 * Other executable types
   * SingularityContainer – simple location URL.
   * JupyterNotebook – location URL (further work is needed to support different notebook references).
   * All reuse AbstractExecutable → consistent kind and lifecycle handling.

### Compute, storage, volumes, and data

 * Compute
   * SimpleComputeResource with nested SimpleComputeCores (min/max) and SimpleComputeMemory (min/max GiB), plus volumes.
   * This gives brokers enough flexibility to negotiate between min/max resources.

 * Storage & volumes
   * SimpleStorageResource with SimpleStorageSize (min/max GiB) and optional data list.
   * SimpleVolumeMount uses path, mode (READONLY / READWRITE), cardinality (SINGLE / CONTAINER), resources list.
   * The API distinguishes between where data lives (AbstractDataResource) and how it is presented to the executable’s filesystem (volume mounts).

 * Data resources
   * AbstractDataResource includes storage and kind discriminator, branching into:
     * SimpleDataResource – single downloadable URL.
     * S3DataResource – endpoint/template/bucket/object for object storage.
     * RucioDataResource – via RucioDataResourceBlock with endpoint/scope/object/type.
     * IvoaDataResource – IVOA metadata via DID, ObsCore, DataLink.
     * SkaoDataResource – SKAO-specific metadata extending IvoaDataResource with namespace/objectname/objecttype/datasize/checksum/replicas.
   * The design is intended to be extensible; new data backends can be added via new kind URIs.

### Messages, options, and updates

 * Messages
   * MessageItem models log/diagnostic messages (kind/time/level/template/values/message) aligned with Message Templates standard.
   * Appears within ComponentMetadata.messages, so every component may carry rich diagnostics.

 * Options API
   * AbstractOption (discriminated on kind) allows the service to present configurable options for components, each targeting a path.
   * Flavours:
     * StringValueOption with optional regex pattern.
     * EnumValueOption with allowed values.
     * IntegerValueOption/IntegerDeltaOption with min/max and units.
   * Pattern: server advertises what can be tuned; client chooses values within constraints.

 * Updates API
   * AbstractUpdate mirrors AbstractOption for making actual changes to those targets.
   * Flavours:
     * StringValueUpdate, EnumValueUpdate (string payloads).
     * IntegerValueUpdate, IntegerDeltaUpdate (numeric payloads with units).
   * Combined with `POST /sessions/{uuid}`: forms a command-style patch system – path+kind+value/delta.

### Media types and representation details

 * All the endpoints support application/json, and application/yaml.
 * URI formats
   * Many fields (kind, url, ivoid) use format: uri, aligning with general web semantics and IVOA identifiers.

## OpenAPI schema

 * The OpenAPI schema for the Execution Broker is published in the `https://github.com/ivoa/Calycopis-schema/ project on GitHub`.
 * There is a local copy of the Calycopis-schema project available at `/calycopis/Calycopis-schema/github-zrq/`
 * The Execution Broker API is defined in `/calycopis/Calycopis-schema/github-zrq/schema/v1.0/execution-broker.yaml`

 * The Calycopis-broker project depends on the `net.ivoa.calycopis:calycopis-spring:1.0.3-SNAPSHOT` package, which contains Spring Boot classes generated from the schema.
 * The Maven project for the `calycopis-spring` package is available at `/calycopis/Calycopis-schema/github-zrq/codegen/java/spring`
 * The source code for the generated Spring Boot classes are available at `/calycopis/Calycopis-schema/github-zrq/codegen/java/spring/target/generated-sources/openapi`.

 * The Calycopis-broker project uses Python client classes generated from the schema for testing.
 * The Python project for the Python client classes is available at `/calycopis/Calycopis-schema/github-zrq/codegen/python/client/build/`

## Design patterns

### Three-tier Entity/Factory/Validator/Repository pattern

Every domain concept (compute, storage, data, executable, volume, session) follows a
three-tier inheritance pattern with consistent file roles at each tier. The tiers map
to the package structure under `net.ivoa.calycopis.datamodel.<concept>/`:

<concept>/                          ← Tier 1: Abstract base
<concept>/simple/                   ← Tier 2: Schema type (e.g. SimpleComputeResource)
<concept>/simple/mock/              ← Tier 3: Platform-specific implementation

**Tier 1 – Abstract base** (`<concept>/`)
Defines the polymorphic root for a family of types. Files:
| File | Role |
|------|------|
| `Abstract<Concept>.java` | Public interface extending `LifecycleComponent`. Defines `WEBAPP_PATH` and domain-specific getters. |
| `Abstract<Concept>Entity.java` | JPA `@Entity` with `@Inheritance(JOINED)`. Abstract base class holding the session reference and common persistence fields. |
| `Abstract<Concept>EntityFactory.java` | Factory interface for creating new entities from validation results and for selecting an exiting on based on its identifier. |
| `Abstract<Concept>EntityFactoryImpl.java` | Abstract factory implementation (extends `FactoryBaseImpl`). |
| `Abstract<Concept>EntityRepository.java` | Spring `@Repository` interface extending `LifecycleComponentEntityRepository`. |
| `Abstract<Concept>Validator.java` | Validator interface extending `Validator<IvoaType, EntityType>`. Contains a nested `Result` interface and `ResultBean` class. |
| `Abstract<Concept>ValidatorImpl.java` | Abstract validator implementation (extends `AbstractValidatorImpl`). |
| `Abstract<Concept>ValidatorFactory.java` | Combines `Validator` and `ValidatorFactory` — acts as a chain-of-responsibility dispatcher. |
| `Abstract<Concept>ValidatorFactoryImpl.java` | Spring `@Component` that iterates registered validators until one of them returns `ACCEPTED` or `FAILED`. |

**Tier 2 – Schema type** (`<concept>/simple/`)
A concrete type from the OpenAPI schema (e.g. `SimpleComputeResource`). Files:
| File | Role |
|------|------|
| `Simple<Concept>.java` | Interface extending `Abstract<Concept>`. Defines `TYPE_DISCRIMINATOR` URI and type-specific getters. |
| `Simple<Concept>Entity.java` | JPA `@Entity` with `@DiscriminatorValue`. Adds type-specific `@Column` fields. Still abstract — leaves platform-specific methods (like
`getPrepareAction`) unimplemented. |
| `Simple<Concept>EntityFactory.java` | Factory interface extending the abstract factory. |
| `Simple<Concept>EntityFactoryImpl.java` | Abstract factory implementation. |
| `Simple<Concept>Validator.java` | Validator interface extending the abstract validator. |
| `Simple<Concept>ValidatorImpl.java` | Validates the specific Ivoa type using exact class matching (`getClass() ==`, not `instanceof`). Creates a `ResultBean` with a `build()` method
that delegates to the entity factory. |

**Tier 3 – Platform implementation** (`<concept>/simple/mock/`)
A concrete, instantiable implementation for a specific platform (currently `mock`).
This is the tier where classes become non-abstract. Files:
| File | Role |
|------|------|
| `Mock<Concept>.java` | Interface extending `Simple<Concept>`. |
| `Mock<Concept>Entity.java` | Concrete JPA `@Entity`. Implements platform-specific behavior (e.g. `getPrepareAction`). |
| `Mock<Concept>EntityFactory.java` | Factory interface with a `create()` method taking the mock-specific validator result. |
| `Mock<Concept>EntityFactoryImpl.java` | Concrete `@Component` factory. Receives the `@Repository` via `@Autowired` and calls `repository.save(new MockEntity(...))`. |
| `Mock<Concept>EntityRepository.java` | Concrete `@Repository` for the mock entity. |
| `Mock<Concept>Validator.java` | Validator interface extending the schema-type validator. |
| `Mock<Concept>ValidatorImpl.java` | Concrete `@Component` validator. Registered with the `ValidatorFactory` at startup. |

### How the pieces connect at runtime
1. **Request arrives** → `OfferSetRequestParser` extracts each component (executable, compute, storage, etc.)
2. **Validation** → For each component, the parser calls the corresponding `ValidatorFactory.validate()`. The factory iterates its registered validators (chain-of-responsibility). Each
validator uses exact class matching to decide if should handle the request. It returns `CONTINUE` the factory should continue to the next validator, `ACCEPTED`
if the component was validated and accepted, and `FAILED` if the component failed the validation.
3. **Result accumulation** → Accepted validators produce a `Result` object (containing the validated Ivoa bean) and add it to the `OfferSetRequestParserContext`.
4. **Entity creation** → When an offer is built, `Result.build(session, offer)` is called, which delegates to the entity factory's `create()` method. The factory constructs the entity
and persists it via the repository.
5. **Serialization** → Entities implement `makeBean(URIBuilder)` to convert back to Ivoa beans for the API response.

### Adding a new platform implementation
To add a new platform (e.g. `docker`):
1. Create package `functional.platfom.docker/`.
2. Create the 2 files following the mock pattern for the interface and the implementation.
3. The implementation starts as a copy of the mock platform, using Mock validators and factories.

1. Create package `datamodel/compute/simple/docker/`.
2. Create the 7 files following the mock pattern: interface, entity, factory interface, factory impl, repository, validator interface, validator impl.
3. The entity class is the only non-trivial one — implement `getPrepareAction()` with real logic to connect to the Docker platform.
4. The factory impl is a `@Component` that `@Autowired` receives the repository and calls `repository.save()`.
5. The validator impl is a `@Component` that registers itself with the `ValidatorFactory` at startup.

### Adding a new resource type
To add an entirely new resource type (e.g. `gpu`):
1. Create package `datamodel/gpu/`.
2. Create the abstract tier files following the compute pattern.
3. Create `datamodel/gpu/simple/` with the schema-type tier.
4. Create `datamodel/gpu/simple/mock/` with the platform tier.
5. Add a corresponding `ValidatorFactory` and wire it into `OfferSetRequestParser`.
6. Add the new component to `ExecutionRequestComponents` / `SimpleExecutionComponents` in the schema.

## Coding conventions

 * The implementation is based on the [Spring Boot](https://spring.io/projects/spring-boot) framework.
 * Where possible generic [Java Persistence API](https://en.wikipedia.org/wiki/Jakarta_Persistence) (JPA) annotations should be used rather than Spring framework specific ones, to make it easier to port the project to a different framework in the future.
 * The code style should favour clarity over brevity.

    This works
    ```
    x=y>27?y-27:y;
    ```
    but this is clearer and more maintainable:
    ```
    if (y > 27)
        {
        x = y - 27 ;
        }
    else {
        x = y ;
        }
    ```

## Development platform

### Docker container

 * Development is performed inside an instance of the `developer-tools` Docker container.
 * The source code for the `developer-tools` container is defined as part of this project at `docker/developer-tools/`.
 * The `developer-tools` container is a RedHat Fedora container with the following tools installed:
  * atop
  * bind-utils
  * curl
  * dateutils
  * diffutils
  * findutils
  * git
  * gnupg
  * gzip
  * hostname
  * htop
  * iotop
  * ipcalc
  * jq
  * less
  * nano
  * openssh-clients
  * patch
  * procps-ng
  * pwgen
  * rsync
  * sed
  * s3cmd
  * tar
  * wget
  * which
  * xmlstarlet
  * yamllint
  * yq
  * zip

 * Additional tools can be installed using `dnf` but requires user permission to do so.

## Docker service

 * The host system runs Podman as a rootless service.
 * See https://docs.podman.io/en/latest/markdown/podman-system-service.1.html for details.
 * The `developer-tools` container is launched with a volume mount mapping the unix socket for the host Podman service into the container, enabling agents running in the container to access the Podman service on the host.

```
podman run \
  ....
  --env "CONTAINER_HOST=unix:///run/podman/podman.sock" \
  --volume "${XDG_RUNTIME_DIR}/podman/podman.sock:/run/podman/podman.sock:Z" \
  ....
  ....
```

## Project structure

### Directory layout

 * attic/ - A place for things that are no longer used.

 * docker/ - Definitions for the Docker containers used by the project.
   * bin/ - A set of shell scripts used to manually build the Docker containers.
   * compose/ - A docker-compose script to launch the broker service and database.
   * developer-tools/ - The Dockerfile and source code for the `developer-tools` container.
   * fedora-base/ - The base RedHat Fedora image used by the `developer-tools` container.
   * java-runtime/ - The base image used to build the `calycopis-broker` service container.

 * docs/ - A  plac e for documents and documentation.
   * adass - Presentations made at ADASS conferences.
 * java/ - The main project source code.
 * notes/ - Contemporary notes about the project development.
 * tests/ - A set of tests for the project.
  * curl/ - A set of examples using `curl` to check the service behaviour.
  * python/ - A set of Python tests using the Python client module generated from the OpenAPI schema.

### Maven build

The project can be built from the `java` directory using the following Maven command
```
./mvnw clean compile
```

The service can be run from the `java`  directory using the following Maven command
```
./mvnw clean spring-boot:run
```

### External dependencies

 * [Spring Boot](https://spring.io/projects/spring-boot) The framework for developing web applications.
 * [PostgreSQL](https://www.postgresql.org/) provides a database to store the Java Persistence API entities
 * [Jackson FasterXML](https://github.com/FasterXML/jackson) and [Jackson annotations](https://github.com/FasterXML/jackson-annotations) to provide serialization and deserialization for JSON, YAML, and XML.
 * [ThreeTen-Extra](https://www.threeten.org/threeten-extra/) and [ThreeTen-Extra](https://github.com/ThreeTen/threeten-extra) provides additional date-time classes, particularly Interval.
 * [SLF4J logging](https://www.slf4j.org/manual.html) logging framework.
 * [Lombok](https://projectlombok.org/) is used for boilerplate reduction, primarily SLF4J logging.

## Testing

### Curl tests

The `tests/curl` directory contains a set of worked examples that use curl to send and receive messages to the service.


### Java tests

The `tests/java` directory contains a set of Java tests that use code coverage metics to verify the functionality.

The Java tests use the Java client classes generated from the OpenAPI schema to test
the service functionality and interoperability between the Spring based service implementation
and the generic Java client classes.

### Python tests

The `tests/python` directory contain a set of Python tests for the service.

The Python tests use the Python client classes generated from the OpenAPI schema to test
both the service functionality and cross-language interoperability between of the Java
service and a Python client.

The following command will import the Python client classes generated from the OpenAPI schema
into the development environment making them available to be used by the Python tests.

``
pip install  --editable /calycopis/Calycopis-schema/github-zrq/codegen/python/client/build
``






