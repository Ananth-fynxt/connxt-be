package nexxus.shared.service;

import java.util.List;

public interface FeeCurrencyValidationService {

  Boolean validatePspCurrencySupport(
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
}
