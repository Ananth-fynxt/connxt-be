package connxt.brandcustomer.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;

import connxt.shared.constants.Scope;
import connxt.shared.constants.UserStatus;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrandCustomerDto {
  private String id;

  @NotBlank(message = "Brand ID is required")
  private String brandId;

  @NotBlank(message = "Environment ID is required")
  private String environmentId;

  @NotBlank(message = "Name is required")
  private String name;

  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  private String email;

  private String tag;

  private String accountType;

  @NotBlank(message = "Country is required")
  private String country;

  @NotEmpty(message = "Currency is required")
  private String[] currencies;

  @Builder.Default private JsonNode customerMeta = null;

  @Builder.Default private UserStatus status = UserStatus.ACTIVE;

  @Builder.Default private Scope scope = Scope.EXTERNAL;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedAt;
}
