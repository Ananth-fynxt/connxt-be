package nexxus.psp.service.filter;

import java.util.List;
import java.util.Map;

import nexxus.fee.dto.FeeDto;
import nexxus.psp.entity.Psp;
import nexxus.request.dto.RequestInputDto;
import nexxus.riskrule.dto.RiskRuleDto;
import nexxus.routingrule.dto.RoutingRuleDto;
import nexxus.transactionlimit.dto.TransactionLimitDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PspFilterContext {

  private RequestInputDto request;
  private List<Psp> originalPsps;
  private List<Psp> filteredPsps;
  private List<RiskRuleDto> riskRules;
  private List<TransactionLimitDto> transactionLimits;
  private List<RoutingRuleDto> routingRules;
  private List<FeeDto> feeRules;
  private Map<String, Object> filterMetadata;

  public static PspFilterContext initialize(RequestInputDto request, List<Psp> psps) {
    return PspFilterContext.builder()
        .request(request)
        .originalPsps(psps)
        .filteredPsps(psps)
        .filterMetadata(Map.of())
        .build();
  }

  public void updateFilteredPsps(List<Psp> newFilteredPsps) {
    this.filteredPsps = newFilteredPsps;
  }

  public void addFilterMetadata(String key, Object value) {
    if (filterMetadata == null) {
      filterMetadata = Map.of();
    }
    var newMetadata = new java.util.HashMap<>(filterMetadata);
    newMetadata.put(key, value);
    this.filterMetadata = newMetadata;
  }
}
