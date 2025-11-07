package connxt.auth.dto;

import connxt.shared.constants.Scope;
import connxt.shared.constants.UserStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {

  private String userId;
  private String email;
  private Scope scope;
  private UserStatus status;
  private String authType;
  private String roleId;
}
