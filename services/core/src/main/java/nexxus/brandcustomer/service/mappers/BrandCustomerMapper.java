package nexxus.brandcustomer.service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import nexxus.brandcustomer.dto.BrandCustomerDto;
import nexxus.brandcustomer.entity.BrandCustomer;
import nexxus.shared.db.mappers.MapperCoreConfig;

@Mapper(config = MapperCoreConfig.class)
public interface BrandCustomerMapper {
  BrandCustomerDto toBrandCustomerDto(BrandCustomer brandCustomer);

  BrandCustomer toBrandCustomer(BrandCustomerDto brandCustomerDto);

  void toUpdateBrandCustomer(
      BrandCustomerDto brandCustomerDto, @MappingTarget BrandCustomer brandCustomer);
}
