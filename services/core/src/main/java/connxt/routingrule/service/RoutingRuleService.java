package connxt.routingrule.service;

import java.util.List;

import connxt.routingrule.dto.RoutingRuleDto;
import connxt.routingrule.dto.UpdateRoutingRuleDto;
import connxt.routingrule.entity.RoutingRulePsp;

public interface RoutingRuleService {

  RoutingRuleDto create(RoutingRuleDto routingRuleDto);

  RoutingRuleDto update(String id, UpdateRoutingRuleDto updateRoutingRuleDto);

  void delete(String id);

  RoutingRuleDto getById(String id);

  List<RoutingRuleDto> readAllByBrandAndEnvironment(String brandId, String environmentId);

  RoutingRuleDto findActiveRoutingRuleById(String routingRuleId);

  List<RoutingRulePsp> findRoutingRulePspsByIdAndVersion(String id, Integer version);

  List<RoutingRuleDto> findEnabledRoutingRulesByBrandAndEnvironment(
      String brandId, String environmentId);
}
