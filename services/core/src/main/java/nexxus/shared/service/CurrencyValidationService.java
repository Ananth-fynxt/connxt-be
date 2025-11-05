package nexxus.shared.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import nexxus.shared.builder.dto.ApiResponse;
import nexxus.shared.dto.ValidationResult;

public interface CurrencyValidationService {

  boolean validatePspCurrencySupportBoolean(
      String currency,
      List<String> pspIds,
      String brandId,
      String environmentId,
      String flowActionId);

  ResponseEntity<ApiResponse<Object>> validatePspCurrencySupport(
      String currency,
      List<String> pspIds,
      String brandId,
      String environmentId,
      String flowActionId);

  ValidationResult validateCurrencySupportWithFlowTargetFallback(
      String currency,
      List<String> pspIds,
      String brandId,
      String environmentId,
      String flowActionId);

  List<String> getUnsupportedPsps(
      String currency,
      List<String> pspIds,
      String brandId,
      String environmentId,
      String flowActionId);

  Boolean validateFlowTargetCurrencySupport(
      String currency, String brandId, String environmentId, String flowActionId);
}
