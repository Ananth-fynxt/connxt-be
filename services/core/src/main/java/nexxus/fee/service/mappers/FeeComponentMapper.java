package nexxus.fee.service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import nexxus.fee.dto.FeeComponentDto;
import nexxus.fee.entity.EmbeddableFeeComponentId;
import nexxus.fee.entity.FeeComponent;
import nexxus.shared.db.mappers.MapperCoreConfig;

@Mapper(config = MapperCoreConfig.class)
public interface FeeComponentMapper {

  @Mapping(target = "feeComponentId", expression = "java(createEmbeddableFeeId(feeId, feeVersion))")
  FeeComponent toFeeComponent(FeeComponentDto componentDto, String feeId, Integer feeVersion);

  @Mapping(target = "id", source = "feeComponent.feeComponentId.id")
  FeeComponentDto toFeeComponentDto(FeeComponent feeComponent);

  default EmbeddableFeeComponentId createEmbeddableFeeId(String id, Integer version) {
    return new EmbeddableFeeComponentId(null, id, version);
  }
}
