package connxt.webhook.entity;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import connxt.shared.constants.IdPrefix;
import connxt.shared.util.RandomIdGenerator;

public class WebhookLogIdGenerator extends RandomIdGenerator implements IdentifierGenerator {
  @Override
  public Object generate(
      SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {

    if (o instanceof WebhookLog) {
      WebhookLog webhookLog = (WebhookLog) o;
      if (webhookLog.getId() != null && !webhookLog.getId().isEmpty()) {
        return webhookLog.getId();
      }
    }

    return generateId(IdPrefix.WEBHOOK_LOG);
  }
}
