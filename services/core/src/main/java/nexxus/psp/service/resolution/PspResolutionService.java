package nexxus.psp.service.resolution;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import nexxus.psp.entity.Psp;
import nexxus.psp.repository.PspRepository;
import nexxus.psp.service.filter.PspFilterContext;
import nexxus.psp.service.filter.PspFilterPipeline;
import nexxus.request.dto.RequestInputDto;
import nexxus.riskrule.dto.RiskRuleDto;
import nexxus.riskrule.service.RiskRuleService;
import nexxus.routingrule.dto.RoutingRuleDto;
import nexxus.routingrule.service.RoutingRuleService;
import nexxus.shared.constants.RiskAction;
import nexxus.shared.constants.Status;
import nexxus.transactionlimit.dto.TransactionLimitDto;
import nexxus.transactionlimit.service.TransactionLimitService;

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
