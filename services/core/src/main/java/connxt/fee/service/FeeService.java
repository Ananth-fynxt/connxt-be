package connxt.fee.service;

import java.util.List;

import connxt.fee.dto.FeeDto;
import connxt.shared.constants.Status;

public interface FeeService {

  FeeDto create(FeeDto feeDto);

  FeeDto readLatest(String id);

  List<FeeDto> readByBrandAndEnvironment(String brandId, String environmentId);

  List<FeeDto> readByPspId(String pspId);

  List<FeeDto> readLatestEnabledFeeRulesByCriteria(
      List<String> pspIds,
      String brandId,
      String environmentId,
      String flowActionId,
      String currency,
      Status status);

  FeeDto update(String id, FeeDto feeDto);

  void delete(String id);
}
