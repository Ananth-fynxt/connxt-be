## denovm — Deno VM execution for Java/Spring

denovm executes dynamic JavaScript in an isolated Deno runtime from Java/Spring. It supports:
- Single-run mode: one Deno process per execution
- Worker mode: a pool of long-lived Deno worker processes (lower latency, higher throughput)

### Key features
- Process isolation with restricted permissions (net allowed; scoped read; no prompt; memory cap)
- Deterministic contract over stdin/stdout (JSON in/out)
- Unified executor script (`subprocess-executor.js`) for both modes
- Spring-friendly configuration via `deno.vm.*` properties

---

## Architecture

- Java service: `nexxus.denovm.service.impl.DenoVMServiceImpl`
  - Accepts a `DenoVMRequest` and returns a `DenoVMResult`
  - Single-run: spawns `deno run ... subprocess-executor.js`
  - Worker mode: submits requests to a `DenoWorkerPool` of persistent processes
- Worker pool: `nexxus.denovm.service.pool.*`
  - `DenoWorkerPool` manages N `DenoWorker` processes
  - Each worker speaks NDJSON on stdin/stdout
- JS executor: `libs/denovm/src/main/resources/nexxus/denovm/subprocess-executor.js`
  - Single-run (default) or worker mode (when arg `deno-vm-worker` is present)
  - Loads the user script from a temp file path and invokes an exported async function by name

Data flow (single run)
1) Java writes user code to a temp file
2) Java starts Deno with restricted flags and the executor
3) Java sends an execution context JSON to stdin
4) Executor imports the user module, runs the step handler, writes a single JSON result to stdout

Data flow (worker mode)
1) N workers run `subprocess-executor.js deno-vm-worker`
2) Java frames requests as one JSON per line (NDJSON)
3) Executor processes each line and writes one line JSON response with the same `id`

---

## Installation/Dependency

This module is a Gradle subproject. From another project in the same multi-repo:

```kts
dependencies {
    implementation(project(":libs:denovm"))
}
```

Runtime requirement: Deno must be available inside the runtime environment. The provided Dockerfile installs Deno and sets `PATH` accordingly. The service auto-discovers Deno via `DENO_INSTALL` or common paths.

---

## Spring configuration

All configuration is centralized in `nexxus.denovm.config.DenoVMProperties` (prefix `deno.vm`). Example:

```yaml
deno:
  vm:
    pool-enabled: true        # enable worker pool
    pool-size: 4              # number of persistent Deno workers
    timeout-seconds: 30       # per-request timeout
    pool-idle-kill-seconds: 120 # idle worker self-termination
```

No code changes required—`DenoVMServiceImpl` reads these properties.

---

## Java API

Types:
- `DenoVMRequest`
- `DenoVMResult`
- `DenoVMService` (Spring bean)

Usage:

```java
@Autowired DenoVMService denoVMService;

DenoVMRequest request = new DenoVMRequest(
    txnId,           // id
    jsCodeString,    // code (string contents of your JS module)
    credentialsMap,  // Map<String, String>
    payloadMap,      // Map<String, Object>
    urls,            // DenoVMRequest.DenoVMUrls with server redirect/webhook
    stepName         // exported async function name in code
);

DenoVMResult result = denoVMService.executeCode(request);
if (result.isSuccess()) {
    // handle success
} else {
    // handle error result.getError()
}
```

---

## Authoring JS handlers (contract)

Your code string must be a JavaScript module that exports an async function with the name provided in `request.step`. Signature: `(ctx, sdk) => Promise<object>`.

Context (`ctx`):
- `id`: string
- `credential`: Map<string, string>
- `data`: any
- `urls`: `{ server: { redirect, webhook, successRedirectUrl, failureRedirectUrl } }`

SDK (`sdk`):
- `logger`: `debug|info|warn|error(msg)`
- `http`: `get|post|put|patch|delete(url, { headers, body })` → `{ success, status, headers, data }`
- `encoding`: `base64`, `base64url`
- `response`: factories: `success(data)`, `failure(message, status?)`, `redirect(url, data?)`

Return value must be an object with `__type`: one of `success`, `failure`, `redirect`.

Example:

```javascript
export async function initiate(ctx, sdk) {
    sdk.logger.info({ id: ctx.id, msg: "starting" });
    const res = await sdk.http.post(ctx.urls.server.webhook, {
        headers: { "content-type": "application/json" },
        body: JSON.stringify({ txn: ctx.id, data: ctx.data })
    });
    if (!res.success) return sdk.response.failure("Webhook failed", res.status);
    return sdk.response.success({ ok: true });
}
```

---

## Security and sandboxing

- Deno flags: `--allow-net`, `--allow-read=<temp_dir>`, `--no-prompt`, `--no-check`, `--v8-flags=--max-old-space-size=64`
- Timeout enforced by Java per request
- In worker mode, each request is still isolated by module import boundaries, but runs in a shared process—use single-run if stronger isolation is required
- Recommended: enforce CPU/memory limits at the container level and restrict egress via network policy

---

## Performance guidance

- Single-run mode is simplest but includes process spawn overhead
- Enable worker mode (`pool-enabled: true`) for lower p95/p99 latency
- Tune `pool-size` based on CPU cores and traffic patterns
- Prefer small modules; move large dependencies behind network calls or pre-bundled code

---

## Troubleshooting

- "VM execution timeout": increase `timeout-seconds`, profile handler, or reduce external dependencies
- "deno not found": ensure Deno is installed and available via PATH or `DENO_INSTALL`
- JSON parse errors: ensure your handler returns via `sdk.response.*` helpers
- Inspect stderr: worker logs are captured in server logs with `[deno-stderr]` prefix

---

## Versioning and compatibility

- Built and tested with Java 21, Gradle 8.x, Spring Boot 3.x
- Deno is installed at runtime; keep your base image up to date

---

## Extending

- Add metrics around `DenoVMServiceImpl` and `DenoWorkerPool` (queue depth, durations, restarts)
- Switch to data-URL imports to avoid temporary files (if script size is small)
- Add per-environment memory caps and timeouts via properties profiles


