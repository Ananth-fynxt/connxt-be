package connxt.transaction.step.impl;

import java.util.Optional;

import org.springframework.stereotype.Component;

import connxt.shared.constants.ErrorCode;
import connxt.shared.exception.TransactionException;
import connxt.transaction.context.TransactionExecutionContext;
import connxt.transaction.dto.TransactionStatus;
import connxt.transaction.entity.Transaction;
import connxt.transaction.repository.TransactionRepository;
import connxt.transaction.service.TransactionFlowConfigurationService;
import connxt.transaction.service.mappers.TransactionMapper;
import connxt.transaction.step.AbstractTransactionStep;
import connxt.wallet.entity.Wallet;
import connxt.wallet.repository.WalletRepository;

@Component
public class TransactionCreditedToWalletStep extends AbstractTransactionStep {

  private final WalletRepository walletRepository;

  public TransactionCreditedToWalletStep(
      WalletRepository walletRepository,
      TransactionRepository transactionRepository,
      TransactionMapper transactionMapper,
      TransactionFlowConfigurationService transactionFlowConfigurationService) {
    super(transactionRepository, transactionMapper, transactionFlowConfigurationService);
    this.walletRepository = walletRepository;
  }

  @Override
  protected TransactionExecutionContext doExecute(TransactionExecutionContext context) {
    Transaction transaction = context.getTransaction();
    String walletId = transaction.getWalletId();
    Optional<Wallet> walletOpt = walletRepository.findByWalletId(walletId);
    if (walletOpt.isPresent()) {
      Wallet wallet = walletOpt.get();
      wallet.setAvailableBalance(wallet.getAvailableBalance().add(transaction.getTxnAmount()));
      walletRepository.save(wallet);
    } else {
      throw new TransactionException(
          "Wallet not found", ErrorCode.TRANSACTION_INVALID_TRANSITION_STATUS);
    }
    return context;
  }

  @Override
  public TransactionStatus getDestinationStatus() {
    return TransactionStatus.CREDITED_TO_WALLET;
  }
}
