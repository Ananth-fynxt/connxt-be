package nexxus.transaction.entity;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import nexxus.shared.constants.IdPrefix;
import nexxus.shared.util.RandomIdGenerator;

public class TransactionIdGenerator extends RandomIdGenerator implements IdentifierGenerator {
  @Override
  public Object generate(
      SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {

    if (o instanceof Transaction txn) {
      if (txn.getId() != null
          && txn.getId().getTxnId() != null
          && !txn.getId().getTxnId().isEmpty()) {
        return txn.getId().getTxnId();
      }
    }

    return generateId(IdPrefix.TRANSACTION, 12);
  }
}
