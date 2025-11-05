package nexxus.conversionrate.entity;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import nexxus.shared.constants.IdPrefix;
import nexxus.shared.util.RandomIdGenerator;

public class ConversionRateRawDataIdGenerator extends RandomIdGenerator
    implements IdentifierGenerator {
  @Override
  public Object generate(
      SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {

    if (o instanceof ConversionRateRawData) {
      ConversionRateRawData rawData = (ConversionRateRawData) o;
      if (rawData.getRawDataId() != null
          && rawData.getRawDataId().getId() != null
          && !rawData.getRawDataId().getId().isEmpty()) {
        return rawData.getRawDataId().getId();
      }
    }

    return generateId(IdPrefix.CONVERSION_RATE_SETUP);
  }
}
