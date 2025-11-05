package nexxus.flowtype.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nexxus.flowtype.entity.FlowType;

@Repository
public interface FlowTypeRepository extends JpaRepository<FlowType, String> {

  Optional<FlowType> findByName(String name);

  boolean existsByName(String name);
}
