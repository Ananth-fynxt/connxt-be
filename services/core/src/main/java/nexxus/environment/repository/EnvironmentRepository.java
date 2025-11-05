package nexxus.environment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import nexxus.environment.entity.Environment;

@Repository
public interface EnvironmentRepository extends JpaRepository<Environment, String> {

  @NonNull
  Optional<Environment> findByName(@Param("name") String name);

  boolean existsByName(@Param("name") String name);

  @NonNull
  List<Environment> findAll();

  void deleteById(@Param("id") @NonNull String id);

  boolean existsByBrandIdAndName(@Param("brandId") String brandId, @Param("name") String name);

  boolean existsBySecret(@Param("secret") String secret);

  Optional<Environment> findBySecret(@Param("secret") String secret);

  List<Environment> findByBrandId(@Param("brandId") String brandId);

  Optional<Environment> findByToken(@Param("token") String token);
}
