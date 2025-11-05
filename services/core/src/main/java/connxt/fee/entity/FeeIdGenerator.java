package connxt.fee.entity;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import connxt.shared.constants.IdPrefix;
import connxt.shared.util.RandomIdGenerator;

public class FeeIdGenerator extends RandomIdGenerator implements IdentifierGenerator {
  @Override
  public Object generate(
      SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {

    if (o instanceof Fee) {
      Fee fee = (Fee) o;
      if (fee.getFeeId() != null
          && fee.getFeeId().getId() != null
          && !fee.getFeeId().getId().isEmpty()) {
        return fee.getFeeId().getId();
      }
    }

    return generateId(IdPrefix.FEE);
  }
}
