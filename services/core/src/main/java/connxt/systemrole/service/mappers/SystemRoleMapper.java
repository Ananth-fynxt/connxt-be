package connxt.systemrole.service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import connxt.shared.db.mappers.MapperCoreConfig;
import connxt.systemrole.dto.SystemRoleDto;
import connxt.systemrole.entity.SystemRole;

@Mapper(config = MapperCoreConfig.class)
public interface SystemRoleMapper {

  SystemRoleDto toSystemRoleDto(SystemRole systemRole);

  SystemRole toSystemRole(SystemRoleDto systemRoleDto);

  void toUpdateSystemRole(SystemRoleDto systemRoleDto, @MappingTarget SystemRole systemRole);
}
