package connxt.systemrole.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import connxt.systemrole.entity.SystemRole;

@Repository
public interface SystemRoleRepository extends JpaRepository<SystemRole, String> {

  @NonNull
  Optional<SystemRole> findByName(@Param("name") String name);

  boolean existsByName(@Param("name") String name);

  @Override
  @NonNull
  List<SystemRole> findAll();
}
