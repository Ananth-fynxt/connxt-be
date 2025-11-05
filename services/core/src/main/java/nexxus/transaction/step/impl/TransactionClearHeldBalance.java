package nexxus.transaction.step.impl;

import java.util.Optional;

import org.springframework.stereotype.Component;

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
public class TransactionClearHeldBalance extends AbstractTransactionStep {

  private final WalletRepository walletRepository;

  public TransactionClearHeldBalance(
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
      wallet.setHoldBalance(wallet.getHoldBalance().subtract(transaction.getTxnAmount()));
      walletRepository.save(wallet);
    }
    return context;
  }

  @Override
  public TransactionStatus getDestinationStatus() {
    return TransactionStatus.CLEAR_HELD_BALANCE;
  }
}
