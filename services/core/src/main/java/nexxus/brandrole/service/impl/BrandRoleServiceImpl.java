package nexxus.brandrole.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;

import nexxus.brandrole.dto.BrandRoleDto;
import nexxus.brandrole.entity.BrandRole;
import nexxus.brandrole.repository.BrandRoleRepository;
import nexxus.brandrole.service.BrandRoleService;
import nexxus.brandrole.service.mappers.BrandRoleMapper;
import nexxus.shared.constants.ErrorCode;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrandRoleServiceImpl implements BrandRoleService {

  private final BrandRoleRepository brandRoleRepository;
  private final BrandRoleMapper brandRoleMapper;
  private final ObjectMapper objectMapper;

  @Override
  @Transactional
  public BrandRoleDto create(BrandRoleDto dto) {
    verifyBrandRoleNameExistsForBrandAndEnvironment(
        dto.getBrandId(), dto.getEnvironmentId(), dto.getName());
    BrandRole brandRole = brandRoleMapper.toBrandRole(dto);
    return brandRoleMapper.toBrandRoleDto(brandRoleRepository.save(brandRole));
  }

  @Override
  public List<BrandRoleDto> readAll() {
    return brandRoleRepository.findAll().stream().map(brandRoleMapper::toBrandRoleDto).toList();
  }

  @Override
  public List<BrandRoleDto> readAll(String brandId, String environmentId) {
    return brandRoleRepository.findByBrandIdAndEnvironmentId(brandId, environmentId).stream()
        .map(brandRoleMapper::toBrandRoleDto)
        .toList();
  }

  @Override
  public BrandRoleDto read(String id) {
    BrandRole brandRole =
        brandRoleRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorCode.BRAND_ROLE_NOT_FOUND.getCode()));
    return brandRoleMapper.toBrandRoleDto(brandRole);
  }

  @Override
  @Transactional
  public BrandRoleDto update(BrandRoleDto dto) {
    BrandRole existingBrandRole =
        brandRoleRepository
            .findById(dto.getId())
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorCode.BRAND_ROLE_NOT_FOUND.getCode()));
    brandRoleMapper.toUpdateBrandRole(dto, existingBrandRole);
    BrandRole brandRole = brandRoleRepository.save(existingBrandRole);
    return brandRoleMapper.toBrandRoleDto(brandRole);
  }

  @Override
  @Transactional
  public void delete(String id) {
    verifyBrandRoleExists(id);
    brandRoleRepository.deleteById(id);
  }

  @Override
  public Map<String, Object> getRolePermissions(String roleId) {
    try {
      return brandRoleRepository
          .findById(roleId)
          .map(BrandRole::getPermission)
          .filter(permission -> permission != null)
          .map(permission -> permission.toString().trim())
          .filter(json -> !json.isEmpty())
          .map(
              json -> {
                try {
                  @SuppressWarnings("unchecked")
                  Map<String, Object> result = objectMapper.readValue(json, Map.class);
                  return result;
                } catch (Exception e) {
                  return null;
                }
              })
          .orElse(null);
    } catch (Exception e) {
      return null;
    }
  }

  private void verifyBrandRoleExists(String id) {
    if (!brandRoleRepository.existsById(id)) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, ErrorCode.BRAND_ROLE_NOT_FOUND.getCode());
    }
  }

  private void verifyBrandRoleNameExistsForBrandAndEnvironment(
      String brandId, String environmentId, String name) {
    if (brandRoleRepository.existsByBrandIdAndEnvironmentIdAndName(brandId, environmentId, name)) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, ErrorCode.BRAND_ROLE_ALREADY_EXISTS.getCode());
    }
  }
}
