package connxt.shared.builder.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import connxt.shared.builder.dto.ApiResponse;
import connxt.shared.builder.impl.ResponseBuilderImpl;
import connxt.shared.constants.ErrorCode;
import connxt.shared.exception.TransactionException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private final ResponseBuilderImpl responseBuilder;

  public GlobalExceptionHandler(ResponseBuilderImpl responseBuilderImpl) {
    this.responseBuilder = responseBuilderImpl;
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ApiResponse<Object>> handleResponseStatusException(
      ResponseStatusException ex) {
    System.out.println(ex.getMessage());
    ErrorCode errorCode = getErrorCodeFromCode(ex.getReason());
    HttpStatus httpStatus = HttpStatus.valueOf(ex.getStatusCode().value());
    return responseBuilder.errorResponse(errorCode, httpStatus.getReasonPhrase(), httpStatus);
  }

  @ExceptionHandler(TransactionException.class)
  public ResponseEntity<ApiResponse<Object>> handleTransactionException(TransactionException ex) {
    System.out.println(ex.getMessage());
    ErrorCode errorCode = getErrorCodeFromCode(String.valueOf(ex.code()));
    HttpStatus httpStatus = ex.category().http();
    return responseBuilder.errorResponse(errorCode, httpStatus.getReasonPhrase(), httpStatus);
  }

  @ExceptionHandler({AuthorizationDeniedException.class, AccessDeniedException.class})
  public ResponseEntity<ApiResponse<Object>> handleAccessDenied(Exception ex) {
    return responseBuilder.errorResponse(
        ErrorCode.AUTH_INSUFFICIENT_PERMISSIONS,
        ErrorCode.AUTH_INSUFFICIENT_PERMISSIONS.getMessage(),
        HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
    ex.printStackTrace();
    return responseBuilder.errorResponse(
        ErrorCode.GENERIC_ERROR,
        ErrorCode.UNEXPECTED_ERROR.getMessage(),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private ErrorCode getErrorCodeFromCode(String code) {
    if (code == null) {
      return ErrorCode.GENERIC_ERROR;
    }

    for (ErrorCode errorCode : ErrorCode.values()) {
      if (errorCode.getCode().equals(code)) {
        return errorCode;
      }
    }

    return ErrorCode.GENERIC_ERROR;
  }
}
