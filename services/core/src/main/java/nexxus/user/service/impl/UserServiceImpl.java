package nexxus.user.service.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import nexxus.email.EmailService;
import nexxus.email.dto.EmailRequest;
import nexxus.shared.constants.ErrorCode;
import nexxus.shared.util.CryptoUtil;
import nexxus.user.dto.UpdatePasswordRequest;
import nexxus.user.dto.UserRequest;
import nexxus.user.entity.User;
import nexxus.user.repository.UserRepository;
import nexxus.user.service.UserService;
import nexxus.user.service.mappers.UserMapper;
import nexxus.user.util.PasswordUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final CryptoUtil cryptoUtil;
  private final PasswordUtil passwordUtil;
  private final EmailService emailService;

  @Value("${api.frontend-url}")
  private String frontendUrl;

  @Override
  @Transactional
  public UserRequest createUser(UserRequest request) {
    log.info("Creating user with email: {}", request.getEmail());

    if (userRepository.existsByEmail(request.getEmail())) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, ErrorCode.USER_ALREADY_EXISTS.getCode());
    }

    String generatedPassword = passwordUtil.generateStrongPassword();
    log.debug("Generated password: {}", generatedPassword);
    String hashedPassword;
    try {
      hashedPassword = cryptoUtil.encrypt(generatedPassword);
    } catch (Exception e) {
      log.error("Failed to hash password", e);
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.USER_PASSWORD_TOO_WEAK.getCode());
    }

    User user = userMapper.toUser(request);
    user.setPassword(hashedPassword);

    User savedUser = userRepository.save(user);

    log.info("User created successfully with ID: {}", savedUser.getId());

    sendUserCreationEmail(savedUser.getEmail(), generatedPassword);

    return userMapper.toUserRequest(savedUser);
  }

  @Override
  public UserRequest getUserById(String id) {
    log.debug("Fetching user by ID: {}", id);

    User user =
        userRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND.getCode()));

    return userMapper.toUserRequest(user);
  }

  @Override
  public UserRequest getUserByEmail(String email) {
    log.debug("Fetching user by email: {}", email);

    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND.getCode()));

    return userMapper.toUserRequest(user);
  }

  @Override
  @Transactional
  public UserRequest updatePassword(String userId, UpdatePasswordRequest request) {
    log.info("Updating password for user ID: {}", userId);

    User user =
        userRepository
            .findById(userId)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND.getCode()));

    try {
      String decryptedPassword = cryptoUtil.decrypt(user.getPassword());
      if (!request.getCurrentPassword().equals(decryptedPassword)) {
        throw new ResponseStatusException(
            HttpStatus.UNAUTHORIZED, ErrorCode.INVALID_CREDENTIALS.getCode());
      }
    } catch (Exception e) {
      log.error("Failed to verify current password", e);
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED, ErrorCode.INVALID_CREDENTIALS.getCode());
    }

    String newHashedPassword;
    try {
      newHashedPassword = cryptoUtil.encrypt(request.getNewPassword());
    } catch (Exception e) {
      log.error("Failed to hash new password", e);
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.USER_PASSWORD_TOO_WEAK.getCode());
    }

    userMapper.updateUserPassword(request, user);
    user.setPassword(newHashedPassword);
    User savedUser = userRepository.save(user);

    log.info("Password updated successfully for user ID: {}", userId);

    return userMapper.toUserRequest(savedUser);
  }

  @Override
  public User findByEmailForAuthentication(String email) {
    log.debug("Finding user by email for authentication: {}", email);

    return userRepository
        .findByEmail(email)
        .orElseThrow(
            () ->
                new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, ErrorCode.USER_NOT_FOUND.getCode()));
  }

  @Override
  public User findByIdForAuthentication(String id) {
    log.debug("Finding user by ID for authentication: {}", id);

    return userRepository
        .findById(id)
        .orElseThrow(
            () ->
                new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, ErrorCode.USER_NOT_FOUND.getCode()));
  }

  private void sendUserCreationEmail(String userEmail, String password) {
    try {
      log.info("Sending welcome email to newly created user: {}", userEmail);

      Map<String, Object> templateData = new HashMap<>();
      templateData.put("userEmail", userEmail);
      templateData.put("password", password);
      templateData.put("loginUrl", frontendUrl + "/login");
      templateData.put("supportEmail", "support@nexxus.fynxt.io");
      templateData.put("companyName", "Nexxus Platform");

      EmailRequest emailRequest =
          EmailRequest.builder()
              .recipients(Arrays.asList(userEmail))
              .templateId("welcome-email")
              .templateData(templateData)
              .description("Welcome email for new user: " + userEmail)
              .build();

      emailService.sendTemplatedEmail(emailRequest);

      log.info("Welcome email sent successfully to user: {}", userEmail);

    } catch (Exception e) {
      log.error(
          "Failed to send welcome email to user: {}, error: {}", userEmail, e.getMessage(), e);
    }
  }
}
