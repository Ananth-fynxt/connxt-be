package connxt.systemuser.service;

import java.util.List;

import connxt.systemuser.dto.SystemUserDto;

public interface SystemUserService {

  SystemUserDto createSystemUser(SystemUserDto dto);

  SystemUserDto getSystemUserById(String id);

  SystemUserDto getSystemUserByEmail(String email);

  List<SystemUserDto> getAllSystemUsers();

  SystemUserDto updateSystemUser(String id, SystemUserDto dto);

  void deleteSystemUser(String id);

  boolean existsByEmail(String email);

  SystemUserDto findByUserId(String userId);
}
