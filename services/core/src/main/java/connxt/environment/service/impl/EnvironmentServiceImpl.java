package connxt.environment.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import connxt.environment.dto.EnvironmentDto;
import connxt.environment.entity.Environment;
import connxt.environment.entity.EnvironmentSecretIdGenerator;
import connxt.environment.entity.TokenIdGenerator;
import connxt.environment.repository.EnvironmentRepository;
import connxt.environment.service.EnvironmentService;
import connxt.environment.service.mappers.EnvironmentMapper;
import connxt.shared.constants.ErrorCode;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnvironmentServiceImpl implements EnvironmentService {

  private final EnvironmentRepository environmentRepository;
  private final EnvironmentMapper environmentMapper;

  @Override
  @Transactional
  public EnvironmentDto create(EnvironmentDto dto) {
    verifyEnvironmentNameExistsForBrand(dto.getBrandId(), dto.getName());

    Environment environment = environmentMapper.toEnvironment(dto);
    environment.setSecret(new EnvironmentSecretIdGenerator().generate(null, null).toString());
    environment.setToken(new TokenIdGenerator().generate(null, null).toString());

    Environment savedEnvironment = environmentRepository.save(environment);
    return environmentMapper.toEnvironmentDto(savedEnvironment);
  }

  @Override
  public List<EnvironmentDto> readAll() {
    return environmentRepository.findAll().stream()
        .map(environmentMapper::toEnvironmentDto)
        .toList();
  }

  @Override
  public EnvironmentDto read(String id) {
    Environment environment =
        environmentRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorCode.ENVIRONMENT_NOT_FOUND.getCode()));
    return environmentMapper.toEnvironmentDto(environment);
  }

  @Override
  @Transactional
  public EnvironmentDto update(EnvironmentDto dto) {
    Environment existingEnvironment =
        environmentRepository
            .findById(dto.getId())
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorCode.ENVIRONMENT_NOT_FOUND.getCode()));
    environmentMapper.toUpdateEnvironment(dto, existingEnvironment);
    Environment environment = environmentRepository.save(existingEnvironment);
    return environmentMapper.toEnvironmentDto(environment);
  }

  @Override
  @Transactional
  public void delete(String id) {
    verifyEnvironmentExists(id);
    environmentRepository.deleteById(id);
  }

  private void verifyEnvironmentExists(String id) {
    if (!environmentRepository.existsById(id)) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, ErrorCode.ENVIRONMENT_NOT_FOUND.getCode());
    }
  }

  private void verifyEnvironmentNameExistsForBrand(String brandId, String name) {
    if (environmentRepository.existsByBrandIdAndName(brandId, name)) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, ErrorCode.ENVIRONMENT_ALREADY_EXISTS.getCode());
    }
  }

  @Override
  public List<EnvironmentDto> findByBrandId(String brandId) {
    return environmentRepository.findByBrandId(brandId).stream()
        .map(environmentMapper::toEnvironmentDto)
        .toList();
  }

  @Override
  @Transactional
  public EnvironmentDto rotateSecret(String id) {
    Environment environment =
        environmentRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorCode.ENVIRONMENT_NOT_FOUND.getCode()));

    environment.setSecret(new EnvironmentSecretIdGenerator().generate(null, null).toString());

    Environment savedEnvironment = environmentRepository.save(environment);
    return environmentMapper.toEnvironmentDto(savedEnvironment);
  }

  @Override
  public EnvironmentDto readByToken(String token) {
    Environment environment =
        environmentRepository
            .findByToken(token)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorCode.ENVIRONMENT_NOT_FOUND.getCode()));
    return environmentMapper.toEnvironmentDto(environment);
  }

  @Override
  public EnvironmentDto readBySecret(String secret) {
    Environment environment =
        environmentRepository
            .findBySecret(secret)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorCode.ENVIRONMENT_NOT_FOUND.getCode()));
    return environmentMapper.toEnvironmentDto(environment);
  }

  @Override
  public EnvironmentDto findBySecret(String secret) {
    return readBySecret(secret); // Same implementation as readBySecret
  }
}
