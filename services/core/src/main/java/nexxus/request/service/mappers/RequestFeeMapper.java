package nexxus.request.service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import nexxus.fee.dto.FeeDto;
import nexxus.request.entity.RequestFee;
import nexxus.shared.db.mappers.MapperCoreConfig;

@Mapper(config = MapperCoreConfig.class)
public interface RequestFeeMapper {

  @Mapping(target = "requestId", source = "requestId")
  @Mapping(target = "feeId", source = "feeDto.id")
  @Mapping(target = "feeVersion", source = "feeDto.version")
  RequestFee toRequestFee(String requestId, FeeDto feeDto);
}
