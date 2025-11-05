package connxt.brandcustomer.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import connxt.brandcustomer.dto.BrandCustomerDto;
import connxt.brandcustomer.entity.BrandCustomer;
import connxt.brandcustomer.repository.BrandCustomerRepository;
import connxt.brandcustomer.service.BrandCustomerService;
import connxt.brandcustomer.service.mappers.BrandCustomerMapper;
import connxt.shared.constants.ErrorCode;
import connxt.wallet.service.WalletService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BrandCustomerServiceImpl implements BrandCustomerService {

  private final BrandCustomerRepository brandCustomerRepository;
  private final BrandCustomerMapper brandCustomerMapper;
  private final WalletService walletService;

  @Override
  @Transactional
  public BrandCustomerDto create(BrandCustomerDto dto) {
    verifyBrandCustomerEmailExistsForBrand(dto.getBrandId(), dto.getEmail());
    BrandCustomer brandCustomer = brandCustomerMapper.toBrandCustomer(dto);
    BrandCustomer savedCustomer = brandCustomerRepository.save(brandCustomer);

    // Create wallets for each currency
    walletService.createWalletsForCustomer(
        dto.getBrandId(), dto.getEnvironmentId(), savedCustomer.getId(), dto.getCurrencies());

    return brandCustomerMapper.toBrandCustomerDto(savedCustomer);
  }

  @Override
  public List<BrandCustomerDto> readAll() {
    return brandCustomerRepository.findAll().stream()
        .map(brandCustomerMapper::toBrandCustomerDto)
        .toList();
  }

  @Override
  public List<BrandCustomerDto> readAll(String brandId, String environmentId) {
    return brandCustomerRepository.findByBrandIdAndEnvironmentId(brandId, environmentId).stream()
        .map(brandCustomerMapper::toBrandCustomerDto)
        .toList();
  }

  @Override
  public BrandCustomerDto read(String id) {
    BrandCustomer brandCustomer =
        brandCustomerRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorCode.BRAND_CUSTOMER_NOT_FOUND.getCode()));
    return brandCustomerMapper.toBrandCustomerDto(brandCustomer);
  }

  @Override
  @Transactional
  public BrandCustomerDto update(BrandCustomerDto dto) {
    BrandCustomer existingBrandCustomer =
        brandCustomerRepository
            .findById(dto.getId())
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorCode.BRAND_CUSTOMER_NOT_FOUND.getCode()));
    brandCustomerMapper.toUpdateBrandCustomer(dto, existingBrandCustomer);
    BrandCustomer brandCustomer = brandCustomerRepository.save(existingBrandCustomer);

    // Create wallets for new currencies only (existing currencies will be skipped)
    walletService.createWalletsForCustomer(
        dto.getBrandId(), dto.getEnvironmentId(), brandCustomer.getId(), dto.getCurrencies());

    return brandCustomerMapper.toBrandCustomerDto(brandCustomer);
  }

  @Override
  public boolean validateBrandCustomerExists(
      String brandId, String environmentId, String customerId) {
    return brandCustomerRepository.existsByBrandIdAndEnvironmentIdAndId(
        brandId, environmentId, customerId);
  }

  private void verifyBrandCustomerEmailExistsForBrand(String brandId, String email) {
    if (brandCustomerRepository.existsByBrandIdAndEmail(brandId, email)) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, ErrorCode.BRAND_CUSTOMER_ALREADY_EXISTS.getCode());
    }
  }

  @Override
  public BrandCustomerDto findByIdAndBrandIdAndEnvironmentId(
      String customerId, String brandId, String environmentId) {
    BrandCustomer customer =
        brandCustomerRepository
            .findByIdAndBrandIdAndEnvironmentId(customerId, brandId, environmentId)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorCode.CUSTOMER_NOT_FOUND_BRAND.getCode()));
    return brandCustomerMapper.toBrandCustomerDto(customer);
  }
}
