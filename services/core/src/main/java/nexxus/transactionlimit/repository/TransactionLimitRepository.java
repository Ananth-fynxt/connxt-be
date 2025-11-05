package nexxus.transactionlimit.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import nexxus.shared.constants.Status;
import nexxus.transactionlimit.entity.EmbeddableTransactionLimitId;
import nexxus.transactionlimit.entity.TransactionLimit;

@Repository
public interface TransactionLimitRepository
    extends JpaRepository<TransactionLimit, EmbeddableTransactionLimitId> {

  Optional<TransactionLimit> findByBrandIdAndEnvironmentIdAndName(
      String brandId, String environmentId, String name);

  @Query(
      "SELECT tl FROM TransactionLimit tl WHERE tl.brandId = :brandId AND tl.environmentId = :environmentId AND tl.transactionLimitId.version = (SELECT MAX(tl2.transactionLimitId.version) FROM TransactionLimit tl2 WHERE tl2.transactionLimitId.id = tl.transactionLimitId.id)")
  List<TransactionLimit> findByBrandIdAndEnvironmentId(
      @Param("brandId") String brandId, @Param("environmentId") String environmentId);

  @Query(
      "SELECT tl FROM TransactionLimit tl WHERE tl.transactionLimitId.id = :id ORDER BY tl.transactionLimitId.version DESC")
  List<TransactionLimit> findByIdOrderByVersionDesc(@Param("id") String id);

  @Query(
      "SELECT tl FROM TransactionLimit tl WHERE tl.transactionLimitId.id = :id ORDER BY tl.transactionLimitId.version DESC LIMIT 1")
  Optional<TransactionLimit> findLatestVersionById(String id);

  Optional<TransactionLimit> findByTransactionLimitIdIdAndTransactionLimitIdVersion(
      String id, Integer version);

  @Query(
      "SELECT COALESCE(MAX(tl.transactionLimitId.version), 0) FROM TransactionLimit tl WHERE tl.transactionLimitId.id = :id")
  Integer findMaxVersionById(@Param("id") String id);

  void deleteByTransactionLimitIdId(String id);

  boolean existsByBrandIdAndEnvironmentIdAndName(String brandId, String environmentId, String name);

  @Query(
      "SELECT tl FROM TransactionLimit tl JOIN TransactionLimitPsp tlp ON tl.transactionLimitId.id = tlp.transactionLimitId AND tl.transactionLimitId.version = tlp.transactionLimitVersion JOIN TransactionLimitPspAction tlpa ON tl.transactionLimitId.id = tlpa.transactionLimitId AND tl.transactionLimitId.version = tlpa.transactionLimitVersion WHERE tlp.pspId IN :pspIds AND tl.brandId = :brandId AND tl.environmentId = :environmentId AND tlpa.flowActionId = :flowActionId AND tl.currency = :currency AND tl.status = :status AND tl.transactionLimitId.version = (SELECT MAX(tl2.transactionLimitId.version) FROM TransactionLimit tl2 WHERE tl2.transactionLimitId.id = tl.transactionLimitId.id)")
  List<TransactionLimit> findLatestEnabledTransactionLimitsByCriteria(
      @Param("pspIds") List<String> pspIds,
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("flowActionId") String flowActionId,
      @Param("currency") String currency,
      @Param("status") Status status);
}
