package connxt.shared.service.impl;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import connxt.flowtarget.repository.FlowTargetRepository;
import connxt.shared.builder.dto.ApiResponse;
import connxt.shared.constants.ErrorCode;
import connxt.shared.dto.OperationCurrencyValidationRequest;
import connxt.shared.dto.ValidationResult;
import connxt.shared.service.OperationCurrencyValidationService;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of operation currency validation service. Validates operation currencies against
 * flow target supported currencies.
 */
@Service
@RequiredArgsConstructor
public class OperationCurrencyValidationServiceImpl implements OperationCurrencyValidationService {

  private final FlowTargetRepository flowTargetRepository;

  @Override
  public ValidationResult validateOperationCurrenciesAgainstFlowTarget(
      OperationCurrencyValidationRequest request) {
    if (request.getOperations() == null || request.getOperations().isEmpty()) {
      return ValidationResult.success();
    }

    return flowTargetRepository
        .findById(request.getFlowTargetId())
        .map(
            flowTarget -> {
              List<String> supportedCurrencies = flowTarget.getCurrencies();
              if (supportedCurrencies == null || supportedCurrencies.isEmpty()) {
                ResponseEntity<ApiResponse<Object>> errorResponse =
                    ResponseEntity.badRequest()
                        .body(
                            ApiResponse.error(
                                ErrorCode.PSP_CURRENCY_NOT_SUPPORTED.getCode(),
                                "Flow target does not support any currencies"));
                return ValidationResult.failure(errorResponse);
              }

              List<String> validationErrors = new java.util.ArrayList<>();

              for (OperationCurrencyValidationRequest.PspOperation operation :
                  request.getOperations()) {
                if (operation.getCurrencies() != null) {
                  for (OperationCurrencyValidationRequest.CurrencyInfo currency :
                      operation.getCurrencies()) {
                    String currencyCode = currency.getCurrency();
                    if (!supportedCurrencies.contains(currencyCode)) {
                      validationErrors.add(
                          String.format(
                              "Currency '%s' in flow action '%s' is not supported by flow target '%s'. Supported currencies: %s",
                              currencyCode,
                              operation.getFlowActionId(),
                              request.getFlowTargetId(),
                              String.join(", ", supportedCurrencies)));
                    }
                  }
                }
              }

              if (!validationErrors.isEmpty()) {
                String errorMessage =
                    "Currency validation failed:\n" + String.join("\n", validationErrors);
                ResponseEntity<ApiResponse<Object>> errorResponse =
                    ResponseEntity.badRequest()
                        .body(
                            ApiResponse.error(
                                ErrorCode.PSP_CURRENCY_NOT_SUPPORTED.getCode(), errorMessage));
                return ValidationResult.failure(errorResponse);
              }

              return ValidationResult.success();
            })
        .orElseGet(
            () -> {
              ResponseEntity<ApiResponse<Object>> errorResponse =
                  ResponseEntity.badRequest()
                      .body(
                          ApiResponse.error(
                              ErrorCode.PSP_CONFIGURATION_ERROR.getCode(),
                              "Flow target not found: " + request.getFlowTargetId()));
              return ValidationResult.failure(errorResponse);
            });
  }
}
