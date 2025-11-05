package connxt.request.service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import connxt.request.entity.RequestTransactionLimit;
import connxt.shared.db.mappers.MapperCoreConfig;
import connxt.transactionlimit.dto.TransactionLimitDto;

@Mapper(config = MapperCoreConfig.class)
public interface RequestTransactionLimitMapper {

  @Mapping(target = "requestId", source = "requestId")
  @Mapping(target = "transactionLimitId", source = "transactionLimitDto.id")
  @Mapping(target = "transactionLimitVersion", source = "transactionLimitDto.version")
  RequestTransactionLimit toRequestTransactionLimit(
      String requestId, TransactionLimitDto transactionLimitDto);
}
