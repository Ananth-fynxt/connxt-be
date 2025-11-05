package connxt.branduser.service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import connxt.branduser.dto.BrandUserDto;
import connxt.branduser.entity.BrandUser;
import connxt.shared.db.mappers.MapperCoreConfig;

@Mapper(config = MapperCoreConfig.class)
public interface BrandUserMapper {
  BrandUserDto toBrandUserDto(BrandUser brandUser);

  BrandUser toBrandUser(BrandUserDto brandUserDto);

  void toUpdateBrandUser(BrandUserDto brandUserDto, @MappingTarget BrandUser brandUser);
}
