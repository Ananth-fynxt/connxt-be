package connxt.shared.service;

import connxt.shared.dto.OperationCurrencyValidationRequest;
import connxt.shared.dto.ValidationResult;

public interface OperationCurrencyValidationService {

  ValidationResult validateOperationCurrenciesAgainstFlowTarget(
      OperationCurrencyValidationRequest request);
}
