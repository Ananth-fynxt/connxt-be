package connxt.riskrule.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiskRulePspId implements Serializable {
  private String riskRuleId;
  private Integer riskRuleVersion;
  private String pspId;
}
