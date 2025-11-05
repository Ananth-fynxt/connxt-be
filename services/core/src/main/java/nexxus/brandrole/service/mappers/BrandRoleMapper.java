package nexxus.brandrole.service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import nexxus.brandrole.dto.BrandRoleDto;
import nexxus.brandrole.entity.BrandRole;
import nexxus.shared.db.mappers.MapperCoreConfig;

@Mapper(config = MapperCoreConfig.class)
public interface BrandRoleMapper {
  BrandRoleDto toBrandRoleDto(BrandRole brandRole);

  BrandRole toBrandRole(BrandRoleDto brandRoleDto);

  void toUpdateBrandRole(BrandRoleDto brandRoleDto, @MappingTarget BrandRole brandRole);
}
