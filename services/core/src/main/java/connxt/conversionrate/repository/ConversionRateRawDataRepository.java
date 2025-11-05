package connxt.conversionrate.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import connxt.conversionrate.entity.ConversionRateRawData;
import connxt.conversionrate.entity.EmbeddableConversionRateRawDataId;

@Repository
public interface ConversionRateRawDataRepository
    extends JpaRepository<ConversionRateRawData, EmbeddableConversionRateRawDataId> {

  @Query(
      "SELECT c FROM ConversionRateRawData c WHERE c.sourceCurrency = :sourceCurrency AND c.targetCurrency = :targetCurrency ORDER BY c.rawDataId.version DESC LIMIT 1")
  Optional<ConversionRateRawData> findLatestByCurrencyPair(
      @Param("sourceCurrency") String sourceCurrency,
      @Param("targetCurrency") String targetCurrency);
}
