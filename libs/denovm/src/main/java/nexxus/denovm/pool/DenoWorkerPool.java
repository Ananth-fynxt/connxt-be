package nexxus.denovm.pool;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DenoWorkerPool implements Closeable {

  private final List<DenoWorker> workers = new ArrayList<>();
  private final AtomicInteger rr = new AtomicInteger(0);
  private final ObjectMapper objectMapper;
  private final String denoExecutable;
  private final String executorScriptPath;
  private final Duration idleKillAfter;
  private final int workerConcurrency;
  private final long maxTasksBeforeRecycle;
  private final int v8MaxOldSpaceMb;

  public DenoWorkerPool(
      ObjectMapper objectMapper,
      String denoExecutable,
      String executorScriptPath,
      int size,
      Duration idleKillAfter,
      int workerConcurrency,
      long maxTasksBeforeRecycle,
      int v8MaxOldSpaceMb)
      throws IOException {

    this.objectMapper = objectMapper;
    this.denoExecutable = denoExecutable;
    this.executorScriptPath = executorScriptPath;
    this.idleKillAfter = idleKillAfter;
    this.workerConcurrency = workerConcurrency;
    this.maxTasksBeforeRecycle = maxTasksBeforeRecycle;
    this.v8MaxOldSpaceMb = v8MaxOldSpaceMb;

    if (size <= 0) size = 1;
    Path baseTmp = Paths.get(System.getProperty("java.io.tmpdir"), "deno-vm-pool");
    Files.createDirectories(baseTmp);

    for (int i = 0; i < size; i++) {
      // All workers allow-read the same base directory so scripts written there are accessible
      Path readableDir = baseTmp;
      DenoWorker w =
          new DenoWorker(
              this.objectMapper,
              this.denoExecutable,
              this.executorScriptPath,
              readableDir,
              this.idleKillAfter,
              this.workerConcurrency,
              this.maxTasksBeforeRecycle,
              this.v8MaxOldSpaceMb);
      w.start();
      workers.add(w);
    }
  }

  public CompletableFuture<JsonNode> submit(JsonNode context) throws IOException {
    Objects.requireNonNull(context, "context");
    int idx = Math.floorMod(rr.getAndIncrement(), workers.size());
    if (ThreadLocalRandom.current().nextInt(10) == 0) {
      idx = ThreadLocalRandom.current().nextInt(workers.size());
    }
    DenoWorker w = workers.get(idx);
    if (w == null || !w.isRunning()) {
      // Best-effort restart with same configuration as pool creation
      Path readableDir = workers.get(0).getReadableDir();
      w =
          new DenoWorker(
              this.objectMapper,
              this.denoExecutable,
              this.executorScriptPath,
              readableDir,
              this.idleKillAfter,
              this.workerConcurrency,
              this.maxTasksBeforeRecycle,
              this.v8MaxOldSpaceMb);
      try {
        w.start();
        workers.set(idx, w);
      } catch (IOException e) {
        throw e;
      }
    }
    return w.submit(context);
  }

  public Path getReadableDirFor(int workerIndex) {
    return workers.get(workerIndex).getReadableDir();
  }

  @Override
  public void close() {
    for (DenoWorker w : workers) {
      try {
        w.close();
      } catch (Exception ignored) {
      }
    }
  }
}
