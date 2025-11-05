package connxt.brand.service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import connxt.brand.dto.BrandDto;
import connxt.brand.entity.Brand;
import connxt.shared.db.mappers.MapperCoreConfig;

@Mapper(config = MapperCoreConfig.class)
public interface BrandMapper {
  BrandDto toBrandDto(Brand brand);

  Brand toBrand(BrandDto brandDto);

  void toUpdateBrand(BrandDto brandDto, @MappingTarget Brand brand);
}
