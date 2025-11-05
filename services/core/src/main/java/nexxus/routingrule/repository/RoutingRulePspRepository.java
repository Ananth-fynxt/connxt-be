package nexxus.routingrule.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import nexxus.routingrule.entity.RoutingRulePsp;
import nexxus.routingrule.entity.RoutingRulePspId;

@Repository
public interface RoutingRulePspRepository extends JpaRepository<RoutingRulePsp, RoutingRulePspId> {

  @Modifying
  @Query("DELETE FROM RoutingRulePsp r WHERE r.routingRuleId = :routingRuleId")
  void deleteAllByRoutingRuleId(@Param("routingRuleId") String routingRuleId);

  @Query(
      "SELECT r FROM RoutingRulePsp r WHERE r.routingRuleId = :routingRuleId AND r.routingRuleVersion = :routingRuleVersion")
  List<RoutingRulePsp> findByRoutingRuleIdAndRoutingRuleVersion(
      @Param("routingRuleId") String routingRuleId,
      @Param("routingRuleVersion") Integer routingRuleVersion);
}
