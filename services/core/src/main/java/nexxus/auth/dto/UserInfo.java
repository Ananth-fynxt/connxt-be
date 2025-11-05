package nexxus.auth.dto;

import java.util.List;

import nexxus.shared.constants.Scope;
import nexxus.shared.constants.UserStatus;

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

  // For SYSTEM scope users
  private String fiId;
  private String fiName;
  private List<BrandInfo> brands;

  // For BRAND scope users
  private List<BrandInfo> accessibleBrands;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class BrandInfo {
    private String id;
    private String name;
    private List<EnvironmentInfo> environments;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class EnvironmentInfo {
    private String id;
    private String name;
    private String roleId;
  }
}
