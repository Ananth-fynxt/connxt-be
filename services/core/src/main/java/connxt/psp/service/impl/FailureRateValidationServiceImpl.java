package connxt.psp.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import connxt.psp.entity.Psp;
import connxt.psp.service.FailureRateValidationService;
import connxt.request.dto.RequestInputDto;
import connxt.transaction.service.TransactionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FailureRateValidationServiceImpl implements FailureRateValidationService {

  private final TransactionService transactionService;

  @Override
  public boolean isFailureRateValid(Psp psp, RequestInputDto request) {
    if (!Boolean.TRUE.equals(psp.getFailureRate())) {
      return true;
    }

    Float failureRateThreshold = psp.getFailureRateThreshold();
    Integer failureRateDurationMinutes = psp.getFailureRateDurationMinutes();

    if (failureRateThreshold == null || failureRateDurationMinutes == null) {
      log.debug(
          "Failure rate monitoring enabled but threshold or duration not configured for PSP: {}",
          psp.getId());
      return true;
    }

    try {
      String requestFlowActionId = request.getActionId();
      String customerId = request.getCustomerId();
      LocalDateTime endTime = LocalDateTime.now();
      LocalDateTime startTime = endTime.minusMinutes(failureRateDurationMinutes);

      double currentFailureRate =
          transactionService.calculateFailureRateByCustomer(
              psp.getId(), customerId, requestFlowActionId, startTime, endTime);

      if (currentFailureRate > failureRateThreshold) {
        log.debug(
            "PSP {} customer {} failure rate {} exceeds threshold {} for flow action {} (duration: {} minutes), filtering out",
            psp.getId(),
            customerId,
            currentFailureRate,
            failureRateThreshold,
            requestFlowActionId,
            failureRateDurationMinutes);
        return false;
      }

      log.debug(
          "PSP {} customer {} failure rate {} is within threshold {} for flow action {} (duration: {} minutes), allowing",
          psp.getId(),
          customerId,
          currentFailureRate,
          failureRateThreshold,
          requestFlowActionId,
          failureRateDurationMinutes);

      return true;
    } catch (Exception e) {
      log.warn("Error calculating failure rate for PSP: {}, allowing request", psp.getId(), e);
      return true;
    }
  }

  @Override
  public List<Psp> filterValidFailureRates(List<Psp> psps, RequestInputDto request) {
    return psps.stream()
        .filter(psp -> isFailureRateValid(psp, request))
        .collect(Collectors.toList());
  }
}
