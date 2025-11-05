package connxt.shared.exception;

import java.util.List;

import connxt.shared.constants.ErrorCode;

public abstract class BaseException extends RuntimeException {
  private final String message;
  private final ErrorCode code;
  private final ErrorCategory category;
  private final String detail;
  private final List<ErrorDetail> errors;

  public BaseException(
      String message,
      ErrorCode ec,
      ErrorCategory category,
      String detail,
      List<ErrorDetail> errors) {
    this.message = message;
    this.code = ec;
    this.detail = detail;
    this.category = category;
    this.errors = errors;
  }

  public BaseException(String message, ErrorCode ec, ErrorCategory category, String detail) {
    this(message, ec, category, detail, List.of());
  }

  public BaseException(String message, ErrorCode ec, ErrorCategory category) {
    this(message, ec, category, ec.getMessage());
  }

  public BaseException(String message, ErrorCode ec) {
    this(message, ec, ErrorCategory.INTERNAL);
  }

  public String message() {
    return message;
  }

  public int code() {
    return Integer.parseInt(code.getCode());
  }

  public String detail() {
    return detail;
  }

  public ErrorCategory category() {
    return category;
  }

  public List<ErrorDetail> errors() {
    return errors;
  }
}
