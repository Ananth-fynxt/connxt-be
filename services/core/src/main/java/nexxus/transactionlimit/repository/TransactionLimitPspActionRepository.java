package nexxus.transactionlimit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nexxus.transactionlimit.entity.TransactionLimitPspAction;
import nexxus.transactionlimit.entity.TransactionLimitPspActionId;

@Repository
public interface TransactionLimitPspActionRepository
    extends JpaRepository<TransactionLimitPspAction, TransactionLimitPspActionId> {

  List<TransactionLimitPspAction> findByTransactionLimitIdAndTransactionLimitVersion(
      String transactionLimitId, Integer transactionLimitVersion);

  void deleteByTransactionLimitId(String transactionLimitId);

  void deleteByTransactionLimitIdAndTransactionLimitVersion(
      String transactionLimitId, Integer transactionLimitVersion);
}
