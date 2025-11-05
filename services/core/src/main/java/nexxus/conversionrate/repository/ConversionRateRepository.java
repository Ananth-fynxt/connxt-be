package nexxus.conversionrate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import nexxus.conversionrate.entity.ConversionRate;
import nexxus.conversionrate.entity.EmbeddableConversionRateId;

@Repository
public interface ConversionRateRepository
    extends JpaRepository<ConversionRate, EmbeddableConversionRateId> {

  @Query(
      "SELECT c FROM ConversionRate c WHERE c.rateId.id = :id ORDER BY c.rateId.version DESC LIMIT 1")
  Optional<ConversionRate> findLatestVersionById(@Param("id") String id);

  @Query(
      "SELECT c FROM ConversionRate c WHERE c.brandId = :brandId AND c.environmentId = :environmentId AND c.rateId.version = (SELECT MAX(c2.rateId.version) FROM ConversionRate c2 WHERE c2.rateId.id = c.rateId.id)")
  List<ConversionRate> findByBrandIdAndEnvironmentId(
      @Param("brandId") String brandId, @Param("environmentId") String environmentId);

  boolean existsBySourceCurrencyAndTargetCurrencyAndBrandIdAndEnvironmentId(
      String sourceCurrency, String targetCurrency, String brandId, String environmentId);

  void deleteByRateIdId(String id);
}
