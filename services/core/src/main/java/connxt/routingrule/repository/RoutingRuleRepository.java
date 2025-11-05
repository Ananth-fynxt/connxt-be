package connxt.routingrule.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import connxt.routingrule.entity.EmbeddableRoutingRuleId;
import connxt.routingrule.entity.RoutingRule;

@Repository
public interface RoutingRuleRepository extends JpaRepository<RoutingRule, EmbeddableRoutingRuleId> {

  Optional<RoutingRule> findTopByRoutingRuleIdIdOrderByRoutingRuleIdVersionDesc(String id);

  @Query(
      value =
          "SELECT * FROM (SELECT r.*, ROW_NUMBER() OVER (PARTITION BY r.id ORDER BY r.version DESC) as rn FROM routing_rules r WHERE r.brand_id = :brandId AND r.environment_id = :environmentId) ranked WHERE rn = 1",
      nativeQuery = true)
  List<RoutingRule> findByBrandIdAndEnvironmentId(
      @Param("brandId") String brandId, @Param("environmentId") String environmentId);

  @Query(
      "SELECT COUNT(DISTINCT r.routingRuleId.id) FROM RoutingRule r WHERE r.brandId = :brandId AND r.environmentId = :environmentId")
  Long countByBrandIdAndEnvironmentId(
      @Param("brandId") String brandId, @Param("environmentId") String environmentId);

  @Query(
      value =
          "SELECT r.* FROM routing_rules r WHERE r.id = :routingRuleId AND r.status = 'ENABLED' AND r.version = (SELECT MAX(r2.version) FROM routing_rules r2 WHERE r2.id = :routingRuleId) LIMIT 1",
      nativeQuery = true)
  RoutingRule findActiveRoutingRuleById(@Param("routingRuleId") String routingRuleId);

  @Query(
      value =
          "SELECT r.* FROM routing_rules r "
              + "WHERE r.brand_id = :brandId "
              + "AND r.environment_id = :environmentId "
              + "AND r.status = 'ENABLED' "
              + "AND r.version = (SELECT MAX(r2.version) FROM routing_rules r2 WHERE r2.id = r.id) "
              + "ORDER BY r.created_at DESC",
      nativeQuery = true)
  List<RoutingRule> findEnabledRoutingRulesByBrandAndEnvironment(
      @Param("brandId") String brandId, @Param("environmentId") String environmentId);

  // Method to find by composite key
  Optional<RoutingRule> findByRoutingRuleId(EmbeddableRoutingRuleId routingRuleId);

  // Method to delete all versions of a routing rule by ID
  void deleteByRoutingRuleIdId(String id);

  boolean existsByBrandIdAndEnvironmentIdAndName(
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("name") String name);

  @Query(
      "SELECT COUNT(r) > 0 FROM RoutingRule r WHERE r.brandId = :brandId AND r.environmentId = :environmentId AND r.name = :name AND r.routingRuleId.id != :excludeId")
  boolean existsByBrandIdAndEnvironmentIdAndNameAndIdNot(
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("name") String name,
      @Param("excludeId") String excludeId);
}
