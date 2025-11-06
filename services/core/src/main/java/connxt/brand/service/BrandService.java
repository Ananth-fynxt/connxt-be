package connxt.brand.service;

import java.util.List;

import connxt.brand.dto.BrandDto;

public interface BrandService {

  BrandDto create(BrandDto brandDto);

  List<BrandDto> readAll();

  BrandDto read(String id);

  BrandDto update(BrandDto dto);

  void delete(String id);

  List<BrandDto> findByUserId(String userId);
}
