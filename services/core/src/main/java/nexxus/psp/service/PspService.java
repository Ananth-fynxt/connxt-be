package nexxus.psp.service;

import java.util.List;
import java.util.Map;

import nexxus.psp.dto.*;
import nexxus.psp.entity.Psp;

public interface PspService {

  PspDto create(PspDto pspDto);

  PspDetailsDto update(String pspId, UpdatePspDto pspDto);

  PspDetailsDto getById(String pspId);

  List<PspSummaryDto> getByBrandAndEnvironment(String brandId, String environmentId);

  List<PspSummaryDto> getByBrandAndEnvironmentByStatusAndCurrencyAndFlowAction(
      String brandId, String environmentId, String status, String currency, String flowActionId);

  List<PspSummaryDto> getByBrandAndEnvironmentByStatusAndFlowAction(
      String brandId, String environmentId, String status, String flowActionId);

  List<String> getSupportedCurrenciesByBrandAndEnvironment(String brandId, String environmentId);

  List<String> getSupportedCountriesByBrandAndEnvironment(String brandId, String environmentId);

  Psp getPspIfEnabled(String pspId);

  PspSummaryDto updateStatus(String pspId, String status);

  Map<String, IdNameDto> getPspIdNameDtoMap(List<String> pspIds);
}
