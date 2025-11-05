package nexxus.riskrule.service.util;

import org.springframework.stereotype.Component;

import nexxus.shared.constants.IdPrefix;
import nexxus.shared.util.RandomIdGenerator;

@Component
public class RiskRuleIdGeneratorUtil extends RandomIdGenerator {

  public String generateRiskRuleId() {
    return generateId(IdPrefix.RISK_RULE);
  }
}
