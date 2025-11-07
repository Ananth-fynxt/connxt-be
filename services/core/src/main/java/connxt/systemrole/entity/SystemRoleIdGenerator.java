package connxt.systemrole.entity;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import connxt.shared.constants.IdPrefix;
import connxt.shared.util.RandomIdGenerator;

public class SystemRoleIdGenerator extends RandomIdGenerator implements IdentifierGenerator {

  @Override
  public Object generate(SharedSessionContractImplementor session, Object o) {
    return generateId(IdPrefix.SYSTEM_ROLE);
  }
}
