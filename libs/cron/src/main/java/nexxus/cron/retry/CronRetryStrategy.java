package nexxus.cron.retry;

import nexxus.cron.dto.CronJobResponse;

public interface CronRetryStrategy {

  boolean shouldRetry(CronJobResponse response, int currentAttempt, int maxRetries);

  long calculateRetryDelay(int currentAttempt);
}
