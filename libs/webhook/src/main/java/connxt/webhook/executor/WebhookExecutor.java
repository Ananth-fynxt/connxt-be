package connxt.webhook.executor;

import connxt.webhook.dto.WebhookRequest;
import connxt.webhook.dto.WebhookResponse;

public interface WebhookExecutor {

  WebhookResponse execute(WebhookRequest request);
}
