# FTAToken

Spring Boot 4.1 service (Java 21, Maven).

## Requirements

- JDK 21+ (`JAVA_HOME` must point to it; Spring Boot 4 does not run on Java 11)
- Maven is provided via the wrapper (`mvnw` / `mvnw.cmd`)

## Build & test

```bash
./mvnw verify
```

## Run

```bash
./mvnw spring-boot:run
```

- `GET http://localhost:8080/api/ping` — sample endpoint
- `GET http://localhost:8080/api/blockchain/network` — chain ID and latest block of the configured node
- `GET http://localhost:8080/actuator/health` — health check (also `/actuator/health/liveness` and `/readiness`)

## Response format

Every `/api/**` endpoint returns an `ApiResult` envelope (`common/ApiResult.java`):

```json
{ "code": 200, "errorCode": null, "errorMessage": null, "error": null, "data": { ... } }
```

Failures use `code: 600` with `errorMessage` set; `GlobalExceptionHandler` wraps all errors
(validation, 404, node failures, unexpected exceptions) in this shape while keeping the HTTP status.

## Ethereum (web3j)

A `Web3j` bean is configured from `web3j.*` properties:

| Property                 | Env var                | Default                 |
|--------------------------|------------------------|-------------------------|
| `web3j.client-address`   | `WEB3J_CLIENT_ADDRESS` | `https://sepolia.base.org` (Base Sepolia, chain ID 84532) |
| `web3j.network-timeout`  | `WEB3J_NETWORK_TIMEOUT`| `30s`                   |

The default is Base's public Sepolia endpoint, which is rate-limited — use a dedicated provider
URL via `WEB3J_CLIENT_ADDRESS` for anything beyond local development.

The `ethereum` health component reports the node's chain ID and latest block. If no node is
reachable, `/actuator/health` reports `DOWN`; the liveness/readiness probes are unaffected.

## Layout

```
src/main/java/com/fintechautomation/ftatoken/
  FtaTokenApplication.java   # entry point
  blockchain/
    config/                  # Web3j bean + web3j.* properties
    controller/              # /api/blockchain REST endpoints
    service/                 # node interaction via web3j
    health/                  # actuator health indicator for the node
  common/                    # ApiResult envelope + GlobalExceptionHandler
  web/                       # REST controllers
src/main/resources/
  application.yml            # configuration
```
