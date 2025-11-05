package connxt.psp.service.resolution;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import connxt.psp.entity.Psp;
import connxt.psp.repository.PspRepository;
import connxt.psp.service.filter.PspFilterContext;
import connxt.psp.service.filter.PspFilterPipeline;
import connxt.request.dto.RequestInputDto;
import connxt.riskrule.dto.RiskRuleDto;
import connxt.riskrule.service.RiskRuleService;
import connxt.routingrule.dto.RoutingRuleDto;
import connxt.routingrule.service.RoutingRuleService;
import connxt.shared.constants.RiskAction;
import connxt.shared.constants.Status;
import connxt.transactionlimit.dto.TransactionLimitDto;
import connxt.transactionlimit.service.TransactionLimitService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PspResolutionService {

  private final PspRepository pspRepository;
  private final PspFilterPipeline pspFilterPipeline;
  private final RiskRuleService riskRuleService;
  private final TransactionLimitService transactionLimitService;
  private final RoutingRuleService routingRuleService;

  public PspResolutionResult resolvePsps(RequestInputDto request) {
    var fetchResult = fetchGlobalPsps(request);
    List<Psp> globalPsps = fetchResult.psps();
    String fetchStrategy = fetchResult.strategy();
    boolean requiresConversion = fetchResult.requiresConversion();

    if (globalPsps.isEmpty()) {
      return PspResolutionResult.builder()
          .filteredPsps(List.of())
          .globalPsps(List.of())
          .riskRules(List.of())
          .feeRules(List.of())
          .transactionLimits(List.of())
          .resolvedByStrategy("GlobalPspResolution")
          .usedRoutingRuleRefinement(false)
          .requiresCurrencyConversion(false)
          .fetchStrategy("NONE")
          .build();
    }

    return applyCompleteFilterPipeline(request, globalPsps, fetchStrategy, requiresConversion);
  }

  private FetchResult fetchGlobalPsps(RequestInputDto request) {
    List<Psp> psps =
        pspRepository.findActivePspsByBrandEnvironmentActionAndCurrency(
            request.getBrandId(),
            request.getEnvironmentId(),
            request.getActionId(),
            request.getCurrency());

    if (!psps.isEmpty()) {
      return new FetchResult(psps, "CURRENCY_ACTION", false);
    }

    psps =
        pspRepository.findActivePspsByBrandEnvironmentAndAction(
            request.getBrandId(), request.getEnvironmentId(), request.getActionId());

    if (!psps.isEmpty()) {
      return new FetchResult(psps, "ACTION_ONLY", true);
    }

    return new FetchResult(List.of(), "NONE", false);
  }

  private record FetchResult(List<Psp> psps, String strategy, boolean requiresConversion) {}

  private PspResolutionResult applyCompleteFilterPipeline(
      RequestInputDto request, List<Psp> psps, String fetchStrategy, boolean requiresConversion) {

    List<String> pspIds = psps.stream().map(Psp::getId).collect(Collectors.toList());

    List<RiskRuleDto> riskRules =
        riskRuleService.readLatestEnabledRiskRulesByCriteria(
            pspIds,
            request.getBrandId(),
            request.getEnvironmentId(),
            request.getActionId(),
            request.getCurrency(),
            RiskAction.BLOCK,
            Status.ENABLED);

    List<TransactionLimitDto> transactionLimits =
        transactionLimitService.readLatestEnabledTransactionLimitsByCriteria(
            pspIds,
            request.getBrandId(),
            request.getEnvironmentId(),
            request.getActionId(),
            request.getCurrency(),
            Status.ENABLED);

    List<RoutingRuleDto> routingRules =
        routingRuleService.findEnabledRoutingRulesByBrandAndEnvironment(
            request.getBrandId(), request.getEnvironmentId());

    PspFilterContext filterContext = PspFilterContext.initialize(request, psps);
    filterContext.setRiskRules(riskRules);
    filterContext.setTransactionLimits(transactionLimits);
    filterContext.setRoutingRules(routingRules);
    filterContext.setFeeRules(List.of());

    PspFilterContext filteredContext = pspFilterPipeline.applyFilters(filterContext);

    return PspResolutionResult.builder()
        .filteredPsps(filteredContext.getFilteredPsps())
        .globalPsps(psps)
        .riskRules(riskRules)
        .feeRules(List.of())
        .transactionLimits(transactionLimits)
        .resolvedByStrategy("CompleteFilterPipeline")
        .usedRoutingRuleRefinement(!routingRules.isEmpty())
        .requiresCurrencyConversion(requiresConversion)
        .fetchStrategy(fetchStrategy)
        .build();
  }
}
