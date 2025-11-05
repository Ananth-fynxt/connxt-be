package nexxus.webhook.executor;

import nexxus.webhook.dto.WebhookRequest;
import nexxus.webhook.dto.WebhookResponse;

public interface WebhookExecutor {

  WebhookResponse execute(WebhookRequest request);
}
