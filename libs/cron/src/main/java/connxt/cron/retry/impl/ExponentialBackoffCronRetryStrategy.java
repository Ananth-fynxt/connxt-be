package connxt.cron.retry.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import connxt.cron.dto.CronJobResponse;
import connxt.cron.retry.CronRetryStrategy;
import connxt.shared.constants.CronExecutionStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ExponentialBackoffCronRetryStrategy implements CronRetryStrategy {

  @Value("${cron.retry.base-delay-seconds:1}")
  private long baseDelaySeconds;

  @Value("${cron.retry.max-delay-seconds:300}")
  private long maxDelaySeconds;

  @Value("${cron.retry.backoff-multiplier:2.0}")
  private double backoffMultiplier;

  @Override
  public boolean shouldRetry(CronJobResponse response, int currentAttempt, int maxRetries) {
    if (currentAttempt >= maxRetries) {
      log.debug(
          "Max retries reached for job {}: attempt={}, maxRetries={}",
          response.getJobId(),
          currentAttempt,
          maxRetries);
      return false;
    }

    if (CronExecutionStatus.SUCCESS.getValue().equals(response.getExecutionStatus())) {
      log.debug("Job {} succeeded, no retry needed", response.getJobId());
      return false;
    }

    if (CronExecutionStatus.FAILED.getValue().equals(response.getExecutionStatus())) {
      log.debug(
          "Job {} failed, retry needed: attempt={}, maxRetries={}",
          response.getJobId(),
          currentAttempt,
          maxRetries);
      return true;
    }

    return false;
  }

  @Override
  public long calculateRetryDelay(int currentAttempt) {
    long delay = (long) (baseDelaySeconds * Math.pow(backoffMultiplier, currentAttempt - 1));
    long cappedDelay = Math.min(delay, maxDelaySeconds);

    log.debug("Calculated retry delay for attempt {}: {} seconds", currentAttempt, cappedDelay);
    return cappedDelay;
  }
}
