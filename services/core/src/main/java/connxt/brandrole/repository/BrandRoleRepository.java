package connxt.brandrole.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import connxt.brandrole.entity.BrandRole;

@Repository
public interface BrandRoleRepository extends JpaRepository<BrandRole, String> {

  @NonNull
  Optional<BrandRole> findByName(@Param("name") String name);

  boolean existsByBrandIdAndEnvironmentIdAndName(
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("name") String name);

  @NonNull
  List<BrandRole> findAll();

  void deleteById(@Param("id") @NonNull String id);

  List<BrandRole> findByBrandId(@Param("brandId") String brandId);

  List<BrandRole> findByBrandIdAndEnvironmentId(
      @Param("brandId") String brandId, @Param("environmentId") String environmentId);
}
