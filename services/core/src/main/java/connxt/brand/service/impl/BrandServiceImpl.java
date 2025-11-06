package connxt.brand.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import connxt.brand.dto.BrandDto;
import connxt.brand.entity.Brand;
import connxt.brand.repository.BrandRepository;
import connxt.brand.service.BrandService;
import connxt.brand.service.mappers.BrandMapper;
import connxt.environment.dto.EnvironmentDto;
import connxt.environment.service.EnvironmentService;
import connxt.shared.constants.ErrorCode;
import connxt.shared.service.NameUniquenessService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

  private final BrandRepository brandRepository;
  private final BrandMapper brandMapper;
  private final EnvironmentService environmentService;
  private final NameUniquenessService nameUniquenessService;

  @Override
  @Transactional
  public BrandDto create(BrandDto dto) {
    nameUniquenessService.validateForCreate(
        name -> brandRepository.existsByName(name), "Brand", dto.getName());
    if (brandRepository.existsByEmail(dto.getEmail())) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "Brand already exists with the given email");
    }
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
        null,
        null,
        null,
        (brandId, environmentId, flowActionId, name) -> {
          Optional<Brand> brandWithName = brandRepository.findByName(name);
          return brandWithName.isPresent() && !brandWithName.get().getId().equals(dto.getId());
        },
        "Brand");

    // Validate email uniqueness if email is being changed
    if (!existingBrand.getEmail().equals(dto.getEmail())) {
      if (brandRepository.existsByEmail(dto.getEmail())) {
        throw new ResponseStatusException(
            HttpStatus.CONFLICT, "Brand already exists with the given email");
      }
    }

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
  public List<BrandDto> findByUserId(String userId) {
    List<Brand> brands = brandRepository.findByUserId(userId);
    return brands.stream().map(brandMapper::toBrandDto).toList();
  }
}
