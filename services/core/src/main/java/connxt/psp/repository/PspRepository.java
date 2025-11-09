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
              + "AND po.flow_action_id = :flowActionId",
      nativeQuery = true)
  List<Psp> findByBrandEnvStatusAndFlowAction(
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("status") String status,
      @Param("flowActionId") String flowActionId);
}
