package nexxus.brand.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import nexxus.brand.dto.BrandDto;
import nexxus.brand.entity.Brand;
import nexxus.brand.repository.BrandRepository;
import nexxus.brand.service.BrandService;
import nexxus.brand.service.mappers.BrandMapper;
import nexxus.environment.dto.EnvironmentDto;
import nexxus.environment.service.EnvironmentService;
import nexxus.fi.repository.FiRepository;
import nexxus.shared.constants.ErrorCode;
import nexxus.shared.service.NameUniquenessService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

  private final BrandRepository brandRepository;
  private final BrandMapper brandMapper;
  private final FiRepository fiRepository;
  private final EnvironmentService environmentService;
  private final NameUniquenessService nameUniquenessService;

  @Override
  @Transactional
  public BrandDto create(BrandDto dto) {
    verifyFiExists(dto.getFiId());
    nameUniquenessService.validateForCreate(
        name -> brandRepository.existsByFiIdAndName(dto.getFiId(), name), "Brand", dto.getName());
    Brand brand = brandMapper.toBrand(dto);
    Brand savedBrand = brandRepository.save(brand);

    createDefaultEnvironments(savedBrand.getId());
    return brandMapper.toBrandDto(savedBrand);
  }

  @Override
  public List<BrandDto> readAll() {
    return brandRepository.findAll().stream().map(brandMapper::toBrandDto).toList();
  }

  @Override
  public BrandDto read(String id) {
    Brand brand =
        brandRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorCode.BRAND_NOT_FOUND.getCode()));
    return brandMapper.toBrandDto(brand);
  }

  @Override
  @Transactional
  public BrandDto update(BrandDto dto) {
    Brand existingBrand =
        brandRepository
            .findById(dto.getId())
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorCode.BRAND_NOT_FOUND.getCode()));

    nameUniquenessService.validateForUpdateWithFlowContext(
        existingBrand.getName(),
        dto.getName(),
        dto.getFiId(),
        null,
        null,
        (brandId, environmentId, flowActionId, name) ->
            brandRepository.existsByFiIdAndNameAndIdNot(brandId, name, dto.getId()),
        "Brand");

    brandMapper.toUpdateBrand(dto, existingBrand);
    Brand brand = brandRepository.save(existingBrand);
    return brandMapper.toBrandDto(brand);
  }

  @Override
  @Transactional
  public void delete(String id) {
    verifyBrandExists(id);
    brandRepository.deleteById(id);
  }

  private void verifyBrandExists(String id) {
    if (!brandRepository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.BRAND_NOT_FOUND.getCode());
    }
  }

  private void verifyFiExists(String fiId) {
    if (!fiRepository.existsById(fiId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.FI_NOT_FOUND.getCode());
    }
  }

  private void createDefaultEnvironments(String brandId) {
    try {
      EnvironmentDto uatEnvironment = EnvironmentDto.builder().name("UAT").brandId(brandId).build();
      environmentService.create(uatEnvironment);
      log.info("Successfully created UAT environment for brand: {}", brandId);

      EnvironmentDto productionEnvironment =
          EnvironmentDto.builder().name("Production").brandId(brandId).build();
      environmentService.create(productionEnvironment);
      log.info("Successfully created Production environment for brand: {}", brandId);
    } catch (Exception e) {
      log.error(
          "Failed to create default environments for brand: {}. Error: {}",
          brandId,
          e.getMessage(),
          e);
    }
  }

  @Override
  public List<BrandDto> findByFiId(String fiId) {
    List<Brand> brands = brandRepository.findByFiId(fiId);
    return brands.stream().map(brandMapper::toBrandDto).toList();
  }

  @Override
  public List<BrandDto> findByUserId(String userId) {
    List<Brand> brands = brandRepository.findByUserId(userId);
    return brands.stream().map(brandMapper::toBrandDto).toList();
  }
}
