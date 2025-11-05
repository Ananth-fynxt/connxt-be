package connxt.fi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import connxt.fi.entity.Fi;

@Repository
public interface FiRepository extends JpaRepository<Fi, String> {

  boolean existsByName(@Param("name") String name);

  boolean existsByEmail(@Param("email") String email);

  @NonNull
  List<Fi> findAll();

  Optional<Fi> findByUserId(@Param("userId") String userId);
}
