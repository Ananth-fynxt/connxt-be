package connxt.shared.builder;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import connxt.shared.builder.dto.ApiResponse;
import connxt.shared.constants.ErrorCode;

public interface ResponseBuilder {
  ResponseEntity<ApiResponse<Object>> successResponse(Object data, String message);

  ResponseEntity<ApiResponse<Object>> successResponse(Object data);

  ResponseEntity<ApiResponse<Object>> successResponse(String message);

  ResponseEntity<ApiResponse<Object>> successResponse(
      Object data, String message, HttpStatus status);

  ResponseEntity<ApiResponse<Object>> successResponse(
      Object data, String message, ApiResponse.ResponseMetadata responseMetadata);

  <T> ResponseEntity<ApiResponse<Object>> paginatedResponse(Page<T> page, String message);

  ResponseEntity<ApiResponse<Object>> errorResponse(ErrorCode errorCode, HttpStatus status);

  ResponseEntity<ApiResponse<Object>> errorResponse(
      ErrorCode errorCode, String customMessage, HttpStatus status);

  ResponseEntity<ApiResponse<Object>> errorResponse(
      ErrorCode errorCode, String customMessage, String details, HttpStatus status);
}
