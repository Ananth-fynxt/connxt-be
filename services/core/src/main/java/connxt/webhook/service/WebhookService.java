package connxt.webhook.service;

import java.util.List;

import connxt.webhook.dto.WebhookDto;

public interface WebhookService {

  WebhookDto create(WebhookDto webhookDto);

  List<WebhookDto> readAll(String brandId, String environmentId);

  WebhookDto read(String id);

  WebhookDto update(String id, WebhookDto webhookDto);

  void delete(String id);
}
