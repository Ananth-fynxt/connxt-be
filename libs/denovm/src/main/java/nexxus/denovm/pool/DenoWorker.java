package nexxus.denovm.pool;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DenoWorker implements Closeable {

  private static final AtomicInteger WORKER_SEQ = new AtomicInteger(0);

  private final ObjectMapper objectMapper;
  private final String denoExecutable;
  private final String executorScriptPath;
  private final Path readableDir;
  private final Duration idleKillAfter;
  private final int maxInFlight;
  private final long maxTasksBeforeRecycle;
  private final int v8MaxOldSpaceMb;
  private final String workerId;

  private final Map<String, CompletableFuture<JsonNode>> pending = new ConcurrentHashMap<>();
  private final AtomicBoolean running = new AtomicBoolean(false);
  private final Semaphore limiter;
  private final AtomicLong tasksProcessed = new AtomicLong(0);

  private Process process;
  private BufferedWriter stdin;

  private Thread stdoutThread;
  private Thread stderrThread;
  private Thread idleKillerThread;
  private volatile long lastActivityNanos;

  public DenoWorker(
      ObjectMapper objectMapper,
      String denoExecutable,
      String executorScriptPath,
      Path readableDir,
      Duration idleKillAfter,
      int maxInFlight,
      long maxTasksBeforeRecycle,
      int v8MaxOldSpaceMb) {
    this.objectMapper = objectMapper;
    this.denoExecutable = denoExecutable;
    this.executorScriptPath = executorScriptPath;
    this.readableDir = readableDir;
    this.idleKillAfter = idleKillAfter;
    this.maxInFlight = Math.max(1, maxInFlight);
    this.maxTasksBeforeRecycle = Math.max(0, maxTasksBeforeRecycle);
    this.limiter = new Semaphore(this.maxInFlight);
    this.v8MaxOldSpaceMb = v8MaxOldSpaceMb;
    this.workerId = String.valueOf(WORKER_SEQ.incrementAndGet());
  }

  public synchronized void start() throws IOException {
    if (running.get()) return;

    Files.createDirectories(readableDir);

    List<String> command =
        DenoProcessFactory.buildCommand(
            denoExecutable,
            readableDir,
            executorScriptPath,
            nexxus.denovm.enums.DenoExecutorMode.WORKER,
            v8MaxOldSpaceMb);
    ProcessBuilder builder = new ProcessBuilder(command);

    builder.directory(new File(System.getProperty("user.dir")));
    process = builder.start();
    stdin =
        new BufferedWriter(
            new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8));

    running.set(true);
    lastActivityNanos = System.nanoTime();

    stdoutThread = new Thread(this::pumpStdout, "deno-worker-" + workerId + "-stdout");
    stderrThread = new Thread(this::pumpStderr, "deno-worker-" + workerId + "-stderr");
    stdoutThread.setDaemon(true);
    stderrThread.setDaemon(true);
    stdoutThread.start();
    stderrThread.start();

    if (idleKillAfter != null && !idleKillAfter.isZero() && !idleKillAfter.isNegative()) {
      idleKillerThread = new Thread(this::idleKillerLoop, "deno-worker-idlekiller");
      idleKillerThread.setDaemon(true);
      idleKillerThread.start();
    }
  }

  public CompletableFuture<JsonNode> submit(JsonNode contextJson) throws IOException {
    if (!running.get()) throw new IllegalStateException("Worker not running");
    try {
      limiter.acquire();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IOException("Interrupted acquiring worker permit", e);
    }
    String id = requireId(contextJson);
    CompletableFuture<JsonNode> future = new CompletableFuture<>();
    pending.put(id, future);
    String line = contextJson.toString();
    synchronized (this) {
      stdin.write(line);
      stdin.write("\n");
      stdin.flush();
    }
    lastActivityNanos = System.nanoTime();
    return future.whenComplete(
        (r, t) -> {
          limiter.release();
          long done = tasksProcessed.incrementAndGet();
          if (maxTasksBeforeRecycle > 0 && done >= maxTasksBeforeRecycle) {
            try {
              log.info("Recycling Deno worker after tasksProcessed={}", done);
            } catch (Throwable ignored) {
            }
            shutdown();
          }
        });
  }

  private String requireId(JsonNode node) {
    JsonNode idNode = node.get("id");
    if (idNode == null || idNode.isNull()) throw new IllegalArgumentException("Context missing id");
    return Objects.toString(idNode.asText());
  }

  private void pumpStdout() {
    try (BufferedReader reader =
        new BufferedReader(
            new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
      String line;
      while ((line = reader.readLine()) != null) {
        lastActivityNanos = System.nanoTime();
        line = line.trim();
        if (line.isEmpty()) continue;
        try {
          JsonNode resp = objectMapper.readTree(line);
          JsonNode idNode = resp.get("id");
          String id = idNode != null && !idNode.isNull() ? idNode.asText() : null;
          if (id == null) {
            log.warn("Deno worker response without id: {}", line);
            continue;
          }
          CompletableFuture<JsonNode> future = pending.remove(id);
          if (future != null) {
            future.complete(resp);
          } else {
            log.warn("No pending future for response id={} line={}", id, line);
          }
        } catch (Exception e) {
          log.error("Failed to parse Deno worker stdout line: {}", line, e);
        }
      }
    } catch (IOException e) {
      log.debug("Deno worker stdout closed");
    } finally {
      shutdownPending("stdout-closed");
    }
  }

  private void pumpStderr() {
    try (BufferedReader reader =
        new BufferedReader(
            new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {
      String line;
      while ((line = reader.readLine()) != null) {
        log.debug("[deno-stderr] {}", line);
      }
    } catch (IOException e) {
      log.debug("Deno worker stderr closed");
    }
  }

  private void idleKillerLoop() {
    try {
      while (running.get()) {
        Thread.sleep(500);
        if (!pending.isEmpty()) continue;
        long idleNanos = System.nanoTime() - lastActivityNanos;
        if (idleNanos > idleKillAfter.toNanos()) {
          log.info("Deno worker idle timeout reached, shutting down");
          shutdown();
          break;
        }
      }
    } catch (InterruptedException ignored) {
    }
  }

  private void shutdownPending(String reason) {
    pending.forEach(
        (k, f) -> f.completeExceptionally(new IOException("Worker terminated: " + reason)));
    pending.clear();
  }

  public synchronized void shutdown() {
    if (!running.get()) return;
    try {
      try {
        String msg = objectMapper.createObjectNode().put("cmd", "shutdown").toString();
        stdin.write(msg);
        stdin.write("\n");
        stdin.flush();
      } catch (Exception ignored) {
      }
      if (!process.waitFor(2, java.util.concurrent.TimeUnit.SECONDS)) {
        process.destroyForcibly();
      }
    } catch (InterruptedException ignored) {
    } catch (Exception e) {
      log.debug("Graceful shutdown failed, destroying process");
    } finally {
      if (process != null) process.destroyForcibly();
      running.set(false);
    }
  }

  @Override
  public void close() {
    shutdown();
  }

  public boolean isRunning() {
    return running.get();
  }

  public Path getReadableDir() {
    return readableDir;
  }
}
