package nexxus.cron;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import nexxus.cron.dto.CronJobRequest;
import nexxus.cron.dto.CronJobRequest.JobType;
import nexxus.cron.executor.CronExecutor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CronService {

  private final CronExecutor cronExecutor;

  @Value("${cron.job.default-max-retries:3}")
  private int defaultMaxRetries;

  public void executeSimpleJob() {
    log.info("Executing a simple background job using CronExecutor");

    CronJobRequest request =
        CronJobRequest.builder()
            .jobId("simple-job-" + System.currentTimeMillis())
            .correlationId("corr-" + System.currentTimeMillis())
            .attemptNumber(1)
            .maxRetries(defaultMaxRetries)
            .description("Simple background task")
            .jobType(JobType.RUNNABLE)
            .runnableTask(this::processSimpleTask)
            .build();

    cronExecutor.execute(request);
  }

  public void executeDelayedJob() {
    log.info("Executing a delayed job using CronExecutor");

    CronJobRequest request =
        CronJobRequest.builder()
            .jobId("delayed-job-" + System.currentTimeMillis())
            .correlationId("corr-" + System.currentTimeMillis())
            .attemptNumber(1)
            .maxRetries(defaultMaxRetries)
            .description("Delayed background task")
            .jobType(JobType.RUNNABLE)
            .runnableTask(this::processDelayedTask)
            .build();

    cronExecutor.execute(request);
  }

  public void executeRecurringJob() {
    log.info("Executing a recurring job using CronExecutor");

    CronJobRequest request =
        CronJobRequest.builder()
            .jobId("recurring-job-" + System.currentTimeMillis())
            .correlationId("corr-" + System.currentTimeMillis())
            .attemptNumber(1)
            .maxRetries(defaultMaxRetries)
            .description("Recurring background task")
            .jobType(JobType.RUNNABLE)
            .runnableTask(this::processRecurringTask)
            .build();

    cronExecutor.execute(request);
  }

  private void processSimpleTask() {
    log.info("Processing simple background task");
    try {
      Thread.sleep(1000);
      log.info("Simple task completed successfully");
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      log.error("Simple task interrupted");
    }
  }

  private void processDelayedTask() {
    log.info("Processing delayed background task");
    try {
      Thread.sleep(2000);
      log.info("Delayed task completed successfully");
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      log.error("Delayed task interrupted");
    }
  }

  private void processRecurringTask() {
    log.info("Processing recurring background task");
    try {
      Thread.sleep(1500);
      log.info("Recurring task completed successfully");
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      log.error("Recurring task interrupted");
    }
  }
}
