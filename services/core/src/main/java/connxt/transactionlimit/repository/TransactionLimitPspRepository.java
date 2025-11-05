package connxt.transactionlimit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import connxt.transactionlimit.entity.TransactionLimitPsp;
import connxt.transactionlimit.entity.TransactionLimitPspId;

@Repository
public interface TransactionLimitPspRepository
    extends JpaRepository<TransactionLimitPsp, TransactionLimitPspId> {

  List<TransactionLimitPsp> findByTransactionLimitIdAndTransactionLimitVersion(
      String transactionLimitId, Integer transactionLimitVersion);

  List<TransactionLimitPsp> findByPspId(String pspId);

  @Query(
      "SELECT tlp FROM TransactionLimitPsp tlp WHERE tlp.pspId = :pspId AND tlp.transactionLimitVersion = (SELECT MAX(tlp2.transactionLimitVersion) FROM TransactionLimitPsp tlp2 WHERE tlp2.transactionLimitId = tlp.transactionLimitId AND tlp2.pspId = :pspId)")
  List<TransactionLimitPsp> findLatestVersionsByPspId(@Param("pspId") String pspId);

  void deleteByTransactionLimitId(String transactionLimitId);

  void deleteByTransactionLimitIdAndTransactionLimitVersion(
      String transactionLimitId, Integer transactionLimitVersion);
}
