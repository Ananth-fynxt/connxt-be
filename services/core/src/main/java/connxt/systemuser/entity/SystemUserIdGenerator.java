package connxt.systemuser.entity;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import connxt.shared.constants.IdPrefix;
import connxt.shared.util.RandomIdGenerator;

public class SystemUserIdGenerator extends RandomIdGenerator implements IdentifierGenerator {
  @Override
  public Object generate(
      SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {
    return generateId(IdPrefix.SYSTEM_USER);
  }
}
