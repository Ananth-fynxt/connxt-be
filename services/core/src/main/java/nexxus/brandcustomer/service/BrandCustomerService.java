package nexxus.brandcustomer.service;

import java.util.List;

import nexxus.brandcustomer.dto.BrandCustomerDto;

public interface BrandCustomerService {

  BrandCustomerDto create(BrandCustomerDto brandCustomerDto);

  List<BrandCustomerDto> readAll();

  List<BrandCustomerDto> readAll(String brandId, String environmentId);

  BrandCustomerDto read(String id);

  BrandCustomerDto update(BrandCustomerDto dto);

  boolean validateBrandCustomerExists(String brandId, String environmentId, String customerId);

  BrandCustomerDto findByIdAndBrandIdAndEnvironmentId(
      String customerId, String brandId, String environmentId);
}
