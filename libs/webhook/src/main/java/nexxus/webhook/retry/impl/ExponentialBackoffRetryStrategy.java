package nexxus.webhook.retry.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import nexxus.shared.constants.WebhookExecutionStatus;
import nexxus.webhook.dto.WebhookResponse;
import nexxus.webhook.retry.RetryStrategy;

/** Exponential backoff retry strategy implementation. */
@Component
public class ExponentialBackoffRetryStrategy implements RetryStrategy {

  @Value("${webhook.integration.max-retry-delay-seconds}")
  private long maxRetryDelaySeconds;

  @Value("${webhook.integration.retry-multiplier}")
  private double retryMultiplier;

  @Override
  public boolean shouldRetry(WebhookResponse response, int currentAttempt, int maxRetries) {
    // Don't retry if we've exceeded max attempts
    if (currentAttempt >= maxRetries) {
      return false;
    }

    // Don't retry if execution was successful
    if (response.getIsSuccess() != null && response.getIsSuccess()) {
      return false;
    }

    // Retry for failed executions
    return WebhookExecutionStatus.FAILED.equals(response.getExecutionStatus());
  }

  @Override
  public long calculateRetryDelay(int currentAttempt) {
    long delay = (long) (Math.pow(retryMultiplier, currentAttempt) * 10); // Start with 10 seconds
    return Math.min(delay, maxRetryDelaySeconds);
  }
}
