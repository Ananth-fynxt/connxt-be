package nexxus.autoapproval.entity;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import nexxus.shared.constants.IdPrefix;
import nexxus.shared.util.RandomIdGenerator;

public class AutoApprovalIdGenerator extends RandomIdGenerator implements IdentifierGenerator {
  @Override
  public Object generate(
      SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {

    if (o instanceof AutoApproval) {
      AutoApproval autoApproval = (AutoApproval) o;
      if (autoApproval.getAutoApprovalId() != null
          && autoApproval.getAutoApprovalId().getId() != null
          && !autoApproval.getAutoApprovalId().getId().isEmpty()) {
        return autoApproval.getAutoApprovalId().getId();
      }
    }

    return generateId(IdPrefix.AUTO_APPROVAL);
  }
}
