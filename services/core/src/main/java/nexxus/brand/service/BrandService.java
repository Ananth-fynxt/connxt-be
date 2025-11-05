package nexxus.brand.service;

import java.util.List;

import nexxus.brand.dto.BrandDto;

public interface BrandService {

  BrandDto create(BrandDto brandDto);

  List<BrandDto> readAll();

  BrandDto read(String id);

  BrandDto update(BrandDto dto);

  void delete(String id);

  List<BrandDto> findByFiId(String fiId);

  List<BrandDto> findByUserId(String userId);
}
