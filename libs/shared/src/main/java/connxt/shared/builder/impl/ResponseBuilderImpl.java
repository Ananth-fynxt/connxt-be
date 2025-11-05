package connxt.shared.builder.impl;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import connxt.shared.builder.ResponseBuilder;
import connxt.shared.builder.dto.ApiResponse;
import connxt.shared.constants.ErrorCode;

@Component
public class ResponseBuilderImpl implements ResponseBuilder {

  @Override
  public ResponseEntity<ApiResponse<Object>> successResponse(Object data, String message) {
    return buildSuccessResponse(data, message, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<ApiResponse<Object>> successResponse(Object data) {
    return buildSuccessResponse(data, "Operation completed successfully", HttpStatus.OK);
  }

  @Override
  public ResponseEntity<ApiResponse<Object>> successResponse(String message) {
    return buildSuccessResponse(null, message, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<ApiResponse<Object>> successResponse(
      Object data, String message, HttpStatus status) {
    return buildSuccessResponse(data, message, status);
  }

  @Override
  public ResponseEntity<ApiResponse<Object>> successResponse(
      Object data, String message, ApiResponse.ResponseMetadata responseMetadata) {
    return buildResponseEntity("200", message, data, responseMetadata, HttpStatus.OK);
  }

  @Override
  public <T> ResponseEntity<ApiResponse<Object>> paginatedResponse(Page<T> page, String message) {
    ApiResponse.ResponseMetadata metadata = buildPaginationMetadata(page);
    return buildResponseEntity("200", message, page.getContent(), metadata, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<ApiResponse<Object>> errorResponse(ErrorCode errorCode, HttpStatus status) {
    return buildFailureResponse(errorCode, null, errorCode.getMessage(), status);
  }

  @Override
  public ResponseEntity<ApiResponse<Object>> errorResponse(
      ErrorCode errorCode, String customMessage, HttpStatus status) {
    return buildFailureResponse(errorCode, null, customMessage, status);
  }

  @Override
  public ResponseEntity<ApiResponse<Object>> errorResponse(
      ErrorCode errorCode, String customMessage, String details, HttpStatus status) {
    return buildFailureResponse(errorCode, details, customMessage, status);
  }

  private ResponseEntity<ApiResponse<Object>> buildSuccessResponse(
      Object data, String message, HttpStatus status) {
    return buildResponseEntity(String.valueOf(status.value()), message, data, null, status);
  }

  private ResponseEntity<ApiResponse<Object>> buildFailureResponse(
      ErrorCode errorCode, Object data, String message, HttpStatus status) {
    return buildResponseEntity(errorCode.getCode(), message, data, null, status);
  }

  private ResponseEntity<ApiResponse<Object>> buildResponseEntity(
      String code,
      String message,
      Object details,
      ApiResponse.ResponseMetadata metadata,
      HttpStatus status) {

    ApiResponse<Object> response =
        ApiResponse.builder()
            .timestamp(OffsetDateTime.now(ZoneOffset.UTC))
            .code(code)
            .message(message)
            .data(details)
            .metadata(metadata)
            .build();

    return ResponseEntity.status(status).body(response);
  }

  private <T> ApiResponse.ResponseMetadata buildPaginationMetadata(Page<T> page) {
    ApiResponse.PaginationInfo paginationInfo =
        ApiResponse.PaginationInfo.builder()
            .page(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .hasNext(page.hasNext())
            .hasPrevious(page.hasPrevious())
            .build();

    return ApiResponse.ResponseMetadata.builder().pagination(paginationInfo).build();
  }
}
