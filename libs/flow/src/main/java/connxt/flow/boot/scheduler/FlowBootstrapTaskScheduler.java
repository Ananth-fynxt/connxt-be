package connxt.flow.boot.scheduler;

/** Simple abstraction that allows the application to control when flow bootstrap tasks run. */
public interface FlowBootstrapTaskScheduler {

  /** Schedule the supplied task for execution. Implementations may run immediately or defer. */
  void schedule(Runnable task);
}
