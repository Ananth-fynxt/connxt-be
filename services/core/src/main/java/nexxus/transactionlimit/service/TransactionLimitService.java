package nexxus.transactionlimit.service;

import java.util.List;

import nexxus.shared.constants.Status;
import nexxus.transactionlimit.dto.TransactionLimitDto;

public interface TransactionLimitService {

  TransactionLimitDto create(TransactionLimitDto transactionLimitDto);

  TransactionLimitDto readLatest(String id);

  List<TransactionLimitDto> readByBrandAndEnvironment(String brandId, String environmentId);

  List<TransactionLimitDto> readByPspId(String pspId);

  List<TransactionLimitDto> readLatestEnabledTransactionLimitsByCriteria(
      List<String> pspIds,
      String brandId,
      String environmentId,
      String flowActionId,
      String currency,
      Status status);

  TransactionLimitDto update(String id, TransactionLimitDto transactionLimitDto);

  void delete(String id);
}
