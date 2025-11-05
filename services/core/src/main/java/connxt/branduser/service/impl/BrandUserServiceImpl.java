package connxt.branduser.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import connxt.brandrole.repository.BrandRoleRepository;
import connxt.branduser.dto.BrandUserDto;
import connxt.branduser.entity.BrandUser;
import connxt.branduser.repository.BrandUserRepository;
import connxt.branduser.service.BrandUserService;
import connxt.branduser.service.mappers.BrandUserMapper;
import connxt.shared.constants.ErrorCode;
import connxt.user.dto.UserRequest;
import connxt.user.service.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BrandUserServiceImpl implements BrandUserService {

  private final BrandUserRepository brandUserRepository;
  private final BrandUserMapper brandUserMapper;
  private final BrandRoleRepository brandRoleRepository;
  private final UserService userService;

  @Override
  @Transactional
  public BrandUserDto create(BrandUserDto dto) {
    verifyBrandRoleExists(dto.getBrandRoleId());
    verifyBrandUserEmailExists(dto.getBrandId(), dto.getEnvironmentId(), dto.getEmail());

    UserRequest createUserRequest = UserRequest.builder().email(dto.getEmail()).build();

    UserRequest createdUser = userService.createUser(createUserRequest);

    BrandUser brandUser = brandUserMapper.toBrandUser(dto);
    brandUser.setUserId(createdUser.getId());

    return brandUserMapper.toBrandUserDto(brandUserRepository.save(brandUser));
  }

  @Override
  public List<BrandUserDto> readAll() {
    return brandUserRepository.findAll().stream().map(brandUserMapper::toBrandUserDto).toList();
  }

  @Override
  public List<BrandUserDto> readAll(String brandId, String environmentId) {
    return brandUserRepository.findByBrandIdAndEnvironmentId(brandId, environmentId).stream()
        .map(brandUserMapper::toBrandUserDto)
        .toList();
  }

  @Override
  public BrandUserDto read(String id) {
    BrandUser brandUser =
        brandUserRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorCode.BRAND_USER_NOT_FOUND.getCode()));
    return brandUserMapper.toBrandUserDto(brandUser);
  }

  @Override
  @Transactional
  public BrandUserDto update(BrandUserDto dto) {
    verifyBrandRoleExists(dto.getBrandRoleId());
    BrandUser existingBrandUser =
        brandUserRepository
            .findById(dto.getId())
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorCode.BRAND_USER_NOT_FOUND.getCode()));
    brandUserMapper.toUpdateBrandUser(dto, existingBrandUser);
    BrandUser brandUser = brandUserRepository.save(existingBrandUser);
    return brandUserMapper.toBrandUserDto(brandUser);
  }

  @Override
  @Transactional
  public void delete(String id) {
    verifyBrandUserExists(id);
    brandUserRepository.deleteById(id);
  }

  private void verifyBrandUserExists(String id) {
    if (!brandUserRepository.existsById(id)) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, ErrorCode.BRAND_USER_NOT_FOUND.getCode());
    }
  }

  private void verifyBrandRoleExists(String brandRoleId) {
    if (!brandRoleRepository.existsById(brandRoleId)) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, ErrorCode.BRAND_ROLE_NOT_FOUND.getCode());
    }
  }

  private void verifyBrandUserEmailExists(String brandId, String environmentId, String email) {
    if (brandUserRepository.existsByBrandIdAndEnvironmentIdAndEmail(
        brandId, environmentId, email)) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, ErrorCode.BRAND_USER_ALREADY_EXISTS.getCode());
    }
  }

  @Override
  public List<BrandUserDto> findByUserId(String userId) {
    List<BrandUser> brandUsers = brandUserRepository.findByUserId(userId);
    return brandUsers.stream().map(brandUserMapper::toBrandUserDto).toList();
  }

  @Override
  public boolean hasAccessToEnvironment(
      String userId, String brandId, String environmentId, String roleId) {
    return brandUserRepository.existsByUserIdAndBrandIdAndEnvironmentIdAndBrandRoleId(
        userId, brandId, environmentId, roleId);
  }
}
