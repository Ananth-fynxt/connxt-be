package connxt.denovm.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "deno.vm")
public class DenoVMProperties {

  /** Enable pooled Deno worker mode */
  private boolean poolEnabled = true;

  /** Number of persistent Deno workers */
  private int poolSize = 2;

  /** Per-request timeout in seconds */
  private int timeoutSeconds = 30;

  /** Idle time in seconds before a worker self-terminates */
  private int poolIdleKillSeconds = 120;

  /** Max concurrent in-flight tasks per worker */
  private int workerConcurrency = 4;

  /** Auto-recycle worker after this many tasks (0 to disable) */
  private int poolMaxTasksPerWorker = 5000;

  /** Enable janitor to clean stale temp files */
  private boolean poolJanitorEnabled = true;

  /** Janitor interval seconds */
  private int poolJanitorIntervalSeconds = 60;

  /** Delete temp files older than this TTL (seconds) */
  private int poolJanitorFileTtlSeconds = 300;

  /** Deno V8 max old space size in MB */
  private int v8MaxOldSpaceMb = 64;

  /** Optional explicit path to the Deno executable */
  private String executable;
}
