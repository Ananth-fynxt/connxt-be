package connxt.webhook.retry;

import connxt.webhook.dto.WebhookResponse;

public interface RetryStrategy {

  boolean shouldRetry(WebhookResponse response, int currentAttempt, int maxRetries);

  long calculateRetryDelay(int currentAttempt);
}
