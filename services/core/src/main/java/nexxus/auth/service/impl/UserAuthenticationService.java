package nexxus.auth.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import nexxus.auth.dto.CustomerInfo;
import nexxus.auth.dto.UserInfo;
import nexxus.brand.dto.BrandDto;
import nexxus.brand.service.BrandService;
import nexxus.brandcustomer.dto.BrandCustomerDto;
import nexxus.brandcustomer.service.BrandCustomerService;
import nexxus.branduser.dto.BrandUserDto;
import nexxus.branduser.service.BrandUserService;
import nexxus.environment.dto.EnvironmentDto;
import nexxus.environment.service.EnvironmentService;
import nexxus.fi.dto.FiDto;
import nexxus.fi.service.FiService;
import nexxus.shared.constants.ErrorCode;
import nexxus.shared.constants.Scope;
import nexxus.shared.constants.UserStatus;
import nexxus.shared.util.CryptoUtil;
import nexxus.systemuser.dto.SystemUserDto;
import nexxus.systemuser.service.SystemUserService;
import nexxus.user.entity.User;
import nexxus.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAuthenticationService {

  private final UserService userService;
  private final FiService fiService;
  private final BrandService brandService;
  private final BrandUserService brandUserService;
  private final BrandCustomerService brandCustomerService;
  private final EnvironmentService environmentService;
  private final SystemUserService systemUserService;
  private final CryptoUtil cryptoUtil;

  public UserInfo authenticateUser(String email, String password) {
    log.debug("Authenticating user");

    User user = userService.findByEmailForAuthentication(email);
    validateUserPassword(user, password, email);

    return buildUserInfo(user);
  }

  public CustomerInfo authenticateCustomer(String customerId, String secretToken) {
    log.debug("Authenticating customer");

    EnvironmentDto environmentDto = environmentService.findBySecret(secretToken);
    return getCustomerInfoById(customerId, environmentDto.getBrandId(), environmentDto.getId());
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

  private UserInfo buildFiUserInfo(User user, FiDto fiDto) {
    List<BrandDto> brandDtos = brandService.findByFiId(fiDto.getId());
    List<UserInfo.BrandInfo> brandInfos = buildBrandInfoList(brandDtos, null);

    return UserInfo.builder()
        .userId(user.getId())
        .email(user.getEmail())
        .scope(Scope.FI)
        .status(fiDto.getStatus())
        .authType("INTERNAL")
        .fiId(fiDto.getId())
        .fiName(fiDto.getName())
        .brands(brandInfos)
        .build();
  }

  private UserInfo buildBrandUserInfo(User user, List<BrandUserDto> brandUserDtos) {
    List<BrandDto> accessibleBrandDtos = brandService.findByUserId(user.getId());
    List<UserInfo.BrandInfo> brandInfos = buildBrandInfoList(accessibleBrandDtos, brandUserDtos);
    UserStatus status =
        brandUserDtos.isEmpty() ? UserStatus.INVITED : brandUserDtos.get(0).getStatus();

    return UserInfo.builder()
        .userId(user.getId())
        .email(user.getEmail())
        .scope(Scope.BRAND)
        .status(status)
        .authType("INTERNAL")
        .accessibleBrands(brandInfos)
        .build();
  }

  private List<UserInfo.BrandInfo> buildBrandInfoList(
      List<BrandDto> brandDtos, List<BrandUserDto> brandUserDtos) {
    return brandDtos.stream()
        .map(brandDto -> buildBrandInfo(brandDto, brandUserDtos))
        .collect(Collectors.toList());
  }

  private UserInfo.BrandInfo buildBrandInfo(BrandDto brandDto, List<BrandUserDto> brandUserDtos) {
    List<EnvironmentDto> environments = environmentService.findByBrandId(brandDto.getId());
    List<UserInfo.EnvironmentInfo> environmentInfos =
        environments.stream()
            .map(env -> buildEnvironmentInfo(env, brandDto.getId(), brandUserDtos))
            .collect(Collectors.toList());

    return UserInfo.BrandInfo.builder()
        .id(brandDto.getId())
        .name(brandDto.getName())
        .environments(environmentInfos)
        .build();
  }

  private UserInfo.EnvironmentInfo buildEnvironmentInfo(
      EnvironmentDto environment, String brandId, List<BrandUserDto> brandUserDtos) {
    String roleId = null;

    if (brandUserDtos != null) {
      roleId =
          brandUserDtos.stream()
              .filter(
                  bu ->
                      bu.getBrandId().equals(brandId)
                          && bu.getEnvironmentId().equals(environment.getId()))
              .map(BrandUserDto::getBrandRoleId)
              .findFirst()
              .orElse(null);
    }

    return UserInfo.EnvironmentInfo.builder()
        .id(environment.getId())
        .name(environment.getName())
        .roleId(roleId)
        .build();
  }

  public UserInfo getUserInfoById(String userId) {
    log.debug("Fetching user info by ID: {}", userId);

    User user = userService.findByIdForAuthentication(userId);
    return buildUserInfo(user);
  }

  public CustomerInfo getCustomerInfoById(String customerId, String brandId, String environmentId) {
    log.debug("Fetching customer info by ID: {}", customerId);

    BrandCustomerDto customerDto =
        brandCustomerService.findByIdAndBrandIdAndEnvironmentId(customerId, brandId, environmentId);
    BrandDto brandDto = brandService.read(brandId);
    EnvironmentDto environmentDto = environmentService.read(environmentId);

    return buildCustomerInfo(customerDto, brandDto, environmentDto);
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

    // Check if user is a system user first
    SystemUserDto systemUserDto = systemUserService.findByUserId(user.getId());
    log.debug("System user lookup result: {}", systemUserDto != null ? "found" : "not found");
    if (systemUserDto != null) {
      log.debug("User is a system user, building system user info");
      return buildSystemUserInfo(user, systemUserDto);
    }

    try {
      FiDto fiDto = fiService.findByUserId(user.getId());
      return buildFiUserInfo(user, fiDto);
    } catch (ResponseStatusException e) {
      // User is not FI user, continue to check brand users
    }

    List<BrandUserDto> brandUserDtos = brandUserService.findByUserId(user.getId());
    if (!brandUserDtos.isEmpty()) {
      return buildBrandUserInfo(user, brandUserDtos);
    }

    // User exists but has no System, FI or Brand association
    throw new ResponseStatusException(HttpStatus.FORBIDDEN, ErrorCode.USER_NO_ACCESS.getCode());
  }

  private CustomerInfo buildCustomerInfo(
      BrandCustomerDto customerDto, BrandDto brandDto, EnvironmentDto environmentDto) {
    return CustomerInfo.builder()
        .customerId(customerDto.getId())
        .customerName(customerDto.getName())
        .customerEmail(customerDto.getEmail())
        .brandId(brandDto.getId())
        .brandName(brandDto.getName())
        .environmentId(environmentDto.getId())
        .environmentName(environmentDto.getName())
        .authType("EXTERNAL")
        .scope(Scope.EXTERNAL)
        .build();
  }
}
