package nexxus.transaction.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import nexxus.routingrule.dto.RoutingRuleDto;
import nexxus.shared.constants.RoutingDuration;

public interface RoutingCalculationService {

  Map<String, RoutingCalculationResult> calculateRoutingThresholds(
      List<RoutingRuleDto> routingRules,
      String brandId,
      String environmentId,
      String flowActionId,
      String currency,
      LocalDateTime startTime,
      LocalDateTime endTime);

  LocalDateTime getStartTimeForDuration(RoutingDuration duration, LocalDateTime currentTime);

  LocalDateTime getEndTimeForDuration(LocalDateTime currentTime);

  record RoutingCalculationResult(
      BigDecimal totalAmount, Long transactionCount, BigDecimal percentage) {}
}
