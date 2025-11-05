package nexxus.pspgroup.entity;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import nexxus.shared.constants.IdPrefix;
import nexxus.shared.util.RandomIdGenerator;

public class PspGroupIdGenerator extends RandomIdGenerator implements IdentifierGenerator {
  @Override
  public Object generate(
      SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {

    if (o instanceof PspGroup) {
      PspGroup pspGroup = (PspGroup) o;
      if (pspGroup.getPspGroupId() != null
          && pspGroup.getPspGroupId().getId() != null
          && !pspGroup.getPspGroupId().getId().isEmpty()) {
        return pspGroup.getPspGroupId().getId();
      }
    }

    return generateId(IdPrefix.PSP_GROUP);
  }
}
