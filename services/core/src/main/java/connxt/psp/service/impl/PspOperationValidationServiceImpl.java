package connxt.psp.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import connxt.psp.entity.Psp;
import connxt.psp.entity.PspOperation;
import connxt.psp.repository.PspOperationRepository;
import connxt.psp.service.PspOperationValidationService;
import connxt.request.dto.RequestInputDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PspOperationValidationServiceImpl implements PspOperationValidationService {

  private final PspOperationRepository pspOperationRepository;

  @Override
  public boolean isPspOperationValid(Psp psp, RequestInputDto request) {
    List<PspOperation> operations = pspOperationRepository.findByPspId(psp.getId());

    if (CollectionUtils.isEmpty(operations)) {
      log.debug("No operations found for PSP: {}", psp.getId());
      return false;
    }

    String requestCurrency = request.getCurrency();
    String requestCountry = request.getCountry();
    String requestActionId = request.getActionId();

    return operations.stream()
        .anyMatch(
            operation ->
                matchesOperationCriteria(
                    operation, requestCurrency, requestCountry, requestActionId));
  }

  @Override
  public List<Psp> filterValidPspOperations(List<Psp> psps, RequestInputDto request) {
    return psps.stream()
        .filter(psp -> isPspOperationValid(psp, request))
        .collect(Collectors.toList());
  }

  private boolean matchesOperationCriteria(
      PspOperation operation, String currency, String country, String actionId) {
    return matchesCurrency(operation, currency)
        && matchesCountry(operation, country)
        && matchesFlowAction(operation, actionId);
  }

  private boolean matchesCurrency(PspOperation operation, String requestCurrency) {
    if (requestCurrency == null) {
      return true;
    }
    return operation.getCurrencies() != null && operation.getCurrencies().contains(requestCurrency);
  }

  private boolean matchesCountry(PspOperation operation, String requestCountry) {
    if (requestCountry == null) {
      return true; // Country is optional, skip validation if not provided
    }
    // Only validate if PSP operation has countries configured
    if (operation.getCountries() == null || operation.getCountries().isEmpty()) {
      return true;
    }
    return operation.getCountries().contains(requestCountry);
  }

  private boolean matchesFlowAction(PspOperation operation, String requestActionId) {
    if (requestActionId == null) {
      return false;
    }
    return requestActionId.equals(operation.getFlowActionId());
  }
}
