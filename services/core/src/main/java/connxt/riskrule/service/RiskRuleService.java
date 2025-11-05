package connxt.riskrule.service;

import java.util.List;

import connxt.riskrule.dto.RiskRuleDto;
import connxt.shared.constants.RiskAction;
import connxt.shared.constants.Status;

public interface RiskRuleService {

  RiskRuleDto create(RiskRuleDto dto);

  List<RiskRuleDto> readAll();

  RiskRuleDto read(String id, Integer version);

  RiskRuleDto readLatest(String id);

  List<RiskRuleDto> readByBrandAndEnvironment(String brandId, String environmentId);

  List<RiskRuleDto> readByPspId(String pspId);

  List<RiskRuleDto> readByPspIds(List<String> pspIds);

  List<RiskRuleDto> readLatestEnabledRiskRulesByCriteria(
      List<String> pspIds,
      String brandId,
      String environmentId,
      String flowActionId,
      String currency,
      RiskAction action,
      Status status);

  RiskRuleDto update(String id, RiskRuleDto dto);

  void delete(String id);
}
