package connxt.fi.dto;

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
public class FiDto {
  private String id;

  @NotBlank(message = "FI name is required")
  private String name;

  @NotBlank(message = "FI email is required")
  @Email(message = "Invalid email format")
  private String email;

  private String userId;

  @Builder.Default private Scope scope = Scope.SYSTEM;

  @Builder.Default private UserStatus status = UserStatus.ACTIVE;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedAt;
}
