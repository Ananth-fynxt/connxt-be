package nexxus.denovm.pool;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import nexxus.denovm.enums.DenoExecutorMode;

public final class DenoProcessFactory {

  private DenoProcessFactory() {}

  public static List<String> buildCommand(
      String denoExecutable,
      Path allowReadDir,
      String executorScriptPath,
      DenoExecutorMode mode,
      int v8MaxOldSpaceMb) {

    List<String> cmd = new ArrayList<>();
    cmd.add(denoExecutable);
    cmd.add("run");
    cmd.add("--allow-net");
    cmd.add("--allow-read=" + allowReadDir.toAbsolutePath());
    cmd.add("--no-prompt");
    cmd.add("--no-check");

    if (v8MaxOldSpaceMb > 0) {
      cmd.add("--v8-flags=--max-old-space-size=" + v8MaxOldSpaceMb);
    }
    cmd.add(executorScriptPath);

    if (mode == DenoExecutorMode.WORKER) {
      cmd.add("deno-vm-worker");
    }

    return cmd;
  }
}
