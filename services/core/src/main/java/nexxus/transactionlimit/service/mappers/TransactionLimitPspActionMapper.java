package nexxus.transactionlimit.service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import nexxus.shared.db.mappers.MapperCoreConfig;
import nexxus.transactionlimit.dto.TransactionLimitPspActionDto;
import nexxus.transactionlimit.entity.TransactionLimitPspAction;

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
