package nexxus.transaction.service.mappers;

import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import nexxus.shared.db.mappers.MapperCoreConfig;
import nexxus.transaction.context.TransactionExecutionContext;
import nexxus.transaction.dto.TransactionDto;
import nexxus.transaction.dto.TransactionStatus;
import nexxus.transaction.entity.EmbeddableTransactionId;
import nexxus.transaction.entity.Transaction;

@Mapper(config = MapperCoreConfig.class)
public interface TransactionMapper {

  @Mapping(target = "txnId", source = "id.txnId")
  @Mapping(target = "version", source = "id.version")
  @Mapping(
      target = "executePayload",
      expression = "java(convertJsonNodeToMap(transaction.getExecutePayload()))")
  TransactionDto toDto(Transaction transaction);

  @Mapping(target = "txnId", expression = "java(context.getTransaction().getId().getTxnId())")
  @Mapping(target = "version", expression = "java(context.getTransaction().getId().getVersion())")
  @Mapping(
      target = "executePayload",
      expression = "java(convertJsonNodeToMap(context.getTransaction().getExecutePayload()))")
  @Mapping(target = "customData", expression = "java(context.getCustomData())")
  TransactionDto toDto(TransactionExecutionContext context);

  @Mapping(
      target = "id",
      expression =
          "java(createEmbeddableTransactionId(transactionDto.getTxnId(), transactionDto.getVersion()))")
  @Mapping(
      target = "executePayload",
      expression = "java(convertMapToJsonNode(transactionDto.getExecutePayload()))")
  Transaction toEntity(TransactionDto transactionDto);

  void updateEntityFromDto(TransactionDto transactionDto, @MappingTarget Transaction transaction);

  @Mapping(
      target = "id",
      expression = "java(createIncrementedTransactionId(currentTransaction.getId()))")
  @Mapping(target = "status", source = "destinationStatus")
  Transaction createNewVersionedRecord(
      Transaction currentTransaction, TransactionStatus destinationStatus);

  default EmbeddableTransactionId createEmbeddableTransactionId(String txnId, Integer version) {
    return new EmbeddableTransactionId(txnId, version);
  }

  default EmbeddableTransactionId createIncrementedTransactionId(
      EmbeddableTransactionId currentId) {
    return new EmbeddableTransactionId(currentId.getTxnId(), currentId.getVersion() + 1);
  }

  @SuppressWarnings("unchecked")
  default Map<String, Object> convertJsonNodeToMap(JsonNode jsonNode) {
    if (jsonNode == null) {
      return null;
    }
    ObjectMapper mapper = new ObjectMapper();
    return mapper.convertValue(jsonNode, Map.class);
  }

  default JsonNode convertMapToJsonNode(Map<String, Object> map) {
    if (map == null) {
      return null;
    }
    ObjectMapper mapper = new ObjectMapper();
    return mapper.valueToTree(map);
  }
}
