package nexxus.branduser.service;

import java.util.List;

import nexxus.branduser.dto.BrandUserDto;

public interface BrandUserService {

  BrandUserDto create(BrandUserDto brandUserDto);

  List<BrandUserDto> readAll();

  List<BrandUserDto> readAll(String brandId, String environmentId);

  BrandUserDto read(String id);

  BrandUserDto update(BrandUserDto dto);

  void delete(String id);

  List<BrandUserDto> findByUserId(String userId);

  boolean hasAccessToEnvironment(
      String userId, String brandId, String environmentId, String roleId);
}
