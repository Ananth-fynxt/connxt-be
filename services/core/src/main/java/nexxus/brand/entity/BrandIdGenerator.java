package nexxus.brand.entity;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import nexxus.shared.constants.IdPrefix;
import nexxus.shared.util.RandomIdGenerator;

public class BrandIdGenerator extends RandomIdGenerator implements IdentifierGenerator {
  @Override
  public Object generate(
      SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {
    return generateId(IdPrefix.BRAND);
  }
}
