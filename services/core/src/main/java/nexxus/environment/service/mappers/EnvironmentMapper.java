package nexxus.environment.service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import nexxus.environment.dto.EnvironmentDto;
import nexxus.environment.entity.Environment;
import nexxus.shared.db.mappers.MapperCoreConfig;

@Mapper(config = MapperCoreConfig.class)
public interface EnvironmentMapper {

  @Mapping(target = "id", source = "environment.id")
  @Mapping(target = "name", source = "environment.name")
  @Mapping(target = "brandId", source = "environment.brandId")
  @Mapping(target = "origin", source = "environment.origin")
  @Mapping(target = "successRedirectUrl", source = "environment.successRedirectUrl")
  @Mapping(target = "failureRedirectUrl", source = "environment.failureRedirectUrl")
  @Mapping(target = "secret", source = "environment.secret")
  @Mapping(target = "token", source = "environment.token")
  @Mapping(target = "createdAt", source = "environment.createdAt")
  @Mapping(target = "updatedAt", source = "environment.updatedAt")
  @Mapping(target = "createdBy", source = "environment.createdBy")
  @Mapping(target = "updatedBy", source = "environment.updatedBy")
  EnvironmentDto toEnvironmentDto(Environment environment);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "secret", ignore = true)
  @Mapping(target = "token", ignore = true)
  Environment toEnvironment(EnvironmentDto environmentDto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "secret", ignore = true)
  @Mapping(target = "token", ignore = true)
  void toUpdateEnvironment(EnvironmentDto environmentDto, @MappingTarget Environment environment);
}
