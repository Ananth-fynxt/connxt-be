package connxt.systemuser.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import connxt.shared.constants.RoleType;
import connxt.shared.constants.Scope;
import connxt.shared.constants.UserStatus;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemUserDto {
  private String id;

  @NotBlank(message = "Name is required")
  private String name;

  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  private String email;

  private String userId;

  @NotNull(message = "Role is required")
  private RoleType role;

  @Builder.Default private Scope scope = Scope.SYSTEM;

  @Builder.Default private UserStatus status = UserStatus.ACTIVE;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedAt;
}
