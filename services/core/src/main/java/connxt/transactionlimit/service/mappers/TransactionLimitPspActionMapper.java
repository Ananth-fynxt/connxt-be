package connxt.transactionlimit.service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import connxt.shared.db.mappers.MapperCoreConfig;
import connxt.transactionlimit.dto.TransactionLimitPspActionDto;
import connxt.transactionlimit.entity.TransactionLimitPspAction;

@Mapper(config = MapperCoreConfig.class)
public interface TransactionLimitPspActionMapper {

  @Mapping(target = "transactionLimitId", source = "transactionLimitId")
  @Mapping(target = "transactionLimitVersion", source = "transactionLimitVersion")
  TransactionLimitPspAction toTransactionLimitPspAction(
      TransactionLimitPspActionDto pspActionDto,
      String transactionLimitId,
      Integer transactionLimitVersion);

  TransactionLimitPspActionDto toTransactionLimitPspActionDto(TransactionLimitPspAction pspAction);
}
