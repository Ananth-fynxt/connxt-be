package nexxus.psp.service.filter.strategy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import nexxus.psp.dto.IdNameDto;
import nexxus.psp.entity.Psp;
import nexxus.psp.service.filter.PspFilterContext;
import nexxus.psp.service.filter.PspFilterStrategy;
import nexxus.riskrule.dto.RiskRuleDto;
import nexxus.shared.constants.RiskAction;
import nexxus.shared.constants.RiskCustomerCriteriaType;
import nexxus.shared.constants.RiskType;
import nexxus.transaction.service.TransactionCalculationService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RiskRuleFilterStrategy implements PspFilterStrategy {

  private final TransactionCalculationService transactionCalculationService;

  @Override
  public PspFilterContext apply(PspFilterContext context) {
    List<Psp> currentPsps = context.getFilteredPsps();
    List<RiskRuleDto> riskRules = context.getRiskRules();

    if (CollectionUtils.isEmpty(riskRules)) {
      return context;
    }

    List<String> blockedPspIds = new ArrayList<>();
    List<String> alertPspIds = new ArrayList<>();

    for (RiskRuleDto rule : riskRules) {
      if (isRiskRuleApplicable(context, rule)) {
        List<String> affectedPspIds =
            rule.getPsps().stream().map(IdNameDto::getId).collect(Collectors.toList());

        if (RiskAction.BLOCK.equals(rule.getAction())) {
          blockedPspIds.addAll(affectedPspIds);
        } else if (RiskAction.ALERT.equals(rule.getAction())) {
          alertPspIds.addAll(affectedPspIds);
          // TODO: Implement email notification for ALERT action
        }
      }
    }

    List<Psp> filteredPsps =
        currentPsps.stream()
            .filter(psp -> !blockedPspIds.contains(psp.getId()))
            .collect(Collectors.toList());

    context.updateFilteredPsps(filteredPsps);
    context.addFilterMetadata("risk_rule_blocked_psps", blockedPspIds);
    context.addFilterMetadata("risk_rule_alert_psps", alertPspIds);

    return context;
  }

  private boolean isRiskRuleApplicable(PspFilterContext context, RiskRuleDto rule) {
    if (!rule.getFlowActionId().equals(context.getRequest().getActionId())
        || !rule.getCurrency().equals(context.getRequest().getCurrency())) {
      return false;
    }

    if (RiskType.DEFAULT.equals(rule.getType())) {
      return isDefaultRuleApplicable(context, rule);
    }

    if (RiskType.CUSTOMER.equals(rule.getType())) {
      return isCustomerRuleApplicable(context, rule);
    }

    return false;
  }

  private boolean isDefaultRuleApplicable(PspFilterContext context, RiskRuleDto rule) {
    try {
      LocalDateTime currentTime = LocalDateTime.now();
      LocalDateTime startTime =
          transactionCalculationService.getStartTimeForDuration(rule.getDuration(), currentTime);
      LocalDateTime endTime = transactionCalculationService.getEndTimeForDuration(currentTime);

      BigDecimal totalAmount = BigDecimal.ZERO;
      for (IdNameDto psp : rule.getPsps()) {
        BigDecimal pspAmount =
            transactionCalculationService.calculateTotalAmount(
                psp.getId(),
                context.getRequest().getBrandId(),
                context.getRequest().getEnvironmentId(),
                context.getRequest().getCustomerId(),
                rule.getFlowActionId(),
                rule.getCurrency(),
                startTime,
                endTime);
        totalAmount = totalAmount.add(pspAmount);
      }

      BigDecimal currentAmount = context.getRequest().getAmount();
      BigDecimal totalWithCurrent = totalAmount.add(currentAmount);

      boolean exceedsLimit = totalWithCurrent.compareTo(rule.getMaxAmount()) > 0;

      return exceedsLimit;
    } catch (Exception e) {
      return false;
    }
  }

  private boolean isCustomerRuleApplicable(PspFilterContext context, RiskRuleDto rule) {
    if (!isCustomerCriteriaValid(context, rule)) {
      return false;
    }

    try {
      LocalDateTime currentTime = LocalDateTime.now();
      LocalDateTime startTime =
          transactionCalculationService.getStartTimeForDuration(rule.getDuration(), currentTime);
      LocalDateTime endTime = transactionCalculationService.getEndTimeForDuration(currentTime);

      BigDecimal customerTotalAmount =
          calculateCustomerTotalAmountByCriteria(context, rule, startTime, endTime);

      BigDecimal currentAmount = context.getRequest().getAmount();
      BigDecimal totalWithCurrent = customerTotalAmount.add(currentAmount);

      boolean exceedsLimit = totalWithCurrent.compareTo(rule.getMaxAmount()) > 0;

      return exceedsLimit;
    } catch (Exception e) {
      return false;
    }
  }

  private boolean isCustomerCriteriaValid(PspFilterContext context, RiskRuleDto rule) {
    if (rule.getCriteriaType() == null
        || rule.getCriteriaValue() == null
        || rule.getCriteriaValue().isEmpty()) {
      return true;
    }

    String requestCustomerTag = context.getRequest().getCustomerTag();
    String requestCustomerAccountType = context.getRequest().getCustomerAccountType();

    if (RiskCustomerCriteriaType.TAG.equals(rule.getCriteriaType())) {
      boolean isValid =
          StringUtils.hasText(requestCustomerTag)
              && rule.getCriteriaValue().stream()
                  .anyMatch(
                      criteriaValue ->
                          criteriaValue.toLowerCase().equals(requestCustomerTag.toLowerCase()));
      return isValid;
    }

    if (RiskCustomerCriteriaType.ACCOUNT_TYPE.equals(rule.getCriteriaType())) {
      boolean isValid =
          StringUtils.hasText(requestCustomerAccountType)
              && rule.getCriteriaValue().stream()
                  .anyMatch(
                      criteriaValue ->
                          criteriaValue
                              .toLowerCase()
                              .equals(requestCustomerAccountType.toLowerCase()));
      return isValid;
    }

    return true;
  }

  @Override
  public int getPriority() {
    return 1; // Highest priority - apply risk rules first
  }

  private BigDecimal calculateCustomerTotalAmountByCriteria(
      PspFilterContext context, RiskRuleDto rule, LocalDateTime startTime, LocalDateTime endTime) {

    if (RiskCustomerCriteriaType.TAG.equals(rule.getCriteriaType())) {
      // Use customer tag for calculation
      return transactionCalculationService.calculateCustomerTotalAmount(
          context.getRequest().getCustomerTag(),
          null, // Don't send account type when using tag
          context.getRequest().getBrandId(),
          context.getRequest().getEnvironmentId(),
          rule.getFlowActionId(),
          rule.getCurrency(),
          startTime,
          endTime);
    } else if (RiskCustomerCriteriaType.ACCOUNT_TYPE.equals(rule.getCriteriaType())) {
      // Use customer account type for calculation
      return transactionCalculationService.calculateCustomerTotalAmount(
          null, // Don't send tag when using account type
          context.getRequest().getCustomerAccountType(),
          context.getRequest().getBrandId(),
          context.getRequest().getEnvironmentId(),
          rule.getFlowActionId(),
          rule.getCurrency(),
          startTime,
          endTime);
    } else {
      // Fallback to customer ID if no specific criteria
      return transactionCalculationService.calculateCustomerTotalAmount(
          context.getRequest().getCustomerId(),
          context.getRequest().getBrandId(),
          context.getRequest().getEnvironmentId(),
          rule.getFlowActionId(),
          rule.getCurrency(),
          startTime,
          endTime);
    }
  }

  @Override
  public String getStrategyName() {
    return "RiskRuleFilter";
  }

  @Override
  public boolean shouldApply(PspFilterContext context) {
    return !CollectionUtils.isEmpty(context.getRiskRules());
  }
}
