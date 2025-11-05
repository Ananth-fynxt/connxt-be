package nexxus.transaction.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;

import nexxus.transaction.dto.TransactionDto;
import nexxus.transaction.dto.TransactionSearchCriteria;
import nexxus.transaction.dto.TransactionStatus;

public interface TransactionService {

  TransactionDto read(String txnId);

  Page<TransactionDto> readByBrandIdAndEnvironmentId(
      String brandId, String environmentId, TransactionSearchCriteria criteria);

  List<TransactionDto> readByCustomerIdAndBrandIdAndEnvironmentId(
      String customerId, String brandId, String environmentId);

  double calculateFailureRate(
      String pspId, String flowActionId, LocalDateTime startTime, LocalDateTime endTime);

  double calculateFailureRateByCustomer(
      String pspId,
      String customerId,
      String flowActionId,
      LocalDateTime startTime,
      LocalDateTime endTime);

  long countByPspFlowStatus(
      String pspId,
      String flowActionId,
      TransactionStatus status,
      LocalDateTime startTime,
      LocalDateTime endTime);

  long countByPspFlow(
      String pspId, String flowActionId, LocalDateTime startTime, LocalDateTime endTime);
}
