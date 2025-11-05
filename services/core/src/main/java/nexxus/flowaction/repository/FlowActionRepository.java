package nexxus.flowaction.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import nexxus.flowaction.entity.FlowAction;

@Repository
public interface FlowActionRepository extends JpaRepository<FlowAction, String> {

  List<FlowAction> findByFlowTypeId(@Param("flowTypeId") String flowTypeId);

  Optional<FlowAction> findByNameAndFlowTypeId(
      @Param("name") String name, @Param("flowTypeId") String flowTypeId);

  @Modifying
  void deleteById(@Param("id") @NonNull String id);

  boolean existsByNameAndFlowTypeId(
      @Param("name") String name, @Param("flowTypeId") String flowTypeId);
}
