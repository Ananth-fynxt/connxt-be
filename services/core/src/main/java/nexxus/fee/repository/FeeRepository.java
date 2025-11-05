package nexxus.fee.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import nexxus.fee.entity.EmbeddableFeeId;
import nexxus.fee.entity.Fee;
import nexxus.shared.constants.Status;

@Repository
public interface FeeRepository extends JpaRepository<Fee, EmbeddableFeeId> {

  Optional<Fee> findByBrandIdAndEnvironmentIdAndFlowActionIdAndName(
      String brandId, String environmentId, String flowActionId, String name);

  @Query(
      "SELECT f FROM Fee f WHERE f.brandId = :brandId AND f.environmentId = :environmentId AND f.feeId.version = (SELECT MAX(f2.feeId.version) FROM Fee f2 WHERE f2.feeId.id = f.feeId.id)")
  List<Fee> findByBrandIdAndEnvironmentId(
      @Param("brandId") String brandId, @Param("environmentId") String environmentId);

  @Query("SELECT f FROM Fee f WHERE f.feeId.id = :id ORDER BY f.feeId.version DESC")
  List<Fee> findByIdOrderByVersionDesc(@Param("id") String id);

  @Query("SELECT f FROM Fee f WHERE f.feeId.id = :id ORDER BY f.feeId.version DESC LIMIT 1")
  Optional<Fee> findLatestVersionById(String id);

  Optional<Fee> findByFeeIdIdAndFeeIdVersion(String id, Integer version);

  @Query("SELECT COALESCE(MAX(f.feeId.version), 0) FROM Fee f WHERE f.feeId.id = :id")
  Integer findMaxVersionById(@Param("id") String id);

  void deleteByFeeIdId(String id);

  boolean existsByBrandIdAndEnvironmentIdAndFlowActionIdAndName(
      String brandId, String environmentId, String flowActionId, String name);

  @Query(
      "SELECT f FROM Fee f JOIN FeePsp fp ON f.feeId.id = fp.feeId AND f.feeId.version = fp.feeVersion WHERE fp.pspId IN :pspIds AND f.brandId = :brandId AND f.environmentId = :environmentId AND f.flowActionId = :flowActionId AND f.currency = :currency AND f.status = :status AND f.feeId.version = (SELECT MAX(f2.feeId.version) FROM Fee f2 WHERE f2.feeId.id = f.feeId.id)")
  List<Fee> findLatestEnabledFeeRulesByCriteria(
      @Param("pspIds") List<String> pspIds,
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("flowActionId") String flowActionId,
      @Param("currency") String currency,
      @Param("status") Status status);

  @Query(
      "SELECT f FROM Fee f JOIN FeePsp fp ON f.feeId.id = fp.feeId AND f.feeId.version = fp.feeVersion WHERE fp.pspId = :pspId AND f.feeId.version = (SELECT MAX(f2.feeId.version) FROM Fee f2 WHERE f2.feeId.id = f.feeId.id)")
  List<Fee> findLatestFeesByPspId(@Param("pspId") String pspId);
}
