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

    // Only system users can login
    SystemUserDto systemUserDto = systemUserService.findByUserId(user.getId());
    if (systemUserDto == null) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, ErrorCode.USER_NO_ACCESS.getCode());
    }

    return buildSystemUserInfo(user, systemUserDto);
  }

  private UserInfo buildSystemUserInfo(User user, SystemUserDto systemUserDto) {
    return UserInfo.builder()
        .userId(user.getId())
        .email(user.getEmail())
        .scope(Scope.SYSTEM)
        .status(systemUserDto.getStatus())
        .authType("INTERNAL")
        .roleId(null) // System users don't have roleId yet - can be added later if needed
        .build();
  }

  public UserInfo getUserInfoById(String userId) {
    log.debug("Fetching system user info by ID: {}", userId);

    User user = userService.findByIdForAuthentication(userId);
    SystemUserDto systemUserDto = systemUserService.findByUserId(user.getId());
    if (systemUserDto == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND.getCode());
    }

    return buildSystemUserInfo(user, systemUserDto);
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
}
