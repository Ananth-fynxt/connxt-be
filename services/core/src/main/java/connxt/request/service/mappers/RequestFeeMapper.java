package connxt.request.service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import connxt.fee.dto.FeeDto;
import connxt.request.entity.RequestFee;
import connxt.shared.db.mappers.MapperCoreConfig;

@Mapper(config = MapperCoreConfig.class)
public interface RequestFeeMapper {

  @Mapping(target = "requestId", source = "requestId")
  @Mapping(target = "feeId", source = "feeDto.id")
  @Mapping(target = "feeVersion", source = "feeDto.version")
  RequestFee toRequestFee(String requestId, FeeDto feeDto);
}
