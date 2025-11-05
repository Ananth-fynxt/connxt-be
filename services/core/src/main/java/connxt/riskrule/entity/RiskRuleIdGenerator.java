package connxt.riskrule.entity;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import connxt.shared.constants.IdPrefix;
import connxt.shared.util.RandomIdGenerator;

public class RiskRuleIdGenerator extends RandomIdGenerator implements IdentifierGenerator {
  @Override
  public Object generate(
      SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {

    if (o instanceof RiskRule) {
      RiskRule rule = (RiskRule) o;
      if (rule.getRiskRuleId() != null
          && rule.getRiskRuleId().getId() != null
          && !rule.getRiskRuleId().getId().isEmpty()) {
        return rule.getRiskRuleId().getId();
      }
    }
    return generateId(IdPrefix.RISK_RULE);
  }
}
