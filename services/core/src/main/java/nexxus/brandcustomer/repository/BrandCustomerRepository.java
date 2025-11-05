package nexxus.brandcustomer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import nexxus.brandcustomer.entity.BrandCustomer;

@Repository
public interface BrandCustomerRepository extends JpaRepository<BrandCustomer, String> {

  @NonNull
  Optional<BrandCustomer> findByEmail(@Param("email") String email);

  boolean existsByEmail(@Param("email") String email);

  @NonNull
  List<BrandCustomer> findAll();

  void deleteById(@Param("id") @NonNull String id);

  boolean existsByBrandIdAndEmail(@Param("brandId") String brandId, @Param("email") String email);

  List<BrandCustomer> findByBrandId(@Param("brandId") String brandId);

  boolean existsByBrandIdAndEnvironmentIdAndId(
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("id") String customerId);

  Optional<BrandCustomer> findByIdAndBrandIdAndEnvironmentId(
      @Param("id") String customerId,
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId);

  List<BrandCustomer> findByBrandIdAndEnvironmentId(
      @Param("brandId") String brandId, @Param("environmentId") String environmentId);
}
