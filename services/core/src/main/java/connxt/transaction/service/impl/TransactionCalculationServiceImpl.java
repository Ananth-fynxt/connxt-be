package connxt.transaction.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import connxt.shared.constants.RiskDuration;
import connxt.transaction.service.TransactionCalculationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionCalculationServiceImpl implements TransactionCalculationService {

  @Override
  public BigDecimal calculateTotalAmount(
      String pspId,
      String brandId,
      String environmentId,
      String flowActionId,
      String currency,
      LocalDateTime startTime,
      LocalDateTime endTime) {
    // Amount and currency fields don't exist in transaction table - return zero
    return BigDecimal.ZERO;
  }

  @Override
  public BigDecimal calculateTotalAmount(
      String pspId,
      String brandId,
      String environmentId,
      String customerId,
      String flowActionId,
      String currency,
      LocalDateTime startTime,
      LocalDateTime endTime) {
    // Amount, currency, and customer fields don't exist in transaction table - return zero
    return BigDecimal.ZERO;
  }

  @Override
  public BigDecimal calculateCustomerTotalAmount(
      String customerId,
      String brandId,
      String environmentId,
      String flowActionId,
      String currency,
      LocalDateTime startTime,
      LocalDateTime endTime) {
    // Amount, currency, and customer fields don't exist in transaction table - return zero
    return BigDecimal.ZERO;
  }

  @Override
  public BigDecimal calculateCustomerTotalAmount(
      String customerTag,
      String customerAccountType,
      String brandId,
      String environmentId,
      String flowActionId,
      String currency,
      LocalDateTime startTime,
      LocalDateTime endTime) {
    // Amount, currency, and customer fields don't exist in transaction table - return zero
    return BigDecimal.ZERO;
  }

  @Override
  public LocalDateTime getStartTimeForDuration(RiskDuration duration, LocalDateTime currentTime) {
    return switch (duration) {
      case HOUR -> currentTime.minusHours(1);
      case DAY -> currentTime.minusDays(1);
      case WEEK -> currentTime.minusWeeks(1);
      case MONTH -> currentTime.minusMonths(1);
    };
  }

  @Override
  public LocalDateTime getEndTimeForDuration(LocalDateTime currentTime) {
    return currentTime;
  }
}
