package connxt.systemrole.service;

import java.util.List;
import java.util.Map;

import connxt.systemrole.dto.SystemRoleDto;

public interface SystemRoleService {

  SystemRoleDto create(SystemRoleDto systemRoleDto);

  List<SystemRoleDto> readAll();

  SystemRoleDto read(String id);

  SystemRoleDto update(SystemRoleDto systemRoleDto);

  void delete(String id);

  Map<String, Object> getRolePermissions(String roleId);
}
