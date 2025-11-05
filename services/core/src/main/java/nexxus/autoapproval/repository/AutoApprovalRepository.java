package nexxus.autoapproval.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import nexxus.autoapproval.entity.AutoApproval;
import nexxus.autoapproval.entity.EmbeddableAutoApprovalId;

@Repository
public interface AutoApprovalRepository
    extends JpaRepository<AutoApproval, EmbeddableAutoApprovalId> {

  Optional<AutoApproval> findByBrandIdAndEnvironmentIdAndFlowActionIdAndName(
      String brandId, String environmentId, String flowActionId, String name);

  @Query(
      "SELECT aa FROM AutoApproval aa WHERE aa.brandId = :brandId AND aa.environmentId = :environmentId AND aa.autoApprovalId.version = (SELECT MAX(aa2.autoApprovalId.version) FROM AutoApproval aa2 WHERE aa2.autoApprovalId.id = aa.autoApprovalId.id)")
  List<AutoApproval> findByBrandIdAndEnvironmentId(
      @Param("brandId") String brandId, @Param("environmentId") String environmentId);

  @Query(
      "SELECT aa FROM AutoApproval aa WHERE aa.autoApprovalId.id = :id ORDER BY aa.autoApprovalId.version DESC")
  List<AutoApproval> findByIdOrderByVersionDesc(@Param("id") String id);

  @Query(
      "SELECT aa FROM AutoApproval aa WHERE aa.autoApprovalId.id = :id ORDER BY aa.autoApprovalId.version DESC LIMIT 1")
  Optional<AutoApproval> findLatestVersionById(String id);

  Optional<AutoApproval> findByAutoApprovalIdIdAndAutoApprovalIdVersion(String id, Integer version);

  @Query(
      "SELECT COALESCE(MAX(aa.autoApprovalId.version), 0) FROM AutoApproval aa WHERE aa.autoApprovalId.id = :id")
  Integer findMaxVersionById(@Param("id") String id);

  @Query(
      "SELECT aa FROM AutoApproval aa WHERE aa.flowActionId = :flowActionId AND aa.autoApprovalId.version = (SELECT MAX(aa2.autoApprovalId.version) FROM AutoApproval aa2 WHERE aa2.autoApprovalId.id = aa.autoApprovalId.id) ORDER BY aa.autoApprovalId.id")
  Optional<AutoApproval> findFirstLatestVersionByFlowActionId(
      @Param("flowActionId") String flowActionId);

  void deleteByAutoApprovalIdId(String id);

  boolean existsByBrandIdAndEnvironmentIdAndFlowActionIdAndName(
      String brandId, String environmentId, String flowActionId, String name);

  @Query(
      "SELECT COUNT(aa) > 0 FROM AutoApproval aa WHERE aa.brandId = :brandId AND aa.environmentId = :environmentId AND aa.flowActionId = :flowActionId AND aa.name = :name AND aa.autoApprovalId.id != :excludeId")
  boolean existsByBrandIdAndEnvironmentIdAndFlowActionIdAndNameAndIdNot(
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("flowActionId") String flowActionId,
      @Param("name") String name,
      @Param("excludeId") String excludeId);
}
