package nexxus.conversionrate.entity;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import nexxus.shared.constants.IdPrefix;
import nexxus.shared.util.RandomIdGenerator;

public class ConversionRateIdGenerator extends RandomIdGenerator implements IdentifierGenerator {
  @Override
  public Object generate(
      SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {

    if (o instanceof ConversionRate) {
      ConversionRate rate = (ConversionRate) o;
      if (rate.getRateId() != null
          && rate.getRateId().getId() != null
          && !rate.getRateId().getId().isEmpty()) {
        return rate.getRateId().getId();
      }
    }

    return generateId(IdPrefix.CONVERSION_RATE);
  }
}
