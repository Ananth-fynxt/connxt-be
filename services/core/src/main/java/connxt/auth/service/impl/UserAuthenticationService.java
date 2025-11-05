package connxt.auth.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import connxt.auth.dto.UserInfo;
import connxt.shared.constants.ErrorCode;
import connxt.shared.constants.Scope;
import connxt.shared.util.CryptoUtil;
import connxt.systemuser.dto.SystemUserDto;
import connxt.systemuser.service.SystemUserService;
import connxt.user.entity.User;
import connxt.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAuthenticationService {

  private final UserService userService;
  private final SystemUserService systemUserService;
  private final CryptoUtil cryptoUtil;

  public UserInfo authenticateUser(String email, String password) {
    log.debug("Authenticating system user");

    User user = userService.findByEmailForAuthentication(email);
    validateUserPassword(user, password, email);

    return buildUserInfo(user);
  }

  private UserInfo buildSystemUserInfo(User user, SystemUserDto systemUserDto) {
    return UserInfo.builder()
        .userId(user.getId())
        .email(user.getEmail())
        .scope(Scope.SYSTEM)
        .status(systemUserDto.getStatus())
        .authType("INTERNAL")
        .build();
  }

  public UserInfo getUserInfoById(String userId) {
    log.debug("Fetching user info by ID: {}", userId);

    User user = userService.findByIdForAuthentication(userId);
    return buildUserInfo(user);
  }

  private void validateUserPassword(User user, String password, String email) {
    try {
      String decryptedPassword = cryptoUtil.decrypt(user.getPassword());
      if (!password.equals(decryptedPassword)) {
        throw new ResponseStatusException(
            HttpStatus.UNAUTHORIZED, ErrorCode.AUTH_INVALID_CREDENTIALS.getCode());
      }
    } catch (Exception e) {
      log.error("Failed to verify password for user: {}", email, e);
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED, ErrorCode.AUTH_INVALID_CREDENTIALS.getCode());
    }
  }

  private UserInfo buildUserInfo(User user) {
    log.debug("Building user info for user ID: {}, email: {}", user.getId(), user.getEmail());

    // Only system users are allowed
    SystemUserDto systemUserDto = systemUserService.findByUserId(user.getId());
    log.debug("System user lookup result: {}", systemUserDto != null ? "found" : "not found");
    if (systemUserDto != null) {
      log.debug("User is a system user, building system user info");
      return buildSystemUserInfo(user, systemUserDto);
    }

    // User exists but is not a system user
    throw new ResponseStatusException(HttpStatus.FORBIDDEN, ErrorCode.USER_NO_ACCESS.getCode());
  }
}
