package connxt.psp.service.filter.strategy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import connxt.psp.entity.Psp;
import connxt.psp.service.filter.PspFilterContext;
import connxt.psp.service.filter.PspFilterStrategy;
import connxt.routingrule.dto.RoutingRuleDto;
import connxt.shared.validators.ConditionValidator;
import connxt.transaction.service.RoutingCalculationService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RoutingRuleFilterStrategy implements PspFilterStrategy {

  private final RoutingCalculationService routingCalculationService;
  private final ConditionValidator conditionValidator;

  @Override
  public PspFilterContext apply(PspFilterContext context) {
    List<Psp> currentPsps = context.getFilteredPsps();
    List<RoutingRuleDto> routingRules = context.getRoutingRules();

    if (CollectionUtils.isEmpty(routingRules) || CollectionUtils.isEmpty(currentPsps)) {
      return context;
    }

    // Step 1: Get routing calculation data in single DB call
    LocalDateTime currentTime = LocalDateTime.now();
    LocalDateTime startTime =
        routingCalculationService.getStartTimeForDuration(
            routingRules.get(0).getDuration(), currentTime);
    LocalDateTime endTime = routingCalculationService.getEndTimeForDuration(currentTime);

    Map<String, RoutingCalculationService.RoutingCalculationResult> calculationResults =
        routingCalculationService.calculateRoutingThresholds(
            routingRules,
            context.getRequest().getBrandId(),
            context.getRequest().getEnvironmentId(),
            context.getRequest().getActionId(),
            context.getRequest().getCurrency(),
            startTime,
            endTime);

    // Step 2: Filter PSPs based on routing rule conditions and thresholds
    List<Psp> finalPsps =
        applyRoutingRulesWithCalculations(context, currentPsps, routingRules, calculationResults);

    context.updateFilteredPsps(finalPsps);
    context.addFilterMetadata("routing_rule_filtered_count", currentPsps.size() - finalPsps.size());

    return context;
  }

  private List<Psp> applyRoutingRulesWithCalculations(
      PspFilterContext context,
      List<Psp> currentPsps,
      List<RoutingRuleDto> routingRules,
      Map<String, RoutingCalculationService.RoutingCalculationResult> calculationResults) {

    List<Psp> finalPsps = new ArrayList<>();

    for (RoutingRuleDto rule : routingRules) {
      if (isRoutingRuleApplicable(context, rule)) {
        List<Psp> rulePsps =
            applyRoutingTypeValidationWithCalculations(
                context, rule, currentPsps, calculationResults);

        if (!rulePsps.isEmpty()) {
          finalPsps.addAll(rulePsps);
          break;
        }
      }
    }

    return finalPsps.isEmpty() ? currentPsps : finalPsps;
  }

  private List<Psp> applyRoutingTypeValidationWithCalculations(
      PspFilterContext context,
      RoutingRuleDto rule,
      List<Psp> availablePsps,
      Map<String, RoutingCalculationService.RoutingCalculationResult> calculationResults) {

    if (rule.getRoutingType() == null) {
      List<String> rulePspIds =
          rule.getPsps().stream().map(p -> p.getPspId()).collect(Collectors.toList());

      return availablePsps.stream()
          .filter(psp -> rulePspIds.contains(psp.getId()))
          .collect(Collectors.toList());
    }

    List<Psp> validPsps = new ArrayList<>();

    for (var pspInfo : rule.getPsps()) {
      String pspId = pspInfo.getPspId();
      Psp psp =
          availablePsps.stream().filter(p -> p.getId().equals(pspId)).findFirst().orElse(null);

      if (psp == null) continue;

      if (isPspWithinThresholdWithCalculations(
          rule, pspId, BigDecimal.valueOf(pspInfo.getPspValue()), calculationResults)) {
        validPsps.add(psp);
      }
    }

    return validPsps;
  }

  private boolean isPspWithinThresholdWithCalculations(
      RoutingRuleDto rule,
      String pspId,
      BigDecimal thresholdValue,
      Map<String, RoutingCalculationService.RoutingCalculationResult> calculationResults) {

    try {
      RoutingCalculationService.RoutingCalculationResult result = calculationResults.get(pspId);
      if (result == null) {
        return true;
      }

      switch (rule.getRoutingType()) {
        case AMOUNT:
          return result.totalAmount().compareTo(thresholdValue) <= 0;
        case PERCENTAGE:
          return result.percentage().compareTo(thresholdValue) <= 0;
        case COUNT:
          return BigDecimal.valueOf(result.transactionCount()).compareTo(thresholdValue) <= 0;
        default:
          return true;
      }
    } catch (Exception e) {
      return false;
    }
  }

  private boolean isRoutingRuleApplicable(PspFilterContext context, RoutingRuleDto rule) {
    try {
      if (rule.getConditionJson() == null) {
        return true;
      }

      Map<String, Object> evaluationContext = new HashMap<>();
      evaluationContext.put("currency", context.getRequest().getCurrency());
      evaluationContext.put("country", context.getRequest().getCountry());

      boolean result = conditionValidator.matches(rule.getConditionJson(), evaluationContext);

      return result;
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public int getPriority() {
    return 4; // Fourth priority - apply after psp configuration filters
  }

  @Override
  public String getStrategyName() {
    return "RoutingRuleFilter";
  }

  @Override
  public boolean shouldApply(PspFilterContext context) {
    return !CollectionUtils.isEmpty(context.getRoutingRules());
  }
}
