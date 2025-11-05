package connxt.psp.service.filter.strategy;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import connxt.psp.entity.Psp;
import connxt.psp.service.filter.PspFilterContext;
import connxt.psp.service.filter.PspFilterStrategy;
import connxt.transactionlimit.dto.TransactionLimitDto;
import connxt.transactionlimit.dto.TransactionLimitPspActionDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionLimitFilterStrategy implements PspFilterStrategy {

  @Override
  public PspFilterContext apply(PspFilterContext context) {
    List<Psp> currentPsps = context.getFilteredPsps();
    List<TransactionLimitDto> transactionLimits = context.getTransactionLimits();

    if (CollectionUtils.isEmpty(transactionLimits)) {
      return context;
    }

    BigDecimal transactionAmount = context.getRequest().getAmount();
    String requestCurrency = context.getRequest().getCurrency();
    String requestCountry = context.getRequest().getCountry();
    String requestActionId = context.getRequest().getActionId();
    String requestCustomerTag = context.getRequest().getCustomerTag();

    if (transactionAmount == null || requestCurrency == null) {
      return context;
    }

    List<Psp> filteredPsps =
        currentPsps.stream()
            .filter(
                psp ->
                    transactionLimits.stream()
                        .anyMatch(
                            limit ->
                                isTransactionAllowed(
                                    limit,
                                    transactionAmount,
                                    requestCurrency,
                                    requestCountry,
                                    requestActionId,
                                    requestCustomerTag)))
            .collect(Collectors.toList());

    context.updateFilteredPsps(filteredPsps);
    context.addFilterMetadata(
        "transaction_limit_filtered_count", currentPsps.size() - filteredPsps.size());

    return context;
  }

  private boolean isTransactionAllowed(
      TransactionLimitDto limit,
      BigDecimal transactionAmount,
      String requestCurrency,
      String requestCountry,
      String requestActionId,
      String requestCustomerTag) {

    if (!requestCurrency.equals(limit.getCurrency())) {
      return false;
    }

    if (!CollectionUtils.isEmpty(limit.getCountries())
        && !limit.getCountries().stream().anyMatch(country -> country.equals(requestCountry))) {
      return false;
    }

    if (!CollectionUtils.isEmpty(limit.getCustomerTags())
        && requestCustomerTag != null
        && !limit.getCustomerTags().stream()
            .anyMatch(tag -> tag.toLowerCase().equals(requestCustomerTag.toLowerCase()))) {
      return false;
    }

    boolean hasValidAction =
        limit.getPspActions().stream()
            .anyMatch(pspAction -> isPspActionValid(pspAction, transactionAmount, requestActionId));

    return hasValidAction;
  }

  private boolean isPspActionValid(
      TransactionLimitPspActionDto pspAction,
      BigDecimal transactionAmount,
      String requestActionId) {

    if (!pspAction.getFlowActionId().equals(requestActionId)) {
      return false;
    }

    BigDecimal minAmount = pspAction.getMinAmount();
    BigDecimal maxAmount = pspAction.getMaxAmount();

    boolean withinMinLimit = minAmount == null || transactionAmount.compareTo(minAmount) >= 0;
    boolean withinMaxLimit = maxAmount == null || transactionAmount.compareTo(maxAmount) <= 0;

    return withinMinLimit && withinMaxLimit;
  }

  @Override
  public int getPriority() {
    return 2; // Second priority - apply after risk rules
  }

  @Override
  public String getStrategyName() {
    return "TransactionLimitFilter";
  }

  @Override
  public boolean shouldApply(PspFilterContext context) {
    return !CollectionUtils.isEmpty(context.getTransactionLimits())
        && context.getRequest().getAmount() != null;
  }
}
