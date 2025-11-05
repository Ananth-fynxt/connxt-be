package nexxus.webhook.retry;

import nexxus.webhook.dto.WebhookResponse;

public interface RetryStrategy {

  boolean shouldRetry(WebhookResponse response, int currentAttempt, int maxRetries);

  long calculateRetryDelay(int currentAttempt);
}
