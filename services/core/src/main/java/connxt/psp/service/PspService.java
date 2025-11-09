package connxt.psp.service;

import java.util.List;
import java.util.Map;

import connxt.psp.dto.*;
import connxt.psp.entity.Psp;
import connxt.shared.dto.IdNameDto;

public interface PspService {

  PspDto create(PspDto pspDto);

  PspDetailsDto update(String pspId, UpdatePspDto pspDto);

  PspDetailsDto getById(String pspId);

  List<PspSummaryDto> getByBrandAndEnvironment(String brandId, String environmentId);

  List<PspSummaryDto> getByBrandAndEnvironmentByStatusAndFlowAction(
      String brandId, String environmentId, String status, String flowActionId);

  Psp getPspIfEnabled(String pspId);

  PspSummaryDto updateStatus(String pspId, String status);

  Map<String, IdNameDto> getPspIdNameDtoMap(List<String> pspIds);
}
