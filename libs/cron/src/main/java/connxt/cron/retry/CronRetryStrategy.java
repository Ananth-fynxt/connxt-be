package connxt.cron.retry;

import connxt.cron.dto.CronJobResponse;

public interface CronRetryStrategy {

  boolean shouldRetry(CronJobResponse response, int currentAttempt, int maxRetries);

  long calculateRetryDelay(int currentAttempt);
}
