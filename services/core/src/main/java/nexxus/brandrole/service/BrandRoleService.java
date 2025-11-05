package nexxus.brandrole.service;

import java.util.List;
import java.util.Map;

import nexxus.brandrole.dto.BrandRoleDto;

public interface BrandRoleService {

  BrandRoleDto create(BrandRoleDto brandRoleDto);

  List<BrandRoleDto> readAll();

  List<BrandRoleDto> readAll(String brandId, String environmentId);

  BrandRoleDto read(String id);

  BrandRoleDto update(BrandRoleDto dto);

  void delete(String id);

  Map<String, Object> getRolePermissions(String roleId);
}
