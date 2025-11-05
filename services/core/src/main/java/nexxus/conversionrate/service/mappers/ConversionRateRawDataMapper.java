package nexxus.conversionrate.service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import nexxus.conversionrate.dto.ConversionRateRawDataDto;
import nexxus.conversionrate.entity.ConversionRateRawData;
import nexxus.conversionrate.entity.EmbeddableConversionRateRawDataId;
import nexxus.shared.db.mappers.MapperCoreConfig;

@Mapper(config = MapperCoreConfig.class)
public interface ConversionRateRawDataMapper {

  @Mapping(source = "rawDataId.id", target = "id")
  @Mapping(source = "rawDataId.version", target = "version")
  ConversionRateRawDataDto toConversionRateRawDataDto(ConversionRateRawData conversionRateRawData);

  @Mapping(source = "id", target = "rawDataId.id")
  @Mapping(source = "version", target = "rawDataId.version")
  ConversionRateRawData toConversionRateRawData(ConversionRateRawDataDto conversionRateRawDataDto);

  default ConversionRateRawData toConversionRateRawData(
      ConversionRateRawDataDto dto, Integer version) {
    ConversionRateRawData rawData = toConversionRateRawData(dto);
    rawData.setRawDataId(new EmbeddableConversionRateRawDataId(dto.getId(), version));
    return rawData;
  }

  default ConversionRateRawData createUpdatedRawData(
      ConversionRateRawData existing, Integer newVersion) {
    return ConversionRateRawData.builder()
        .rawDataId(
            new EmbeddableConversionRateRawDataId(existing.getRawDataId().getId(), newVersion))
        .sourceCurrency(existing.getSourceCurrency())
        .targetCurrency(existing.getTargetCurrency())
        .timeRange(existing.getTimeRange())
        .amount(existing.getAmount())
        .build();
  }
}
