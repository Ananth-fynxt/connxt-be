package connxt.flowdefinition.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import connxt.flowdefinition.entity.FlowDefinition;

@Repository
public interface FlowDefinitionRepository extends JpaRepository<FlowDefinition, String> {

  List<FlowDefinition> findByFlowTargetId(String flowTargetId);

  Optional<FlowDefinition> findByCode(String code);

  boolean existsByCode(String code);

  Optional<FlowDefinition> findByFlowTargetIdAndFlowActionId(
      String flowTargetId, String flowActionId);

  void deleteById(@NonNull String id);
}
