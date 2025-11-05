package nexxus.pspgroup.service;

import java.util.List;

import nexxus.pspgroup.dto.PspGroupDto;

public interface PspGroupService {

  PspGroupDto create(PspGroupDto pspGroupDto);

  PspGroupDto readLatest(String id);

  List<PspGroupDto> readByBrandAndEnvironment(String brandId, String environmentId);

  List<PspGroupDto> readByPspId(String pspId);

  PspGroupDto update(String id, PspGroupDto pspGroupDto);

  void delete(String id);
}
