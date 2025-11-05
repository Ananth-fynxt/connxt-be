package nexxus.cron.executor.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import nexxus.cron.dto.CronJobRequest;
import nexxus.cron.dto.CronJobResponse;
import nexxus.cron.executor.CronExecutor;
import nexxus.shared.constants.CronExecutionStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CronExecutorImpl implements CronExecutor {

  @Override
  public CronJobResponse execute(CronJobRequest request) {
    long startTime = System.currentTimeMillis();
    LocalDateTime executedAt = LocalDateTime.now();

    try {
      log.debug(
          "Executing cron job: jobId={}, jobType={}, attempt={}",
          request.getJobId(),
          request.getJobType(),
          request.getAttemptNumber());

      Object result = executeJob(request);
      long executionTime = System.currentTimeMillis() - startTime;

      return buildSuccessResponse(request, result, executionTime, executedAt);

    } catch (Exception e) {
      return handleError(request, e, startTime, executedAt);
    }
  }

  private Object executeJob(CronJobRequest request) {
    switch (request.getJobType()) {
      case RUNNABLE:
        return executeRunnableJob(request);
      case JOB_REQUEST:
        return executeJobRequest(request);
      default:
        throw new IllegalArgumentException("Unsupported job type: " + request.getJobType());
    }
  }

  private Object executeRunnableJob(CronJobRequest request) {
    log.info("Executing Runnable job: {}", request.getDescription());

    if (request.getRunnableTask() != null) {
      request.getRunnableTask().run();
      return "Runnable job completed successfully";
    } else {
      throw new IllegalStateException("Runnable task is null");
    }
  }

  private Object executeJobRequest(CronJobRequest request) {
    log.info("Executing JobRequest job: {}", request.getDescription());

    if (request.getJobRequest() != null) {
      return "JobRequest job scheduled successfully";
    } else {
      throw new IllegalStateException("JobRequest is null");
    }
  }

  private CronJobResponse buildSuccessResponse(
      CronJobRequest request, Object result, long executionTime, LocalDateTime executedAt) {

    return CronJobResponse.builder()
        .jobId(request.getJobId())
        .correlationId(request.getCorrelationId())
        .executionStatus(CronExecutionStatus.SUCCESS.getValue())
        .result(result)
        .executionTimeMs(executionTime)
        .attemptNumber(request.getAttemptNumber())
        .scheduledAt(request.getScheduledAt())
        .executedAt(executedAt)
        .completedAt(LocalDateTime.now())
        .jobType(request.getJobType())
        .description(request.getDescription())
        .isSuccess(true)
        .build();
  }

  private CronJobResponse handleError(
      CronJobRequest request, Exception e, long startTime, LocalDateTime executedAt) {
    long executionTime = System.currentTimeMillis() - startTime;

    log.error(
        "Cron job execution failed: jobId={}, error={}", request.getJobId(), e.getMessage(), e);

    return CronJobResponse.builder()
        .jobId(request.getJobId())
        .correlationId(request.getCorrelationId())
        .executionStatus(CronExecutionStatus.FAILED.getValue())
        .errorMessage(e.getMessage())
        .executionTimeMs(executionTime)
        .attemptNumber(request.getAttemptNumber())
        .scheduledAt(request.getScheduledAt())
        .executedAt(executedAt)
        .completedAt(LocalDateTime.now())
        .jobType(request.getJobType())
        .description(request.getDescription())
        .isSuccess(false)
        .build();
  }
}
