package connxt.systemrole.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;

import connxt.shared.constants.ErrorCode;
import connxt.systemrole.dto.SystemRoleDto;
import connxt.systemrole.entity.SystemRole;
import connxt.systemrole.repository.SystemRoleRepository;
import connxt.systemrole.service.SystemRoleService;
import connxt.systemrole.service.mappers.SystemRoleMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SystemRoleServiceImpl implements SystemRoleService {

  private final SystemRoleRepository systemRoleRepository;
  private final SystemRoleMapper systemRoleMapper;
  private final ObjectMapper objectMapper;

  @Override
  @Transactional
  public SystemRoleDto create(SystemRoleDto dto) {
    verifyRoleNameUnique(dto.getName());
    SystemRole systemRole = systemRoleMapper.toSystemRole(dto);
    SystemRole savedSystemRole = systemRoleRepository.save(systemRole);
    return systemRoleMapper.toSystemRoleDto(savedSystemRole);
  }

  @Override
  public List<SystemRoleDto> readAll() {
    return systemRoleRepository.findAll().stream().map(systemRoleMapper::toSystemRoleDto).toList();
  }

  @Override
  public SystemRoleDto read(String id) {
    SystemRole systemRole =
        systemRoleRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorCode.ROLE_NOT_FOUND.getCode()));
    return systemRoleMapper.toSystemRoleDto(systemRole);
  }

  @Override
  @Transactional
  public SystemRoleDto update(SystemRoleDto dto) {
    SystemRole existingSystemRole =
        systemRoleRepository
            .findById(dto.getId())
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorCode.ROLE_NOT_FOUND.getCode()));

    String roleName = dto.getName();
    if (roleName != null && !roleName.equals(existingSystemRole.getName())) {
      verifyRoleNameUnique(roleName);
    }

    systemRoleMapper.toUpdateSystemRole(dto, existingSystemRole);
    SystemRole systemRole = systemRoleRepository.save(existingSystemRole);
    return systemRoleMapper.toSystemRoleDto(systemRole);
  }

  @Override
  @Transactional
  public void delete(String id) {
    verifyRoleExists(id);
    systemRoleRepository.deleteById(id);
  }

  @Override
  public Map<String, Object> getRolePermissions(String roleId) {
    try {
      return systemRoleRepository
          .findById(roleId)
          .map(SystemRole::getPermissions)
          .filter(permissions -> permissions != null)
          .map(permissions -> permissions.toString().trim())
          .filter(json -> !json.isEmpty())
          .map(
              json -> {
                try {
                  @SuppressWarnings("unchecked")
                  Map<String, Object> result = objectMapper.readValue(json, Map.class);
                  return result;
                } catch (Exception e) {
                  log.debug(
                      "Failed to parse permissions JSON for role {}: {}", roleId, e.getMessage());
                  return null;
                }
              })
          .orElse(null);
    } catch (Exception e) {
      log.debug(
          "Unexpected error while retrieving permissions for role {}: {}", roleId, e.getMessage());
      return null;
    }
  }

  private void verifyRoleExists(String id) {
    if (!systemRoleRepository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.ROLE_NOT_FOUND.getCode());
    }
  }

  private void verifyRoleNameUnique(String name) {
    if (systemRoleRepository.existsByName(name)) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, ErrorCode.DUPLICATE_RESOURCE.getCode());
    }
  }
}
