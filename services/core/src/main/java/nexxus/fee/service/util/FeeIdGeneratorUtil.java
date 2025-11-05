package nexxus.fee.service.util;

import org.springframework.stereotype.Component;

import nexxus.shared.constants.IdPrefix;
import nexxus.shared.util.RandomIdGenerator;

@Component
public class FeeIdGeneratorUtil extends RandomIdGenerator {

  public String generateFeeId() {
    return generateId(IdPrefix.FEE);
  }

  public String generateFeeComponentId() {
    return generateId(IdPrefix.FEE_COMPONENT);
  }
}
