package nexxus.webhook.entity;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import nexxus.shared.constants.IdPrefix;
import nexxus.shared.util.RandomIdGenerator;

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
