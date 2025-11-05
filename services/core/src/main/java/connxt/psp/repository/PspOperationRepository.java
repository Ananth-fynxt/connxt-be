package connxt.psp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import connxt.psp.entity.PspOperation;

@Repository
public interface PspOperationRepository extends JpaRepository<PspOperation, String> {

  @Query(
      value =
          "SELECT * FROM psp_operations WHERE brand_id = :brandId AND environment_id = :environmentId AND psp_id = :pspId AND flow_action_id = :flowActionId AND flow_definition_id = :flowDefinitionId",
      nativeQuery = true)
  PspOperation findByCompositeKey(
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("pspId") String pspId,
      @Param("flowActionId") String flowActionId,
      @Param("flowDefinitionId") String flowDefinitionId);

  @Modifying
  @Query(
      value =
          "INSERT INTO psp_operations (brand_id, environment_id, psp_id, flow_action_id, flow_definition_id, status) VALUES (:brandId, :environmentId, :pspId, :flowActionId, :flowDefinitionId, :status)",
      nativeQuery = true)
  void insertPspOperation(
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("pspId") String pspId,
      @Param("flowActionId") String flowActionId,
      @Param("flowDefinitionId") String flowDefinitionId,
      @Param("status") String status);

  @Modifying
  @Query(
      value =
          "UPDATE psp_operations SET status = :status WHERE brand_id = :brandId AND environment_id = :environmentId AND psp_id = :pspId AND flow_action_id = :flowActionId AND flow_definition_id = :flowDefinitionId",
      nativeQuery = true)
  void updateStatus(
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("pspId") String pspId,
      @Param("flowActionId") String flowActionId,
      @Param("flowDefinitionId") String flowDefinitionId,
      @Param("status") String status);

  @Query(value = "SELECT * FROM psp_operations WHERE psp_id = :pspId", nativeQuery = true)
  List<PspOperation> findByPspId(@Param("pspId") String pspId);

  @Modifying
  @Query(value = "DELETE FROM psp_operations WHERE psp_id = :pspId", nativeQuery = true)
  void deleteByPspId(@Param("pspId") String pspId);

  @Query(
      value =
          "SELECT COUNT(*) FROM psp_operations WHERE psp_id IN :pspIds AND flow_action_id = :flowActionId AND :currency = ANY(currencies)",
      nativeQuery = true)
  long countByPspIdsAndFlowActionIdAndCurrency(
      @Param("pspIds") List<String> pspIds,
      @Param("flowActionId") String flowActionId,
      @Param("currency") String currency);

  PspOperation findByPspIdAndFlowActionId(String pspId, String flowActionId);
}
