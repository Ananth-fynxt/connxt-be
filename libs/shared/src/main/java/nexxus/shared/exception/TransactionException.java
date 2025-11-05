package nexxus.shared.exception;

import nexxus.shared.constants.ErrorCode;

public class TransactionException extends BaseException {
  public TransactionException(String message, ErrorCode code) {
    super(message, code);
  }

  public TransactionException(String message, ErrorCode ec, ErrorCategory category) {
    super(message, ec, category, ec.getMessage());
  }
}
