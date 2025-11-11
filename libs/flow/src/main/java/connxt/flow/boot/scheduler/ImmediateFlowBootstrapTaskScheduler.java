package connxt.flow.boot.scheduler;

import java.util.concurrent.CompletableFuture;

/**
 * Default scheduler that simply runs the task asynchronously. Applications can override the
 * scheduler bean to hook into custom job systems.
 */
public class ImmediateFlowBootstrapTaskScheduler implements FlowBootstrapTaskScheduler {

  @Override
  public void schedule(Runnable task) {
    CompletableFuture.runAsync(task);
  }
}
