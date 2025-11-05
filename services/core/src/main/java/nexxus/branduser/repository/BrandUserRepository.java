package nexxus.branduser.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import nexxus.branduser.entity.BrandUser;

@Repository
public interface BrandUserRepository extends JpaRepository<BrandUser, String> {

  @NonNull
  Optional<BrandUser> findByEmail(@Param("email") String email);

  boolean existsByEmail(@Param("email") String email);

  @NonNull
  List<BrandUser> findAll();

  void deleteById(@Param("id") @NonNull String id);

  boolean existsByBrandIdAndEnvironmentIdAndEmail(
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("email") String email);

  List<BrandUser> findByBrandId(@Param("brandId") String brandId);

  List<BrandUser> findByUserId(@Param("userId") String userId);

  Optional<BrandUser> findByUserIdAndEmail(
      @Param("userId") String userId, @Param("email") String email);

  boolean existsByUserIdAndBrandIdAndEnvironmentIdAndBrandRoleId(
      @Param("userId") String userId,
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("brandRoleId") String brandRoleId);

  List<BrandUser> findByBrandIdAndEnvironmentId(
      @Param("brandId") String brandId, @Param("environmentId") String environmentId);
}
