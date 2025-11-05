package nexxus.shared.dto;

import org.springframework.http.ResponseEntity;

import nexxus.shared.builder.dto.ApiResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the result of a validation operation. Contains success/failure status and optional
 * error response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationResult {

  private boolean success;
  private ResponseEntity<ApiResponse<Object>> errorResponse;

  /** Create a successful validation result */
  public static ValidationResult success() {
    return ValidationResult.builder().success(true).build();
  }

  /** Create a failed validation result with error response */
  public static ValidationResult failure(ResponseEntity<ApiResponse<Object>> errorResponse) {
    return ValidationResult.builder().success(false).errorResponse(errorResponse).build();
  }

  /** Check if validation was successful */
  public boolean isSuccess() {
    return success;
  }

  /** Check if validation failed */
  public boolean isFailure() {
    return !success;
  }
}
