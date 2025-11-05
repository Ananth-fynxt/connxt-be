package nexxus.webhook.service;

import java.util.List;
import java.util.Map;

import nexxus.webhook.entity.WebhookLog;

public interface WebhookExecutionService {

  void sendWebhook(
      String brandId,
      String environmentId,
      nexxus.shared.constants.WebhookStatusType statusType,
      Object payload,
      String correlationId);

  void sendWebhook(
      String brandId,
      String environmentId,
      nexxus.shared.constants.WebhookStatusType statusType,
      Object payload,
      String correlationId,
      String webhookId);

  void sendWebhookById(String webhookId, Object payload, String correlationId);

  Map<String, Object> getWebhookStats(String webhookId);

  List<WebhookLog> getWebhookLogs(String webhookId, int page, int size);
}
