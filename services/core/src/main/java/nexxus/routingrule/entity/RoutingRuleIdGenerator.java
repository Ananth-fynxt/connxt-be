package nexxus.routingrule.entity;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import nexxus.shared.constants.IdPrefix;
import nexxus.shared.util.RandomIdGenerator;

public class RoutingRuleIdGenerator extends RandomIdGenerator implements IdentifierGenerator {
  @Override
  public Object generate(
      SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {

    if (o instanceof RoutingRule rule) {
      if (rule.getRoutingRuleId() != null
          && rule.getRoutingRuleId().getId() != null
          && !rule.getRoutingRuleId().getId().isEmpty()) {
        return rule.getRoutingRuleId().getId();
      }
    }

    return generateId(IdPrefix.ROUTING_RULE);
  }
}
