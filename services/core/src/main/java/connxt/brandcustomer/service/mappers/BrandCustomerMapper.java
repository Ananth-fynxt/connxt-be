package connxt.brandcustomer.service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import connxt.brandcustomer.dto.BrandCustomerDto;
import connxt.brandcustomer.entity.BrandCustomer;
import connxt.shared.db.mappers.MapperCoreConfig;

@Mapper(config = MapperCoreConfig.class)
public interface BrandCustomerMapper {
  BrandCustomerDto toBrandCustomerDto(BrandCustomer brandCustomer);

  BrandCustomer toBrandCustomer(BrandCustomerDto brandCustomerDto);

  void toUpdateBrandCustomer(
      BrandCustomerDto brandCustomerDto, @MappingTarget BrandCustomer brandCustomer);
}
