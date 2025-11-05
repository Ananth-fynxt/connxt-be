package nexxus.shared.builder.dto;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standardized API response wrapper for all Nexxus services Provides consistent structure for both
 * success and error responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
  @JsonProperty("timestamp")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
  private OffsetDateTime timestamp;

  @JsonProperty("code")
  private String code;

  @JsonProperty("message")
  private String message;

  @JsonProperty("data")
  private T data;

  @JsonProperty("error")
  private ErrorDetails error;

  @JsonProperty("metadata")
  private ResponseMetadata metadata;

  /** Create a successful response with data */
  public static <T> ApiResponse<T> success(T data) {
    return ApiResponse.<T>builder()
        .timestamp(OffsetDateTime.now(ZoneOffset.UTC))
        .code("200")
        .message("Success")
        .data(data)
        .build();
  }

  /** Create a successful response with custom message */
  public static <T> ApiResponse<T> success(T data, String message) {
    return ApiResponse.<T>builder()
        .timestamp(OffsetDateTime.now(ZoneOffset.UTC))
        .code("200")
        .message(message)
        .data(data)
        .build();
  }

  /** Create a successful response without data */
  public static <T> ApiResponse<T> success(String message) {
    return ApiResponse.<T>builder()
        .timestamp(OffsetDateTime.now(ZoneOffset.UTC))
        .code("200")
        .message(message)
        .build();
  }

  /** Create an error response */
  public static <T> ApiResponse<T> error(String code, String message) {
    return ApiResponse.<T>builder()
        .timestamp(OffsetDateTime.now(ZoneOffset.UTC))
        .code(code)
        .message(message)
        .error(ErrorDetails.builder().code(code).message(message).build())
        .build();
  }

  /** Create an error response with detailed error information */
  public static <T> ApiResponse<T> error(String code, String message, String details) {
    return ApiResponse.<T>builder()
        .timestamp(OffsetDateTime.now(ZoneOffset.UTC))
        .code(code)
        .message(message)
        .error(ErrorDetails.builder().code(code).message(message).details(details).build())
        .build();
  }

  /** Create an error response with validation errors */
  public static <T> ApiResponse<T> error(
      String code, String message, String details, Object validationErrors) {
    return ApiResponse.<T>builder()
        .timestamp(OffsetDateTime.now(ZoneOffset.UTC))
        .code(code)
        .message(message)
        .error(
            ErrorDetails.builder()
                .code(code)
                .message(message)
                .details(details)
                .validationErrors(validationErrors)
                .build())
        .build();
  }

  /** Error details for failed responses */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static class ErrorDetails {
    @JsonProperty("code")
    private String code;

    @JsonProperty("message")
    private String message;

    @JsonProperty("details")
    private String details;

    @JsonProperty("validationErrors")
    private Object validationErrors;

    @JsonProperty("timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private OffsetDateTime timestamp;

    public ErrorDetails(String code, String message) {
      this.code = code;
      this.message = message;
      this.timestamp = OffsetDateTime.now(ZoneOffset.UTC);
    }
  }

  /** Response metadata for additional information */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static class ResponseMetadata {
    @JsonProperty("requestId")
    private String requestId;

    @JsonProperty("processingTime")
    private Long processingTime;

    @JsonProperty("pagination")
    private PaginationInfo pagination;

    @JsonProperty("version")
    private String version;

    @JsonProperty("service")
    private String service;
  }

  /** Pagination information for list responses */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static class PaginationInfo {
    @JsonProperty("page")
    private Integer page;

    @JsonProperty("size")
    private Integer size;

    @JsonProperty("totalElements")
    private Long totalElements;

    @JsonProperty("totalPages")
    private Integer totalPages;

    @JsonProperty("hasNext")
    private Boolean hasNext;

    @JsonProperty("hasPrevious")
    private Boolean hasPrevious;
  }
}
