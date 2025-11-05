package nexxus.transaction.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import nexxus.shared.constants.RiskDuration;

public interface TransactionCalculationService {

  BigDecimal calculateTotalAmount(
      String pspId,
      String brandId,
      String environmentId,
      String flowActionId,
      String currency,
      LocalDateTime startTime,
      LocalDateTime endTime);

  BigDecimal calculateTotalAmount(
      String pspId,
      String brandId,
      String environmentId,
      String customerId,
      String flowActionId,
      String currency,
      LocalDateTime startTime,
      LocalDateTime endTime);

  BigDecimal calculateCustomerTotalAmount(
      String customerId,
      String brandId,
      String environmentId,
      String flowActionId,
      String currency,
      LocalDateTime startTime,
      LocalDateTime endTime);

  BigDecimal calculateCustomerTotalAmount(
      String customerTag,
      String customerAccountType,
      String brandId,
      String environmentId,
      String flowActionId,
      String currency,
      LocalDateTime startTime,
      LocalDateTime endTime);

  LocalDateTime getStartTimeForDuration(RiskDuration duration, LocalDateTime currentTime);

  LocalDateTime getEndTimeForDuration(LocalDateTime currentTime);
}
