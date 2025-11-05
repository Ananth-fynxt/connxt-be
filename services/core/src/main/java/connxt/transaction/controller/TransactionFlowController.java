package connxt.transaction.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import connxt.shared.builder.ResponseBuilder;
import connxt.shared.builder.dto.ApiResponse;
import connxt.transaction.dto.TransactionApprovalRequest;
import connxt.transaction.dto.TransactionDto;
import connxt.transaction.dto.TransactionSearchCriteria;
import connxt.transaction.dto.TransactionStatus;
import connxt.transaction.service.TransactionFlowService;
import connxt.transaction.service.TransactionService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Validated
public class TransactionFlowController {

  private final TransactionFlowService transactionFlowService;
  private final TransactionService transactionService;
  private final ResponseBuilder responseBuilder;

  /** Creates a new transaction and starts the flow */
  @PostMapping
  public ResponseEntity<ApiResponse<Object>> createTransaction(
      @Validated @RequestBody @NotNull TransactionDto transactionDto) {
    log.info("Received request to create transaction with ID: {}", transactionDto.getTxnId());
    return responseBuilder.successResponse(
        transactionFlowService.createTransaction(transactionDto),
        "Transaction created successfully");
  }

  /** Moves a transaction to a specific status */
  @PutMapping("/{txnId}/status")
  public ResponseEntity<ApiResponse<Object>> moveToStatus(
      @PathVariable("txnId") @NotNull String txnId,
      @RequestParam("status") @NotNull TransactionStatus status,
      @Validated @RequestBody @NotNull TransactionDto transactionDto) {
    log.info("Received request to move transaction {} to status: {}", txnId, status);
    return responseBuilder.successResponse(
        transactionFlowService.moveToStatus(transactionDto, status),
        "Transaction status updated successfully");
  }

  @GetMapping("/{txnId}")
  public ResponseEntity<ApiResponse<Object>> read(@PathVariable("txnId") @NotNull String txnId) {
    return responseBuilder.successResponse(
        transactionService.read(txnId), "Transaction retrieved successfully");
  }

  @PostMapping("/brand/{brandId}/environment/{environmentId}/search")
  public ResponseEntity<ApiResponse<Object>> searchTransactions(
      @PathVariable("brandId") @NotBlank String brandId,
      @PathVariable("environmentId") @NotBlank String environmentId,
      @Valid @RequestBody TransactionSearchCriteria criteria) {
    log.info(
        "Received search request for brand: {} and environment: {} with criteria: {}",
        brandId,
        environmentId,
        criteria);

    Page<TransactionDto> pagedTransactions =
        transactionService.readByBrandIdAndEnvironmentId(brandId, environmentId, criteria);

    return responseBuilder.paginatedResponse(
        pagedTransactions, "Transactions retrieved successfully");
  }

  @GetMapping("/customer/{customerId}/brand/{brandId}/environment/{environmentId}")
  public ResponseEntity<ApiResponse<Object>> readByCustomerIdAndBrandIdAndEnvironmentId(
      @PathVariable("customerId") @NotBlank String customerId,
      @PathVariable("brandId") @NotBlank String brandId,
      @PathVariable("environmentId") @NotBlank String environmentId) {
    log.info(
        "Received request to retrieve transactions for customer: {}, brand: {} and environment: {}",
        customerId,
        brandId,
        environmentId);
    return responseBuilder.successResponse(
        transactionService.readByCustomerIdAndBrandIdAndEnvironmentId(
            customerId, brandId, environmentId));
  }

  @PostMapping("/{txnId}/approval")
  public ResponseEntity<ApiResponse<Object>> processApproval(
      @PathVariable("txnId") @NotNull String txnId,
      @Validated @RequestBody @NotNull TransactionApprovalRequest approvalRequest) {
    log.info("Received approval request for transaction: {}", txnId);
    approvalRequest.setTxnId(txnId);
    return responseBuilder.successResponse(
        transactionFlowService.processManualApproval(approvalRequest),
        "Transaction approval processed successfully");
  }
}
