package nexxus.brand.service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import nexxus.brand.dto.BrandDto;
import nexxus.brand.entity.Brand;
import nexxus.shared.db.mappers.MapperCoreConfig;

@Mapper(config = MapperCoreConfig.class)
public interface BrandMapper {
  BrandDto toBrandDto(Brand brand);

  Brand toBrand(BrandDto brandDto);

  void toUpdateBrand(BrandDto brandDto, @MappingTarget Brand brand);
}
