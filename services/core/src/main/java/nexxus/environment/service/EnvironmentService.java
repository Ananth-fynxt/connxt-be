package nexxus.environment.service;

import java.util.List;

import nexxus.environment.dto.EnvironmentDto;

public interface EnvironmentService {

  EnvironmentDto create(EnvironmentDto environmentDto);

  List<EnvironmentDto> readAll();

  EnvironmentDto read(String id);

  EnvironmentDto update(EnvironmentDto dto);

  void delete(String id);

  List<EnvironmentDto> findByBrandId(String brandId);

  EnvironmentDto rotateSecret(String id);

  EnvironmentDto readByToken(String token);

  EnvironmentDto findBySecret(String secret);

  EnvironmentDto readBySecret(String secret);
}
