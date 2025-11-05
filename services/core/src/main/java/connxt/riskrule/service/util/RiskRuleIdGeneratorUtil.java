package connxt.riskrule.service.util;

import org.springframework.stereotype.Component;

import connxt.shared.constants.IdPrefix;
import connxt.shared.util.RandomIdGenerator;

@Component
public class RiskRuleIdGeneratorUtil extends RandomIdGenerator {

  public String generateRiskRuleId() {
    return generateId(IdPrefix.RISK_RULE);
  }
}
