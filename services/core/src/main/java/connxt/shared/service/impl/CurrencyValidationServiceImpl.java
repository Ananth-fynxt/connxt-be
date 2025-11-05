package connxt.shared.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import connxt.psp.repository.CurrencyLimitRepository;
import connxt.shared.builder.dto.ApiResponse;
import connxt.shared.dto.ValidationResult;
import connxt.shared.service.CurrencyValidationService;
import connxt.shared.util.ValidationUtils;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of currency validation service. Uses the currency_limits table to validate PSP
 * currency support.
 */
@Service
@RequiredArgsConstructor
public class CurrencyValidationServiceImpl implements CurrencyValidationService {

  private final CurrencyLimitRepository currencyLimitRepository;

  @Override
  public boolean validatePspCurrencySupportBoolean(
      String currency,
      List<String> pspIds,
      String brandId,
      String environmentId,
      String flowActionId) {

    if (ValidationUtils.isNullOrEmpty(pspIds)) {
      return true;
    }

    for (String pspId : pspIds) {
      Boolean supportsCurrency =
          currencyLimitRepository.existsByCompositeKeyAndCurrency(
              brandId, environmentId, flowActionId, pspId, currency);
      if (supportsCurrency == null || !supportsCurrency) {
        return false;
      }
    }
    return true; // All PSPs support the currency
  }

  @Override
  public ResponseEntity<ApiResponse<Object>> validatePspCurrencySupport(
      String currency,
      List<String> pspIds,
      String brandId,
      String environmentId,
      String flowActionId) {

    Boolean allSupported =
        validatePspCurrencySupportBoolean(currency, pspIds, brandId, environmentId, flowActionId);

    if (allSupported == null || !allSupported) {
      List<String> unsupportedPsps =
          getUnsupportedPsps(currency, pspIds, brandId, environmentId, flowActionId);
      String errorMessage =
          "Currency "
              + currency
              + " is not supported by PSPs: "
              + String.join(", ", unsupportedPsps);
      return ResponseEntity.badRequest()
          .body(ApiResponse.error("CURRENCY_NOT_SUPPORTED", errorMessage));
    }
    return null; // Validation successful
  }

  @Override
  public ValidationResult validateCurrencySupportWithFlowTargetFallback(
      String currency,
      List<String> pspIds,
      String brandId,
      String environmentId,
      String flowActionId) {

    // First check PSP currency support using currency_limits table
    Boolean allPspSupported =
        validatePspCurrencySupportBoolean(currency, pspIds, brandId, environmentId, flowActionId);

    if (allPspSupported != null && allPspSupported) {
      // All PSPs support the currency, validation passes
      return ValidationResult.success();
    } else {
      // PSP validation failed, check if any PSPs support this currency via their flow targets
      List<String> supportedPspIds =
          currencyLimitRepository.findSupportedPspIdsByCurrency(
              brandId, environmentId, flowActionId, currency);

      // Check if any of the requested PSPs are in the supported list
      boolean anyPspSupports = pspIds.stream().anyMatch(supportedPspIds::contains);

      if (!anyPspSupports) {
        // None of the requested PSPs support this currency
        String errorMessage =
            "Currency " + currency + " is not supported by any of the specified PSPs";
        ResponseEntity<ApiResponse<Object>> errorResponse =
            ResponseEntity.badRequest()
                .body(ApiResponse.error("CURRENCY_NOT_SUPPORTED", errorMessage));
        return ValidationResult.failure(errorResponse);
      }
      return ValidationResult.success(); // At least one PSP supports via flow target
    }
  }

  @Override
  public List<String> getUnsupportedPsps(
      String currency,
      List<String> pspIds,
      String brandId,
      String environmentId,
      String flowActionId) {

    if (pspIds == null || pspIds.isEmpty()) {
      return new ArrayList<>();
    }

    List<String> unsupportedPsps = new ArrayList<>();

    // Check each PSP to see if it has a currency_limits entry for the currency
    for (String pspId : pspIds) {
      Boolean supports =
          currencyLimitRepository.existsByCompositeKeyAndCurrency(
              brandId, environmentId, flowActionId, pspId, currency);
      if (supports == null || !supports) {
        unsupportedPsps.add(pspId);
      }
    }

    return unsupportedPsps;
  }

  @Override
  public Boolean validateFlowTargetCurrencySupport(
      String currency, String brandId, String environmentId, String flowActionId) {

    // Check if any PSPs support this currency for the given context
    // This indicates that flow targets support the currency through their PSPs
    List<String> supportedPspIds =
        currencyLimitRepository.findSupportedPspIdsByCurrency(
            brandId, environmentId, flowActionId, currency);
    return !supportedPspIds.isEmpty(); // Return true if any PSPs support this currency
  }
}
