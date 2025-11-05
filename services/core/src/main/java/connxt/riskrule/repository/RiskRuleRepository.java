package connxt.riskrule.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import connxt.riskrule.entity.RiskRule;
import connxt.riskrule.entity.RiskRuleId;
import connxt.shared.constants.RiskAction;
import connxt.shared.constants.Status;

@Repository
public interface RiskRuleRepository extends JpaRepository<RiskRule, RiskRuleId> {

  @Query("SELECT r FROM RiskRule r WHERE r.riskRuleId.id = :id ORDER BY r.riskRuleId.version DESC")
  List<RiskRule> findByIdOrderByVersionDesc(@Param("id") String id);

  @Query(
      "SELECT r FROM RiskRule r WHERE r.riskRuleId.id = :id ORDER BY r.riskRuleId.version DESC LIMIT 1")
  Optional<RiskRule> findLatestVersionById(@Param("id") String id);

  Optional<RiskRule> findByRiskRuleIdIdAndRiskRuleIdVersion(String id, Integer version);

  @Query(
      "SELECT r FROM RiskRule r WHERE r.brandId = :brandId AND r.environmentId = :environmentId AND r.riskRuleId.version = (SELECT MAX(r2.riskRuleId.version) FROM RiskRule r2 WHERE r2.riskRuleId.id = r.riskRuleId.id)")
  List<RiskRule> findByBrandIdAndEnvironmentId(
      @Param("brandId") String brandId, @Param("environmentId") String environmentId);

  @Query(
      "SELECT COALESCE(MAX(r.riskRuleId.version), 0) FROM RiskRule r WHERE r.riskRuleId.id = :id")
  Integer findMaxVersionById(@Param("id") String id);

  void deleteByRiskRuleIdId(String id);

  boolean existsByBrandIdAndEnvironmentIdAndFlowActionIdAndName(
      String brandId, String environmentId, String flowActionId, String name);

  @Query(
      "SELECT rr FROM RiskRule rr JOIN RiskRulePsp rrp ON rr.riskRuleId.id = rrp.riskRuleId AND rr.riskRuleId.version = rrp.riskRuleVersion WHERE rrp.pspId IN :pspIds AND rr.brandId = :brandId AND rr.environmentId = :environmentId AND rr.flowActionId = :flowActionId AND rr.currency = :currency AND rr.action = :action AND rr.status = :status AND rr.riskRuleId.version = (SELECT MAX(r2.riskRuleId.version) FROM RiskRule r2 WHERE r2.riskRuleId.id = rr.riskRuleId.id)")
  List<RiskRule> findLatestEnabledRiskRulesByCriteria(
      @Param("pspIds") List<String> pspIds,
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("flowActionId") String flowActionId,
      @Param("currency") String currency,
      @Param("action") RiskAction action,
      @Param("status") Status status);

  @Query(
      "SELECT rr FROM RiskRule rr JOIN RiskRulePsp rrp ON rr.riskRuleId.id = rrp.riskRuleId AND rr.riskRuleId.version = rrp.riskRuleVersion WHERE rrp.pspId = :pspId AND rr.riskRuleId.version = (SELECT MAX(r2.riskRuleId.version) FROM RiskRule r2 WHERE r2.riskRuleId.id = rr.riskRuleId.id)")
  List<RiskRule> findLatestRiskRulesByPspId(@Param("pspId") String pspId);
}
