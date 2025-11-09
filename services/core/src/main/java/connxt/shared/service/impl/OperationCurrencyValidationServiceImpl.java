package connxt.shared.service.impl;

import org.springframework.stereotype.Service;

import connxt.shared.dto.OperationCurrencyValidationRequest;
import connxt.shared.dto.ValidationResult;
import connxt.shared.service.OperationCurrencyValidationService;

@Service
public class OperationCurrencyValidationServiceImpl implements OperationCurrencyValidationService {

  @Override
  public ValidationResult validateOperationCurrenciesAgainstFlowTarget(
      OperationCurrencyValidationRequest request) {
    return ValidationResult.success();
  }
}
