package connxt.fee.service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import connxt.fee.dto.FeeComponentDto;
import connxt.fee.entity.EmbeddableFeeComponentId;
import connxt.fee.entity.FeeComponent;
import connxt.shared.db.mappers.MapperCoreConfig;

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
