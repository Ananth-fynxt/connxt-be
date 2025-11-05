package connxt.branduser.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import connxt.shared.constants.Scope;
import connxt.shared.constants.UserStatus;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrandUserDto {
  private String id;

  @NotBlank(message = "Brand ID is required")
  private String brandId;

  @NotBlank(message = "Environment ID is required")
  private String environmentId;

  @NotBlank(message = "Brand Role ID is required")
  private String brandRoleId;

  @NotBlank(message = "Name is required")
  private String name;

  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  private String email;

  private String userId;

  @Builder.Default private Scope scope = Scope.BRAND;

  @Builder.Default private UserStatus status = UserStatus.ACTIVE;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedAt;
}
