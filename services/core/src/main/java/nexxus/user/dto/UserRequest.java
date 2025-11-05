package nexxus.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

  private String id;

  @NotBlank(message = "Email is required")
  @Email(message = "Email must be valid")
  private String email;
}
