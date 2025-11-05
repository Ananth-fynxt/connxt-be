package nexxus.branduser.service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import nexxus.branduser.dto.BrandUserDto;
import nexxus.branduser.entity.BrandUser;
import nexxus.shared.db.mappers.MapperCoreConfig;

@Mapper(config = MapperCoreConfig.class)
public interface BrandUserMapper {
  BrandUserDto toBrandUserDto(BrandUser brandUser);

  BrandUser toBrandUser(BrandUserDto brandUserDto);

  void toUpdateBrandUser(BrandUserDto brandUserDto, @MappingTarget BrandUser brandUser);
}
