package nexxus.transaction.repository;

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

import nexxus.transaction.dto.TransactionStatus;
import nexxus.transaction.entity.EmbeddableTransactionId;
import nexxus.transaction.entity.Transaction;

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
      "SELECT t FROM Transaction t WHERE t.customerId = :customerId AND t.brandId = :brandId AND t.environmentId = :environmentId AND t.id.version = (SELECT MAX(t2.id.version) FROM Transaction t2 WHERE t2.id.txnId = t.id.txnId) ORDER BY t.createdAt DESC")
  List<Transaction> findByCustomerAndBrandAndEnvLatest(
      @Param("customerId") String customerId,
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId);

  @Query(
      "SELECT t FROM Transaction t WHERE t.pspId = :pspId AND t.flowActionId = :flowActionId AND t.createdAt BETWEEN :startTime AND :endTime")
  List<Transaction> findByPspAndFlowAndTimeRange(
      @Param("pspId") String pspId,
      @Param("flowActionId") String flowActionId,
      @Param("startTime") LocalDateTime startTime,
      @Param("endTime") LocalDateTime endTime);

  @Query(
      "SELECT t FROM Transaction t WHERE t.pspId = :pspId AND t.customerId = :customerId AND t.flowActionId = :flowActionId AND t.createdAt BETWEEN :startTime AND :endTime")
  List<Transaction> findByPspCustomerFlow(
      @Param("pspId") String pspId,
      @Param("customerId") String customerId,
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

  @Query(
      "SELECT t FROM Transaction t WHERE t.externalRequestId = :externalRequestId ORDER BY t.id.version DESC LIMIT 1")
  Transaction findLatestByExternalRequestId(@Param("externalRequestId") String externalRequestId);

  @Query(
      "SELECT t FROM Transaction t WHERE t.pspId = :pspId AND t.brandId = :brandId AND t.environmentId = :environmentId AND t.flowActionId = :flowActionId AND t.txnCurrency = :txnCurrency AND t.createdAt BETWEEN :startTime AND :endTime")
  List<Transaction> findByPspContext(
      @Param("pspId") String pspId,
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("flowActionId") String flowActionId,
      @Param("txnCurrency") String txnCurrency,
      @Param("startTime") LocalDateTime startTime,
      @Param("endTime") LocalDateTime endTime);

  @Query(
      "SELECT t FROM Transaction t WHERE t.customerId = :customerId AND t.brandId = :brandId AND t.environmentId = :environmentId AND t.flowActionId = :flowActionId AND t.txnCurrency = :txnCurrency AND t.createdAt BETWEEN :startTime AND :endTime")
  List<Transaction> findByCustomerContext(
      @Param("customerId") String customerId,
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("flowActionId") String flowActionId,
      @Param("txnCurrency") String txnCurrency,
      @Param("startTime") LocalDateTime startTime,
      @Param("endTime") LocalDateTime endTime);

  @Query(
      "SELECT t FROM Transaction t WHERE t.pspId = :pspId AND t.customerId = :customerId AND t.brandId = :brandId AND t.environmentId = :environmentId AND t.flowActionId = :flowActionId AND t.txnCurrency = :txnCurrency AND t.createdAt BETWEEN :startTime AND :endTime")
  List<Transaction> findByPspCustomerContext(
      @Param("pspId") String pspId,
      @Param("customerId") String customerId,
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("flowActionId") String flowActionId,
      @Param("txnCurrency") String txnCurrency,
      @Param("startTime") LocalDateTime startTime,
      @Param("endTime") LocalDateTime endTime);

  @Query(
      "SELECT t FROM Transaction t WHERE t.customerTag = :customerTag AND t.customerAccountType = :customerAccountType AND t.brandId = :brandId AND t.environmentId = :environmentId AND t.flowActionId = :flowActionId AND t.txnCurrency = :txnCurrency AND t.createdAt BETWEEN :startTime AND :endTime")
  List<Transaction> findByCustomerCriteria(
      @Param("customerTag") String customerTag,
      @Param("customerAccountType") String customerAccountType,
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("flowActionId") String flowActionId,
      @Param("txnCurrency") String txnCurrency,
      @Param("startTime") LocalDateTime startTime,
      @Param("endTime") LocalDateTime endTime);

  @Query(
      "SELECT t FROM Transaction t WHERE t.customerTag = :customerTag AND t.brandId = :brandId AND t.environmentId = :environmentId AND t.flowActionId = :flowActionId AND t.txnCurrency = :txnCurrency AND t.createdAt BETWEEN :startTime AND :endTime")
  List<Transaction> findByCustomerTag(
      @Param("customerTag") String customerTag,
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("flowActionId") String flowActionId,
      @Param("txnCurrency") String txnCurrency,
      @Param("startTime") LocalDateTime startTime,
      @Param("endTime") LocalDateTime endTime);

  @Query(
      "SELECT t FROM Transaction t WHERE t.customerAccountType = :customerAccountType AND t.brandId = :brandId AND t.environmentId = :environmentId AND t.flowActionId = :flowActionId AND t.txnCurrency = :txnCurrency AND t.createdAt BETWEEN :startTime AND :endTime")
  List<Transaction> findByCustomerAccountType(
      @Param("customerAccountType") String customerAccountType,
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("flowActionId") String flowActionId,
      @Param("txnCurrency") String txnCurrency,
      @Param("startTime") LocalDateTime startTime,
      @Param("endTime") LocalDateTime endTime);

  @Query(
      "SELECT t.pspId, "
          + "       COALESCE(SUM(t.txnAmount), 0) as totalAmount, "
          + "       COUNT(t) as transactionCount "
          + "FROM Transaction t "
          + "WHERE t.pspId IN :pspIds "
          + "  AND t.brandId = :brandId "
          + "  AND t.environmentId = :environmentId "
          + "  AND t.flowActionId = :flowActionId "
          + "  AND t.txnCurrency = :txnCurrency "
          + "  AND t.status IN (nexxus.transaction.dto.TransactionStatus.SUCCESS, nexxus.transaction.dto.TransactionStatus.COMPLETED) "
          + "  AND t.createdAt BETWEEN :startTime AND :endTime "
          + "GROUP BY t.pspId")
  List<Object[]> findRoutingCalculationData(
      @Param("pspIds") List<String> pspIds,
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("flowActionId") String flowActionId,
      @Param("txnCurrency") String txnCurrency,
      @Param("startTime") LocalDateTime startTime,
      @Param("endTime") LocalDateTime endTime);
}
