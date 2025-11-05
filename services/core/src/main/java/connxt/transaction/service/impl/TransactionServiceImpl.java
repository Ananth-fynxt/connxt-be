package connxt.transaction.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import connxt.shared.constants.ErrorCode;
import connxt.transaction.dto.TransactionDto;
import connxt.transaction.dto.TransactionSearchCriteria;
import connxt.transaction.dto.TransactionStatus;
import connxt.transaction.entity.Transaction;
import connxt.transaction.query.TransactionQueryBuilder;
import connxt.transaction.repository.TransactionRepository;
import connxt.transaction.service.TransactionService;
import connxt.transaction.service.mappers.TransactionMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

  private static final Set<TransactionStatus> FAILURE_STATUSES =
      Set.of(
          TransactionStatus.FAILED,
          TransactionStatus.PG_FAILED,
          TransactionStatus.REJECTED,
          TransactionStatus.PG_REJECTED);

  private final TransactionRepository transactionRepository;
  private final TransactionMapper transactionMapper;
  private final TransactionQueryBuilder queryBuilder;

  @Override
  public TransactionDto read(String txnId) {
    Transaction transaction = transactionRepository.findLatestByTxnId(txnId);
    if (transaction == null) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, ErrorCode.TRANSACTION_NOT_FOUND.getCode());
    }
    return transactionMapper.toDto(transaction);
  }

  @Override
  public Page<TransactionDto> readByBrandIdAndEnvironmentId(
      String brandId, String environmentId, TransactionSearchCriteria criteria) {
    if (criteria == null) {
      criteria = new TransactionSearchCriteria();
    }

    Pageable pageable = queryBuilder.createPageable(criteria);
    Specification<Transaction> specification =
        queryBuilder.buildSpecification(brandId, environmentId, criteria);

    Page<Transaction> transactions = transactionRepository.findAll(specification, pageable);
    return transactions.map(transactionMapper::toDto);
  }

  @Override
  public List<TransactionDto> readByCustomerIdAndBrandIdAndEnvironmentId(
      String customerId, String brandId, String environmentId) {
    List<Transaction> transactions =
        transactionRepository.findByCustomerAndBrandAndEnvLatest(
            customerId, brandId, environmentId);
    return transactions.stream().map(transactionMapper::toDto).toList();
  }

  @Override
  public double calculateFailureRate(
      String pspId, String flowActionId, LocalDateTime startTime, LocalDateTime endTime) {
    return calculateFailureRateInternal(pspId, null, flowActionId, startTime, endTime);
  }

  @Override
  public double calculateFailureRateByCustomer(
      String pspId,
      String customerId,
      String flowActionId,
      LocalDateTime startTime,
      LocalDateTime endTime) {
    return calculateFailureRateInternal(pspId, customerId, flowActionId, startTime, endTime);
  }

  private double calculateFailureRateInternal(
      String pspId,
      String customerId,
      String flowActionId,
      LocalDateTime startTime,
      LocalDateTime endTime) {
    try {
      List<Transaction> recentTransactions =
          customerId != null
              ? transactionRepository.findByPspCustomerFlow(
                  pspId, customerId, flowActionId, startTime, endTime)
              : transactionRepository.findByPspAndFlowAndTimeRange(
                  pspId, flowActionId, startTime, endTime);

      if (recentTransactions.isEmpty()) {
        return 0.0;
      }

      long failedTransactions =
          recentTransactions.stream()
              .filter(transaction -> transaction.getStatus() != null)
              .filter(transaction -> FAILURE_STATUSES.contains(transaction.getStatus()))
              .count();

      return (double) failedTransactions / recentTransactions.size();
    } catch (Exception e) {
      return 0.0;
    }
  }

  @Override
  public long countByPspFlowStatus(
      String pspId,
      String flowActionId,
      TransactionStatus status,
      LocalDateTime startTime,
      LocalDateTime endTime) {
    return transactionRepository.countByPspFlowStatus(
        pspId, flowActionId, status, startTime, endTime);
  }

  @Override
  public long countByPspFlow(
      String pspId, String flowActionId, LocalDateTime startTime, LocalDateTime endTime) {
    return transactionRepository.countByPspFlow(pspId, flowActionId, startTime, endTime);
  }
}
