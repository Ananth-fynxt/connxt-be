package nexxus.shared.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import nexxus.flowtarget.repository.FlowTargetRepository;
import nexxus.psp.repository.PspRepository;
import nexxus.psp.repository.SupportedCurrencyRepository;
import nexxus.shared.service.FeeCurrencyValidationService;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of fee currency validation service. Validates PSP currency support for fee
 * operations.
 */
@Service
@RequiredArgsConstructor
public class FeeCurrencyValidationServiceImpl implements FeeCurrencyValidationService {

  private final SupportedCurrencyRepository supportedCurrencyRepository;
  private final FlowTargetRepository flowTargetRepository;
  private final PspRepository pspRepository;

  @Override
  public Boolean validatePspCurrencySupport(
      String currency,
      List<String> pspIds,
      String brandId,
      String environmentId,
      String flowActionId) {

    for (String pspId : pspIds) {
      Boolean isSupported =
          validateSinglePspCurrency(currency, pspId, brandId, environmentId, flowActionId);
      if (isSupported == null || !isSupported) {
        return false;
      }
    }
    return true;
  }

  /** Validates currency support for a single PSP */
  private Boolean validateSinglePspCurrency(
      String currency, String pspId, String brandId, String environmentId, String flowActionId) {
    // First check supported_currencies table
    boolean foundInSupportedCurrencies =
        supportedCurrencyRepository
            .findByCompositeKey(brandId, environmentId, flowActionId, pspId)
            .stream()
            .anyMatch(supportedCurrency -> currency.equals(supportedCurrency.getCurrency()));

    if (foundInSupportedCurrencies) {
      return true;
    } else {
      // Fallback to flow_targets table
      return checkCurrencyInFlowTargets(currency, pspId);
    }
  }

  /**
   * Checks if currency is supported in flow_targets table. PSP ID should match a flow target ID in
   * this context.
   */
  private Boolean checkCurrencyInFlowTargets(String currency, String pspId) {
    return pspRepository
        .findById(pspId)
        .map(
            psp ->
                flowTargetRepository
                    .findById(psp.getFlowTargetId())
                    .map(
                        flowTarget -> {
                          List<String> currencies = flowTarget.getCurrencies();
                          if (currencies != null && !currencies.isEmpty()) {
                            return currencies.contains(currency);
                          }
                          return false;
                        })
                    .orElse(false))
        .orElse(false);
  }

  @Override
  public List<String> getUnsupportedPsps(
      String currency,
      List<String> pspIds,
      String brandId,
      String environmentId,
      String flowActionId) {

    List<String> unsupportedPsps = new ArrayList<>();

    for (String pspId : pspIds) {
      Boolean isSupported =
          validateSinglePspCurrency(currency, pspId, brandId, environmentId, flowActionId);
      if (isSupported == null || !isSupported) {
        unsupportedPsps.add(pspId);
      }
    }

    return unsupportedPsps;
  }
}
