package connxt.psp.service.filter;

import java.util.List;
import java.util.Map;

import connxt.fee.dto.FeeDto;
import connxt.psp.entity.Psp;
import connxt.request.dto.RequestInputDto;
import connxt.riskrule.dto.RiskRuleDto;
import connxt.routingrule.dto.RoutingRuleDto;
import connxt.transactionlimit.dto.TransactionLimitDto;

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
