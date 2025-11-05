package connxt.webhook.entity;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import connxt.shared.constants.IdPrefix;
import connxt.shared.util.RandomIdGenerator;

public class WebhookIdGenerator extends RandomIdGenerator implements IdentifierGenerator {
  @Override
  public Object generate(
      SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {

    if (o instanceof Webhook) {
      Webhook webhook = (Webhook) o;
      if (webhook.getId() != null && !webhook.getId().isEmpty()) {
        return webhook.getId();
      }
    }

    return generateId(IdPrefix.WEBHOOK);
  }
}
