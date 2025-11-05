package connxt.fee.entity;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import connxt.shared.constants.IdPrefix;
import connxt.shared.util.RandomIdGenerator;

public class FeeComponentIdGenerator extends RandomIdGenerator implements IdentifierGenerator {
  @Override
  public Object generate(
      SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {
    return generateId(IdPrefix.FEE_COMPONENT);
  }
}
