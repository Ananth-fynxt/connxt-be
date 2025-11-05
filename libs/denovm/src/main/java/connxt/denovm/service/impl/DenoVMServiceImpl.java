package connxt.denovm.service.impl;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import connxt.denovm.config.DenoVMProperties;
import connxt.denovm.dto.DenoVMExecutionContext;
import connxt.denovm.dto.DenoVMRequest;
import connxt.denovm.dto.DenoVMResult;
import connxt.denovm.enums.DenoExecutorMode;
import connxt.denovm.pool.DenoProcessFactory;
import connxt.denovm.pool.DenoWorkerPool;
import connxt.denovm.service.DenoVMService;
import connxt.denovm.service.mappers.DenoVMMapper;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** Simplified Deno VM service implementation Executes VM code using Deno subprocess */
@Slf4j
@Service
@RequiredArgsConstructor
public class DenoVMServiceImpl implements DenoVMService {

  private final ObjectMapper objectMapper;
  private final DenoVMMapper denoVMMapper;
  private final DenoVMProperties properties;
  private volatile DenoWorkerPool workerPool;
  private static volatile String cachedExecutorPath = null;
  private static final Object lock = new Object();

  @Override
  public DenoVMResult executeCode(DenoVMRequest request) {
    try {
      log.info("🔧 Executing VM code for ID: {}", request.getId());

      if (properties.isPoolEnabled()) {
        return executeViaPool(request);
      } else {
        // Create temporary file for the code
        Path tempFile = createTempFile(request.getCode());
        try {
          return executeDenoSubprocess(tempFile, request);
        } finally {
          Files.deleteIfExists(tempFile);
        }
      }
    } catch (Exception e) {
      log.error("❌ VM execution failed for ID: {}", request.getId(), e);
      return DenoVMResult.error("VM execution failed: " + e.getMessage());
    }
  }

  @Override
  public CompletableFuture<DenoVMResult> executeCodeAsync(DenoVMRequest request) {
    return CompletableFuture.supplyAsync(() -> executeCode(request));
  }

  private Path createTempFile(String code) throws IOException {
    Path tempFile = Files.createTempFile("deno-vm-", ".js");
    Files.write(tempFile, code.getBytes(StandardCharsets.UTF_8));
    return tempFile;
  }

  private DenoVMResult executeDenoSubprocess(Path scriptFile, DenoVMRequest request)
      throws Exception {
    // Create execution context using mapper
    DenoVMExecutionContext context =
        denoVMMapper.toExecutionContext(request, scriptFile.toString());

    String contextJson = objectMapper.writeValueAsString(context);

    // Get Deno executable path
    String denoPath = getDenoExecutablePath();

    // Build Deno command
    List<String> command =
        DenoProcessFactory.buildCommand(
            denoPath,
            Paths.get(context.file).getParent(),
            getVMExecutorPath(),
            DenoExecutorMode.SINGLE,
            properties.getV8MaxOldSpaceMb());
    ProcessBuilder builder = new ProcessBuilder(command);

    builder.directory(new File(System.getProperty("user.dir")));
    log.debug("🚀 Starting Deno subprocess with script: {}", context.file);

    Process process = builder.start();

    // Send execution context to stdin
    try (OutputStream stdin = process.getOutputStream()) {
      stdin.write(contextJson.getBytes(StandardCharsets.UTF_8));
      stdin.flush();
    }

    // Read output and error streams
    StringBuilder output = new StringBuilder();
    StringBuilder error = new StringBuilder();

    try (BufferedReader reader =
            new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader errorReader =
            new BufferedReader(new InputStreamReader(process.getErrorStream()))) {

      String line;
      while ((line = reader.readLine()) != null) {
        output.append(line).append("\n");
      }

      while ((line = errorReader.readLine()) != null) {
        error.append(line).append("\n");
      }
    }

    // Wait for process completion with timeout
    boolean completed = process.waitFor(30, TimeUnit.SECONDS);

    if (!completed) {
      process.destroyForcibly();
      log.error("⏰ VM execution timeout");
      return DenoVMResult.error("VM execution timeout");
    }

    int exitCode = process.exitValue();

    if (exitCode != 0) {
      log.error("❌ VM execution failed with exit code {}: {}", exitCode, error.toString());
      return DenoVMResult.error("VM execution failed: " + error.toString());
    }

    // Parse the result
    try {
      String resultJson = output.toString().trim();
      log.debug("📤 VM execution result: {}", resultJson);

      DenoVMResult result = objectMapper.readValue(resultJson, DenoVMResult.class);
      log.info("✅ VM execution completed successfully");
      return result;

    } catch (Exception e) {
      log.error("❌ Failed to parse VM execution result", e);
      return DenoVMResult.error(
          "Failed to parse result: " + e.getMessage() + " Raw: " + output.toString());
    }
  }

  private DenoVMResult executeViaPool(DenoVMRequest request) throws Exception {
    ensurePool();

    // Write code to a stable temp directory that pool can read
    Path tempDir = getOrCreatePoolTempDir();
    Path tempFile = Files.createTempFile(tempDir, "deno-vm-", ".js");
    Files.write(tempFile, request.getCode().getBytes(StandardCharsets.UTF_8));

    try {
      DenoVMExecutionContext context =
          denoVMMapper.toExecutionContext(request, tempFile.toString());
      JsonNode ctxNode = objectMapper.valueToTree(context);
      CompletableFuture<JsonNode> future = workerPool.submit(ctxNode);

      JsonNode resp = future.get(properties.getTimeoutSeconds(), TimeUnit.SECONDS);

      boolean success = resp.path("success").asBoolean(false);
      if (success) {
        JsonNode data = resp.path("data");
        JsonNode meta = resp.path("meta");
        @SuppressWarnings("unchecked")
        Map<String, Object> metaMap =
            meta.isMissingNode() || meta.isNull()
                ? Map.of()
                : objectMapper.convertValue(meta, Map.class);
        Object dataObj =
            data.isMissingNode() ? null : objectMapper.convertValue(data, Object.class);
        return DenoVMResult.success(dataObj, metaMap);
      } else {
        JsonNode err = resp.path("error");
        String errStr = err.isMissingNode() || err.isNull() ? "Unknown error" : err.toString();
        return DenoVMResult.error(errStr);
      }
    } catch (TimeoutException te) {
      return DenoVMResult.error("VM execution timeout");
    } finally {
      try {
        Files.deleteIfExists(tempFile);
      } catch (Exception ignored) {
      }
    }
  }

  private synchronized void ensurePool() throws IOException {
    if (workerPool != null) return;
    String denoPath = getDenoExecutablePath();
    String executorPath = getVMExecutorPoolPath();
    log.info(
        "Starting Deno worker pool size={} deno={} exec={}",
        properties.getPoolSize(),
        denoPath,
        executorPath);
    workerPool =
        new DenoWorkerPool(
            objectMapper,
            denoPath,
            executorPath,
            properties.getPoolSize(),
            Duration.ofSeconds(properties.getPoolIdleKillSeconds()),
            properties.getWorkerConcurrency(),
            properties.getPoolMaxTasksPerWorker(),
            properties.getV8MaxOldSpaceMb());
    startPoolJanitorIfEnabled();
  }

  @PreDestroy
  public void onShutdown() {
    try {
      if (workerPool != null) {
        workerPool.close();
        log.info("🧹 Deno worker pool closed gracefully");
      }
    } catch (Exception e) {
      log.warn("Failed to close Deno worker pool cleanly", e);
    }
  }

  private void startPoolJanitorIfEnabled() {
    if (!properties.isPoolJanitorEnabled()) return;
    Path dir = Paths.get(System.getProperty("java.io.tmpdir"), "deno-vm-pool");
    int interval = Math.max(10, properties.getPoolJanitorIntervalSeconds());
    int ttl = Math.max(60, properties.getPoolJanitorFileTtlSeconds());
    Thread janitor =
        new Thread(
            () -> {
              while (true) {
                try {
                  Files.createDirectories(dir);
                  long now = System.currentTimeMillis();
                  try (var stream = Files.list(dir)) {
                    stream
                        .filter(p -> Files.isRegularFile(p))
                        .forEach(
                            p -> {
                              try {
                                long ageMs = now - Files.getLastModifiedTime(p).toMillis();
                                if (ageMs > ttl * 1000L) {
                                  Files.deleteIfExists(p);
                                }
                              } catch (Exception ignored) {
                              }
                            });
                  }
                } catch (Exception ignored) {
                }
                try {
                  Thread.sleep(interval * 1000L);
                } catch (InterruptedException e) {
                  Thread.currentThread().interrupt();
                  break;
                }
              }
            },
            "deno-vm-pool-janitor");
    janitor.setDaemon(true);
    janitor.start();
  }

  private Path getOrCreatePoolTempDir() throws IOException {
    // Align with worker allow-read directory
    Path base = Paths.get(System.getProperty("java.io.tmpdir"), "deno-vm-pool");
    if (!Files.exists(base)) Files.createDirectories(base);
    return base;
  }

  private String getVMExecutorPath() {
    if (cachedExecutorPath != null && Files.exists(Paths.get(cachedExecutorPath))) {
      return cachedExecutorPath;
    }

    synchronized (lock) {
      if (cachedExecutorPath != null && Files.exists(Paths.get(cachedExecutorPath))) {
        return cachedExecutorPath;
      }

      try {
        // First try to find the script in the file system (for development)
        String resourcePath = "libs/denovm/src/main/resources/connxt/denovm/subprocess-executor.js";
        Path executorPath = Paths.get(System.getProperty("user.dir"), resourcePath);

        if (Files.exists(executorPath)) {
          log.debug("📁 Found VM executor script in file system: {}", executorPath);
          cachedExecutorPath = executorPath.toString();
          return cachedExecutorPath;
        }

        log.warn(
            "VM executor not found at dev path ({}), falling back to JAR extraction.",
            executorPath);
        // Extract from JAR resources to temporary file
        cachedExecutorPath = extractExecutorFromJar();
        return cachedExecutorPath;

      } catch (Exception e) {
        log.error("❌ Failed to get VM executor script", e);
        throw new RuntimeException("Could not find or extract VM executor script", e);
      }
    }
  }

  private String getVMExecutorPoolPath() {
    // Single source of truth: use the same executor; pooled mode toggled by script args
    return getVMExecutorPath();
  }

  private String extractExecutorFromJar() throws IOException {
    Path tempExecutorFile = Files.createTempFile("deno-vm-executor-", ".js");

    try (InputStream resourceStream =
        getClass().getClassLoader().getResourceAsStream("connxt/denovm/subprocess-executor.js")) {

      if (resourceStream == null) {
        throw new IOException("VM executor script not found in classpath resources");
      }

      Files.copy(resourceStream, tempExecutorFile, StandardCopyOption.REPLACE_EXISTING);
      log.debug("📁 Extracted VM executor script to temporary file: {}", tempExecutorFile);

      try {
        tempExecutorFile.toFile().setExecutable(true);
      } catch (Exception e) {
        log.warn(
            "⚠️ Could not set executable permissions for VM executor script: {}", e.getMessage());
      }

      return tempExecutorFile.toString();
    }
  }

  /**
   * Finds the Deno executable path by checking multiple locations
   *
   * @return Full path to Deno executable
   */
  private String getDenoExecutablePath() {
    // Prefer explicitly configured executable if provided
    try {
      if (properties.getExecutable() != null && !properties.getExecutable().isBlank()) {
        Path p = Paths.get(properties.getExecutable());
        if (Files.exists(p)) {
          log.debug("🔍 Using configured Deno executable: {}", p);
          return p.toString();
        } else {
          log.warn("Configured Deno executable not found at: {}. Falling back to auto-detect.", p);
        }
      }
    } catch (Exception ignored) {
    }
    // Check if DENO_INSTALL environment variable is set (Docker environment)
    String denoInstall = System.getenv("DENO_INSTALL");
    if (denoInstall != null && !denoInstall.isEmpty()) {
      Path denoPath = Paths.get(denoInstall, "bin", "deno");
      if (Files.exists(denoPath)) {
        log.debug("🔍 Found Deno at: {}", denoPath);
        return denoPath.toString();
      }
    }

    // Check common installation paths
    String[] possiblePaths = {
      System.getProperty("user.home") + "/.deno/bin/deno",
      "/home/connxt/.deno/bin/deno",
      "/usr/local/bin/deno",
      "/usr/bin/deno"
    };

    for (String path : possiblePaths) {
      if (Files.exists(Paths.get(path))) {
        log.debug("🔍 Found Deno at: {}", path);
        return path;
      }
    }

    // Fallback to just "deno" and let PATH resolution handle it
    log.debug("🔍 Using 'deno' from PATH");
    return "deno";
  }
}
