package nexxus.shared.service;

import nexxus.shared.dto.OperationCurrencyValidationRequest;
import nexxus.shared.dto.ValidationResult;

public interface OperationCurrencyValidationService {

  ValidationResult validateOperationCurrenciesAgainstFlowTarget(
      OperationCurrencyValidationRequest request);
}
