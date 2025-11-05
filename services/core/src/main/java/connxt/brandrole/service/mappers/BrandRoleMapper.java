package connxt.brandrole.service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import connxt.brandrole.dto.BrandRoleDto;
import connxt.brandrole.entity.BrandRole;
import connxt.shared.db.mappers.MapperCoreConfig;

@Mapper(config = MapperCoreConfig.class)
public interface BrandRoleMapper {
  BrandRoleDto toBrandRoleDto(BrandRole brandRole);

  BrandRole toBrandRole(BrandRoleDto brandRoleDto);

  void toUpdateBrandRole(BrandRoleDto brandRoleDto, @MappingTarget BrandRole brandRole);
}
