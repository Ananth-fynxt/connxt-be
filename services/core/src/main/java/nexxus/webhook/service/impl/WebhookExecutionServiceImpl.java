package nexxus.webhook.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import nexxus.shared.config.properties.WebhookProperties;
import nexxus.shared.constants.Status;
import nexxus.shared.constants.WebhookExecutionStatus;
import nexxus.webhook.dto.WebhookRequest;
import nexxus.webhook.dto.WebhookResponse;
import nexxus.webhook.entity.Webhook;
import nexxus.webhook.entity.WebhookLog;
import nexxus.webhook.executor.WebhookExecutor;
import nexxus.webhook.repository.WebhookLogRepository;
import nexxus.webhook.repository.WebhookRepository;
import nexxus.webhook.retry.RetryStrategy;
import nexxus.webhook.service.WebhookExecutionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookExecutionServiceImpl implements WebhookExecutionService {

  private final WebhookRepository webhookRepository;
  private final WebhookLogRepository webhookLogRepository;
  private final JobScheduler jobScheduler;
  private final WebhookExecutor webhookExecutor;
  private final RetryStrategy retryStrategy;
  private final ObjectMapper webhookObjectMapper;
  private final WebhookProperties webhookProperties;

  @Override
  public void sendWebhook(
      String brandId,
      String environmentId,
      nexxus.shared.constants.WebhookStatusType statusType,
      Object payload,
      String correlationId) {
    sendWebhook(brandId, environmentId, statusType, payload, correlationId, null);
  }

  @Override
  public void sendWebhook(
      String brandId,
      String environmentId,
      nexxus.shared.constants.WebhookStatusType statusType,
      Object payload,
      String correlationId,
      String webhookId) {

    List<Webhook> webhooks = findActiveWebhooks(brandId, environmentId, statusType, webhookId);

    for (Webhook webhook : webhooks) {
      try {
        enqueueWebhookJob(webhook, payload, correlationId, 0);
        log.info(
            "Enqueued webhook job for webhook ID: {}, correlation ID: {}",
            webhook.getId(),
            correlationId);
      } catch (Exception e) {
        log.error(
            "Failed to enqueue webhook job for webhook ID: {}, error: {}",
            webhook.getId(),
            e.getMessage(),
            e);
      }
    }
  }

  @Override
  public void sendWebhookById(String webhookId, Object payload, String correlationId) {
    Optional<Webhook> webhookOpt = webhookRepository.findById(webhookId);

    if (webhookOpt.isEmpty()) {
      log.warn("Webhook not found with ID: {}", webhookId);
      return;
    }

    Webhook webhook = webhookOpt.get();
    if (webhook.getStatus() != Status.ENABLED) {
      log.warn("Webhook is not enabled: {}", webhookId);
      return;
    }

    enqueueWebhookJob(webhook, payload, correlationId, 0);
  }

  @Job(name = "Webhook Execution Job", retries = 0)
  public void executeWebhook(
      String webhookId, String payloadJson, String correlationId, int attempt) {

    Optional<Webhook> webhookOpt = webhookRepository.findById(webhookId);
    if (webhookOpt.isEmpty()) {
      log.error("Webhook not found during execution: {}", webhookId);
      return;
    }

    Webhook webhook = webhookOpt.get();
    WebhookLog webhookLog = createWebhookLog(webhook, payloadJson, correlationId, attempt);

    try {
      // Update log status to IN_PROGRESS
      webhookLog.setExecutionStatus(WebhookExecutionStatus.IN_PROGRESS);
      webhookLog.setExecutedAt(LocalDateTime.now());

      // Create webhook request for the integration library
      WebhookRequest request =
          WebhookRequest.builder()
              .webhookId(webhook.getId())
              .url(webhook.getUrl())
              .payload(payloadJson)
              .correlationId(correlationId)
              .attemptNumber(attempt + 1)
              .maxRetries(webhook.getRetry())
              .timeoutMs((int) webhookProperties.getDefaultTimeout())
              .scheduledAt(webhookLog.getScheduledAt())
              .jobId(webhookLog.getJobId())
              .build();

      // Execute webhook using the integration library
      WebhookResponse response = webhookExecutor.execute(request);

      // Update webhook log with response details
      updateWebhookLogFromResponse(webhookLog, response);

      // Check if retry is needed
      if (retryStrategy.shouldRetry(response, attempt, webhook.getRetry())) {
        scheduleRetry(webhook, payloadJson, correlationId, attempt);
        webhookLog.setExecutionStatus(WebhookExecutionStatus.RETRIED);
      }

      log.info(
          "Webhook executed: webhookId={}, attempt={}, status={}, time={}ms, timeout={}ms",
          webhookId,
          attempt,
          response.getExecutionStatus(),
          response.getExecutionTimeMs(),
          webhookProperties.getDefaultTimeout());

    } catch (Exception e) {
      log.error(
          "Unexpected error during webhook execution: webhookId={}, error={}",
          webhookId,
          e.getMessage(),
          e);
      webhookLog.setExecutionStatus(WebhookExecutionStatus.FAILED);
      webhookLog.setErrorMessage("Unexpected error: " + e.getMessage());
      webhookLog.setCompletedAt(LocalDateTime.now());
    } finally {
      // Save the webhook log only once at the end
      webhookLogRepository.save(webhookLog);
    }
  }

  @Override
  public Map<String, Object> getWebhookStats(String webhookId) {
    Optional<Webhook> webhookOpt = webhookRepository.findById(webhookId);
    if (webhookOpt.isEmpty()) {
      return Map.of("error", "Webhook not found");
    }

    Object[] stats = webhookLogRepository.getSuccessRateStats(webhookId);
    long totalAttempts = (Long) stats[0];
    long successfulAttempts = (Long) stats[1];

    double successRate = totalAttempts > 0 ? (double) successfulAttempts / totalAttempts * 100 : 0;

    return Map.of(
        "webhookId", webhookId,
        "totalAttempts", totalAttempts,
        "successfulAttempts", successfulAttempts,
        "failedAttempts", totalAttempts - successfulAttempts,
        "successRate", String.format("%.2f%%", successRate));
  }

  @Override
  public List<WebhookLog> getWebhookLogs(String webhookId, int page, int size) {
    return webhookLogRepository
        .findByWebhookIdOrderByCreatedAtDesc(
            webhookId, org.springframework.data.domain.PageRequest.of(page, size))
        .getContent();
  }

  private List<Webhook> findActiveWebhooks(
      String brandId,
      String environmentId,
      nexxus.shared.constants.WebhookStatusType statusType,
      String webhookId) {
    if (webhookId != null) {
      Optional<Webhook> webhookOpt = webhookRepository.findById(webhookId);
      return webhookOpt
          .filter(w -> w.getStatus() == Status.ENABLED)
          .map(List::of)
          .orElse(List.of());
    } else {
      return webhookRepository.findByBrandIdAndEnvironmentIdAndStatusTypeAndStatus(
          brandId, environmentId, statusType, Status.ENABLED);
    }
  }

  private void enqueueWebhookJob(
      Webhook webhook, Object payload, String correlationId, int attempt) {
    String payloadJson = toJson(payload);

    jobScheduler.enqueue(
        () -> executeWebhook(webhook.getId(), payloadJson, correlationId, attempt));
  }

  private void scheduleRetry(
      Webhook webhook, String payloadJson, String correlationId, int currentAttempt) {
    long delaySeconds = retryStrategy.calculateRetryDelay(currentAttempt);
    int nextAttempt = currentAttempt + 1;

    jobScheduler.schedule(
        LocalDateTime.now().plusSeconds(delaySeconds),
        () -> executeWebhook(webhook.getId(), payloadJson, correlationId, nextAttempt));

    log.info(
        "Scheduled webhook retry: webhookId={}, attempt={}, delay={}s",
        webhook.getId(),
        nextAttempt,
        delaySeconds);
  }

  private WebhookLog createWebhookLog(
      Webhook webhook, String payloadJson, String correlationId, int attempt) {
    return WebhookLog.builder()
        .webhookId(webhook.getId())
        .requestPayload(payloadJson)
        .attemptNumber(attempt + 1)
        .executionStatus(WebhookExecutionStatus.PENDING)
        .correlationId(correlationId)
        .scheduledAt(LocalDateTime.now())
        .build();
  }

  private void updateWebhookLogFromResponse(WebhookLog webhookLog, WebhookResponse response) {
    webhookLog.setExecutionTimeMs(response.getExecutionTimeMs());
    webhookLog.setResponseStatus(response.getResponseStatus());
    webhookLog.setResponsePayload(toValidJson(response.getResponsePayload()));
    webhookLog.setErrorMessage(response.getErrorMessage());
    webhookLog.setIsSuccess(response.getIsSuccess());
    webhookLog.setExecutionStatus(response.getExecutionStatus());
    webhookLog.setCompletedAt(response.getCompletedAt());

    // Convert response headers to JSON
    if (response.getResponseHeaders() != null) {
      webhookLog.setResponseHeaders(toJson(response.getResponseHeaders()));
    }
  }

  private String toJson(Object obj) {
    try {
      return webhookObjectMapper.writeValueAsString(obj);
    } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
      log.error("Failed to serialize object to JSON", e);
      return "{}";
    }
  }

  private String toValidJson(String payload) {
    if (payload == null || payload.trim().isEmpty()) {
      return null;
    }

    try {
      webhookObjectMapper.readTree(payload);
      return payload;
    } catch (Exception e) {
      try {
        return webhookObjectMapper.writeValueAsString(Map.of("response", payload));
      } catch (com.fasterxml.jackson.core.JsonProcessingException ex) {
        return "{}";
      }
    }
  }
}
