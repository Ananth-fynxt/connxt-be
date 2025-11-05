package connxt.transactionlimit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import connxt.transactionlimit.entity.TransactionLimitPspAction;
import connxt.transactionlimit.entity.TransactionLimitPspActionId;

@Repository
public interface TransactionLimitPspActionRepository
    extends JpaRepository<TransactionLimitPspAction, TransactionLimitPspActionId> {

  List<TransactionLimitPspAction> findByTransactionLimitIdAndTransactionLimitVersion(
      String transactionLimitId, Integer transactionLimitVersion);

  void deleteByTransactionLimitId(String transactionLimitId);

  void deleteByTransactionLimitIdAndTransactionLimitVersion(
      String transactionLimitId, Integer transactionLimitVersion);
}
