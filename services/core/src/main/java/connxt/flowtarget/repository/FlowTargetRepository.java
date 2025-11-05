package connxt.flowtarget.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import connxt.flowtarget.entity.FlowTarget;

@Repository
public interface FlowTargetRepository extends JpaRepository<FlowTarget, String> {
  List<FlowTarget> findByFlowTypeId(String flowTypeId);

  Optional<FlowTarget> findByFlowTypeIdAndName(String flowTypeId, String name);

  boolean existsByFlowTypeIdAndName(String flowTypeId, String name);
}
