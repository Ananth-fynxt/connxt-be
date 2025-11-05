package connxt.riskrule.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import connxt.riskrule.entity.RiskRulePsp;
import connxt.riskrule.entity.RiskRulePspId;

@Repository
public interface RiskRulePspRepository extends JpaRepository<RiskRulePsp, RiskRulePspId> {

  List<RiskRulePsp> findByRiskRuleIdAndRiskRuleVersion(String riskRuleId, Integer riskRuleVersion);

  List<RiskRulePsp> findByPspId(String pspId);

  @Query("SELECT DISTINCT r.riskRuleId FROM RiskRulePsp r WHERE r.pspId = :pspId")
  List<String> findRiskRuleIdsByPspId(@Param("pspId") String pspId);

  @Query("SELECT DISTINCT r.riskRuleId FROM RiskRulePsp r WHERE r.pspId IN :pspIds")
  List<String> findRiskRuleIdsByPspIds(@Param("pspIds") List<String> pspIds);

  /**
   * Find the most recent version of RiskRulePsp records for each riskRuleId associated with the
   * given pspId. This query uses a window function to get the maximum version for each riskRuleId
   * and then joins back to get the complete RiskRulePsp records.
   */
  @Query(
      value =
          "SELECT rrp.* FROM risk_rule_psps rrp "
              + "INNER JOIN ( "
              + "  SELECT risk_rule_id, MAX(risk_rule_version) as max_version "
              + "  FROM risk_rule_psps "
              + "  WHERE psp_id = :pspId "
              + "  GROUP BY risk_rule_id "
              + ") latest ON rrp.risk_rule_id = latest.risk_rule_id AND rrp.risk_rule_version = latest.max_version "
              + "WHERE rrp.psp_id = :pspId",
      nativeQuery = true)
  List<RiskRulePsp> findLatestRiskRulePspsByPspId(@Param("pspId") String pspId);

  /**
   * Find the most recent version of RiskRulePsp records for each riskRuleId associated with the
   * given pspIds. This query uses a window function to get the maximum version for each riskRuleId
   * and then joins back to get the complete RiskRulePsp records.
   */
  @Query(
      value =
          "SELECT rrp.* FROM risk_rule_psps rrp "
              + "INNER JOIN ( "
              + "  SELECT risk_rule_id, MAX(risk_rule_version) as max_version "
              + "  FROM risk_rule_psps "
              + "  WHERE psp_id IN :pspIds "
              + "  GROUP BY risk_rule_id "
              + ") latest ON rrp.risk_rule_id = latest.risk_rule_id AND rrp.risk_rule_version = latest.max_version "
              + "WHERE rrp.psp_id IN :pspIds",
      nativeQuery = true)
  List<RiskRulePsp> findLatestRiskRulePspsByPspIds(@Param("pspIds") List<String> pspIds);

  void deleteByRiskRuleIdAndRiskRuleVersion(String riskRuleId, Integer riskRuleVersion);

  void deleteByRiskRuleId(String riskRuleId);
}
