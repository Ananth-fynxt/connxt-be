package nexxus.transaction.step.impl;

import java.util.Optional;

import org.springframework.stereotype.Component;

import nexxus.shared.constants.ErrorCode;
import nexxus.shared.exception.TransactionException;
import nexxus.transaction.context.TransactionExecutionContext;
import nexxus.transaction.dto.TransactionStatus;
import nexxus.transaction.entity.Transaction;
import nexxus.transaction.repository.TransactionRepository;
import nexxus.transaction.service.TransactionFlowConfigurationService;
import nexxus.transaction.service.mappers.TransactionMapper;
import nexxus.transaction.step.AbstractTransactionStep;
import nexxus.wallet.entity.Wallet;
import nexxus.wallet.repository.WalletRepository;

@Component
public class TransactionHoldBalanceStep extends AbstractTransactionStep {

  private final WalletRepository walletRepository;

  public TransactionHoldBalanceStep(
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
      if (wallet.getBalance().compareTo(transaction.getTxnAmount()) < 0) {
        throw new TransactionException(
            "Invalid transaction amount", ErrorCode.TRANSACTION_INVALID_TRANSITION_STATUS);
      }
      wallet.setHoldBalance(wallet.getHoldBalance().add(transaction.getTxnAmount()));
      wallet.setAvailableBalance(wallet.getAvailableBalance().subtract(transaction.getTxnAmount()));
      walletRepository.save(wallet);
    }
    return context;
  }

  @Override
  public TransactionStatus getDestinationStatus() {
    return TransactionStatus.HOLD_BALANCE;
  }
}
