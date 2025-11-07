package connxt.brand.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import connxt.brand.entity.Brand;

@Repository
public interface BrandRepository extends JpaRepository<Brand, String> {

  @NonNull
  Optional<Brand> findByName(@Param("name") String name);

  boolean existsByName(@Param("name") String name);

  @NonNull
  Optional<Brand> findByEmail(@Param("email") String email);

  boolean existsByEmail(@Param("email") String email);

  @NonNull
  List<Brand> findAll();

  void deleteById(@Param("id") @NonNull String id);

  @Query(
      value =
          "SELECT b.* FROM brands b WHERE b.id IN (SELECT bu.brand_id FROM brand_users bu WHERE bu.user_id = :userId)",
      nativeQuery = true)
  List<Brand> findByUserId(@Param("userId") String userId);
}
