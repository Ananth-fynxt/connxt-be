package connxt.systemuser.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import connxt.shared.constants.ErrorCode;
import connxt.systemuser.dto.SystemUserDto;
import connxt.systemuser.entity.SystemUser;
import connxt.systemuser.repository.SystemUserRepository;
import connxt.systemuser.service.SystemUserService;
import connxt.systemuser.service.mappers.SystemUserMapper;
import connxt.user.dto.UserRequest;
import connxt.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SystemUserServiceImpl implements SystemUserService {

  private final SystemUserRepository systemUserRepository;
  private final SystemUserMapper mapper;
  private final UserService userService;

  @Override
  @Transactional
  public SystemUserDto createSystemUser(SystemUserDto dto) {
    log.info("Creating system user with email: {}", dto.getEmail());

    verifySystemUserEmailNotExists(dto.getEmail());

    UserRequest createUserRequest = UserRequest.builder().email(dto.getEmail()).build();
    UserRequest createdUser = userService.createUser(createUserRequest);

    SystemUser systemUser = mapper.toEntity(dto);
    systemUser.setUserId(createdUser.getId());

    SystemUser savedSystemUser = systemUserRepository.save(systemUser);
    log.info("System user created successfully with ID: {}", savedSystemUser.getId());

    return mapper.toDto(savedSystemUser);
  }

  @Override
  public SystemUserDto getSystemUserById(String id) {
    log.debug("Retrieving system user by ID: {}", id);

    SystemUser systemUser =
        systemUserRepository
            .findById(id)
            .orElseThrow(
                () -> {
                  log.warn("System user not found with ID: {}", id);
                  return new ResponseStatusException(
                      HttpStatus.NOT_FOUND, ErrorCode.RESOURCE_NOT_FOUND.getCode());
                });

    return mapper.toDto(systemUser);
  }

  @Override
  public SystemUserDto getSystemUserByEmail(String email) {
    log.debug("Retrieving system user by email: {}", email);

    SystemUser systemUser =
        systemUserRepository
            .findByEmail(email)
            .orElseThrow(
                () -> {
                  log.warn("System user not found with email: {}", email);
                  return new ResponseStatusException(
                      HttpStatus.NOT_FOUND, ErrorCode.RESOURCE_NOT_FOUND.getCode());
                });

    return mapper.toDto(systemUser);
  }

  @Override
  public List<SystemUserDto> getAllSystemUsers() {
    log.debug("Retrieving all system users");

    return systemUserRepository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
  }

  @Override
  @Transactional
  public SystemUserDto updateSystemUser(String id, SystemUserDto dto) {
    log.info("Updating system user with ID: {}", id);

    SystemUser existingUser =
        systemUserRepository
            .findById(id)
            .orElseThrow(
                () -> {
                  log.warn("System user not found with ID: {}", id);
                  return new ResponseStatusException(
                      HttpStatus.NOT_FOUND, ErrorCode.RESOURCE_NOT_FOUND.getCode());
                });

    if (!existingUser.getEmail().equals(dto.getEmail())
        && systemUserRepository.existsByEmail(dto.getEmail())) {
      log.warn("Email {} is already in use by another system user", dto.getEmail());
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, ErrorCode.DUPLICATE_RESOURCE.getCode());
    }

    existingUser.setName(dto.getName());
    existingUser.setEmail(dto.getEmail());
    existingUser.setRole(dto.getRole());
    existingUser.setStatus(dto.getStatus());

    SystemUser updatedUser = systemUserRepository.save(existingUser);
    log.info("System user updated successfully: {}", id);

    return mapper.toDto(updatedUser);
  }

  @Override
  @Transactional
  public void deleteSystemUser(String id) {
    log.info("Deleting system user with ID: {}", id);

    verifySystemUserExists(id);
    systemUserRepository.deleteById(id);

    log.info("System user deleted successfully: {}", id);
  }

  @Override
  public boolean existsByEmail(String email) {
    return systemUserRepository.existsByEmail(email);
  }

  @Override
  public SystemUserDto findByUserId(String userId) {
    log.debug("Retrieving system user by user ID: {}", userId);

    return systemUserRepository.findByUserId(userId).map(mapper::toDto).orElse(null);
  }

  private void verifySystemUserExists(String id) {
    if (!systemUserRepository.existsById(id)) {
      log.warn("System user not found with ID: {}", id);
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, ErrorCode.RESOURCE_NOT_FOUND.getCode());
    }
  }

  private void verifySystemUserEmailNotExists(String email) {
    if (systemUserRepository.existsByEmail(email)) {
      log.warn("System user with email {} already exists", email);
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, ErrorCode.DUPLICATE_RESOURCE.getCode());
    }
  }
}
