package nexxus.psp.service.resolution;

import java.util.List;

import nexxus.fee.dto.FeeDto;
import nexxus.psp.entity.Psp;
import nexxus.riskrule.dto.RiskRuleDto;
import nexxus.transactionlimit.dto.TransactionLimitDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PspResolutionResult {

  private List<Psp> filteredPsps;
  private List<Psp> globalPsps;
  private List<RiskRuleDto> riskRules;
  private List<FeeDto> feeRules;
  private List<TransactionLimitDto> transactionLimits;
  private String resolvedByStrategy;
  private String routingRuleId;
  private boolean usedRoutingRuleRefinement;
  private boolean requiresCurrencyConversion;
  private String fetchStrategy; // "CURRENCY_ACTION" or "ACTION_ONLY"

  public boolean isEmpty() {
    return filteredPsps == null || filteredPsps.isEmpty();
  }
}
