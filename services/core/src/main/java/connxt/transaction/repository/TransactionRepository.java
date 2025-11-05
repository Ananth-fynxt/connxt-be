package connxt.transaction.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import connxt.transaction.dto.TransactionStatus;
import connxt.transaction.entity.EmbeddableTransactionId;
import connxt.transaction.entity.Transaction;

import jakarta.persistence.LockModeType;

@Repository
public interface TransactionRepository
    extends JpaRepository<Transaction, EmbeddableTransactionId>,
        JpaSpecificationExecutor<Transaction> {

  @Query("SELECT MAX(t.id.version) FROM Transaction t WHERE t.id.txnId = :transactionId")
  Long findMaxVersionById(@Param("transactionId") String transactionId);

  @Query("SELECT t FROM Transaction t WHERE t.id.txnId = :transactionId ORDER BY t.id.version")
  List<Transaction> findByIdOrderByVersion(@Param("transactionId") String transactionId);

  @Query("SELECT MAX(t.id.version) FROM Transaction t WHERE t.id.txnId = :transactionId")
  int findLatestVersionById(@Param("transactionId") String transactionId);

  @Query(
      "SELECT t FROM Transaction t WHERE t.id.txnId = :transactionId ORDER BY t.id.version DESC LIMIT 1")
  Transaction findLatestByTxnId(@Param("transactionId") String transactionId);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query(
      "SELECT t FROM Transaction t WHERE t.id.txnId = :transactionId ORDER BY t.id.version DESC LIMIT 1")
  Transaction findLatestByTxnIdForUpdate(@Param("transactionId") String transactionId);

  @Query(
      "SELECT t FROM Transaction t WHERE t.brandId = :brandId AND t.environmentId = :environmentId AND t.id.version = (SELECT MAX(t2.id.version) FROM Transaction t2 WHERE t2.id.txnId = t.id.txnId)")
  Page<Transaction> findByBrandAndEnvLatest(
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      Pageable pageable);

  @Query(
      "SELECT t FROM Transaction t WHERE t.pspId = :pspId AND t.flowActionId = :flowActionId AND t.createdAt BETWEEN :startTime AND :endTime")
  List<Transaction> findByPspAndFlowAndTimeRange(
      @Param("pspId") String pspId,
      @Param("flowActionId") String flowActionId,
      @Param("startTime") LocalDateTime startTime,
      @Param("endTime") LocalDateTime endTime);

  @Query(
      "SELECT COUNT(t) FROM Transaction t WHERE t.pspId = :pspId AND t.flowActionId = :flowActionId AND t.status = :status AND t.createdAt BETWEEN :startTime AND :endTime")
  long countByPspFlowStatus(
      @Param("pspId") String pspId,
      @Param("flowActionId") String flowActionId,
      @Param("status") TransactionStatus status,
      @Param("startTime") LocalDateTime startTime,
      @Param("endTime") LocalDateTime endTime);

  @Query(
      "SELECT COUNT(t) FROM Transaction t WHERE t.pspId = :pspId AND t.flowActionId = :flowActionId AND t.createdAt BETWEEN :startTime AND :endTime")
  long countByPspFlow(
      @Param("pspId") String pspId,
      @Param("flowActionId") String flowActionId,
      @Param("startTime") LocalDateTime startTime,
      @Param("endTime") LocalDateTime endTime);
}
