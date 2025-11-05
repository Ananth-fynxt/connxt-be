package nexxus.flowdefinition.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import nexxus.flowdefinition.entity.FlowDefinition;

@Repository
public interface FlowDefinitionRepository extends JpaRepository<FlowDefinition, String> {

  List<FlowDefinition> findByFlowTargetId(String flowTargetId);

  List<FlowDefinition> findByBrandId(@Param("brandId") String brandId);

  List<FlowDefinition> findByFlowTargetIdAndBrandId(String flowTargetId, String brandId);

  Optional<FlowDefinition> findByCodeAndBrandId(String code, String brandId);

  boolean existsByCodeAndBrandId(String code, String brandId);

  Optional<FlowDefinition> findByFlowTargetIdAndFlowActionId(
      String flowTargetId, String flowActionId);

  void deleteById(@NonNull String id);
}
