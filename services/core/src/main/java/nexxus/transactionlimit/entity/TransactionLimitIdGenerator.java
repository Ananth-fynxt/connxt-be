package nexxus.transactionlimit.entity;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import nexxus.shared.constants.IdPrefix;
import nexxus.shared.util.RandomIdGenerator;

public class TransactionLimitIdGenerator extends RandomIdGenerator implements IdentifierGenerator {
  @Override
  public Object generate(
      SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {

    if (o instanceof TransactionLimit) {
      TransactionLimit transactionLimit = (TransactionLimit) o;
      if (transactionLimit.getTransactionLimitId() != null
          && transactionLimit.getTransactionLimitId().getId() != null
          && !transactionLimit.getTransactionLimitId().getId().isEmpty()) {
        return transactionLimit.getTransactionLimitId().getId();
      }
    }

    return generateId(IdPrefix.TRANSACTION_LIMIT);
  }
}
