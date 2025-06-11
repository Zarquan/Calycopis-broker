# Calycopis - Execution Broker prototype
Experiments and prototypes for the IVOA ExecutionBroker.

This project is named after the <a href="https://en.wikipedia.org/wiki/Calycopis">_Calycopis_</a> genus of butterflies.

<a title="Charles J Sharp [CC BY-SA 4.0 (https://creativecommons.org/licenses/by-sa/4.0)], via Wikimedia Commons" href="https://commons.wikimedia.org/wiki/File:Trebula_groundstreak_(Calycopis_trebula).jpg"><img width="512" alt="Calycopis Trebula" src="https://upload.wikimedia.org/wikipedia/commons/thumb/8/8d/Trebula_groundstreak_%28Calycopis_trebula%29.jpg/440px-Trebula_groundstreak_%28Calycopis_trebula%29.jpg"></a>

Early work on this project was developed under the name CIRASA-planner and was funded by the CIRASA visualization tools project at INAF.

Current work on this project is being developed as part of the SKA SRCNet program.

[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-2.0-4baaaa.svg)](CODE_OF_CONDUCT.md)

----
To run a copy of the service in Podman or Docker.

```
podman run \
    --rm \
    --tty \
    --interactive \
    --publish 8082:8082 \
    --name calycopis-broker \
    ghcr.io/ivoa/calycopis/calycopis-broker:latest
```

---
Use curl to send the example requests in YAML.

```
curl \
    --silent \
    --show-error \
    --header 'Content-Type: application/yaml' \
    --data-binary "@examples/001/001-001-offerset-request.yaml" \
    --header 'Accept: application/yaml' \
    'http://127.0.0.1:8082/offersets'
```

---
Use curl to send the example requests in JSON.

```
curl \
    --silent \
    --show-error \
    --header 'Content-Type: application/json' \
    --data-binary "@examples/004/004-001-offerset-request.json" \
    --header 'Accept: application/json' \
    'http://127.0.0.1:8082/offersets'
```

