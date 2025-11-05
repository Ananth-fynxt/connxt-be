package nexxus.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalLoginRequest {

  @NotBlank(message = "Customer ID is required")
  private String customerId;

  @NotBlank(message = "Secret token is required")
  private String secretToken;
}
