package connxt.psp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import connxt.psp.entity.Psp;

@Repository
public interface PspRepository extends JpaRepository<Psp, String> {

  boolean existsByBrandIdAndEnvironmentIdAndFlowTargetIdAndName(
      String brandId, String environmentId, String flowTargetId, String name);

  List<Psp> findByBrandIdAndEnvironmentId(String brandId, String environmentId);

  @Query(
      value =
          "SELECT DISTINCT p.* FROM psps p "
              + "INNER JOIN psp_operations po ON p.id = po.psp_id "
              + "WHERE p.brand_id = :brandId AND p.environment_id = :environmentId AND p.status = CAST(:status AS status) AND po.status = CAST(:status AS status) "
              + "AND :currency = ANY(po.currencies) AND po.flow_action_id = :flowActionId",
      nativeQuery = true)
  List<Psp> findByBrandEnvStatusCurrencyAndFlowAction(
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("status") String status,
      @Param("currency") String currency,
      @Param("flowActionId") String flowActionId);

  @Query(
      value =
          "SELECT DISTINCT p.* FROM psps p "
              + "INNER JOIN psp_operations po ON p.id = po.psp_id "
              + "WHERE p.brand_id = :brandId AND p.environment_id = :environmentId AND p.status = CAST(:status AS status) AND po.status = CAST(:status AS status) "
              + "AND po.flow_action_id = :flowActionId",
      nativeQuery = true)
  List<Psp> findByBrandEnvStatusAndFlowAction(
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("status") String status,
      @Param("flowActionId") String flowActionId);

  @Query(
      value =
          "SELECT DISTINCT UNNEST(po.currencies) FROM psp_operations po JOIN psps p ON po.psp_id = p.id WHERE p.brand_id = :brandId AND p.environment_id = :environmentId",
      nativeQuery = true)
  List<String> findSupportedCurrenciesByBrandAndEnvironment(
      @Param("brandId") String brandId, @Param("environmentId") String environmentId);

  @Query(
      value =
          "SELECT DISTINCT UNNEST(po.countries) FROM psp_operations po JOIN psps p ON po.psp_id = p.id WHERE p.brand_id = :brandId AND p.environment_id = :environmentId",
      nativeQuery = true)
  List<String> findSupportedCountriesByBrandAndEnvironment(
      @Param("brandId") String brandId, @Param("environmentId") String environmentId);

  @Query(
      value =
          "SELECT DISTINCT p.* FROM psps p JOIN psp_operations po ON p.id = po.psp_id WHERE p.brand_id = :brandId AND p.environment_id = :environmentId AND po.flow_action_id = :actionId AND p.status = 'ENABLED' AND po.status = 'ENABLED'",
      nativeQuery = true)
  List<Psp> findActivePspsByBrandEnvironmentAndAction(
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("actionId") String actionId);

  @Query(
      value =
          "SELECT DISTINCT p.* FROM psps p JOIN psp_operations po ON p.id = po.psp_id WHERE p.brand_id = :brandId AND p.environment_id = :environmentId AND po.flow_action_id = :actionId AND :currency = ANY(po.currencies) AND p.status = 'ENABLED' AND po.status = 'ENABLED'",
      nativeQuery = true)
  List<Psp> findActivePspsByBrandEnvironmentActionAndCurrency(
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("actionId") String actionId,
      @Param("currency") String currency);
}
