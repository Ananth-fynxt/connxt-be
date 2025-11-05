package connxt.conversionrate.service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import connxt.conversionrate.dto.ConversionRateDto;
import connxt.conversionrate.entity.ConversionRate;
import connxt.conversionrate.entity.EmbeddableConversionRateId;
import connxt.shared.db.mappers.MapperCoreConfig;

@Mapper(config = MapperCoreConfig.class)
public interface ConversionRateMapper {

  @Mapping(source = "rateId.id", target = "id")
  @Mapping(source = "rateId.version", target = "version")
  ConversionRateDto toConversionRateDto(ConversionRate conversionRate);

  @Mapping(source = "id", target = "rateId.id")
  @Mapping(source = "version", target = "rateId.version")
  ConversionRate toConversionRate(ConversionRateDto conversionRateDto);

  default ConversionRate toConversionRate(ConversionRateDto dto, Integer version) {
    ConversionRate rate = toConversionRate(dto);
    rate.setRateId(new EmbeddableConversionRateId(dto.getId(), version));
    return rate;
  }

  default ConversionRate createUpdatedRate(ConversionRate existing, Integer newVersion) {
    return ConversionRate.builder()
        .rateId(new EmbeddableConversionRateId(existing.getRateId().getId(), newVersion))
        .brandId(existing.getBrandId())
        .environmentId(existing.getEnvironmentId())
        .status(existing.getStatus())
        .sourceCurrency(existing.getSourceCurrency())
        .targetCurrency(existing.getTargetCurrency())
        .value(existing.getValue())
        .build();
  }
}
