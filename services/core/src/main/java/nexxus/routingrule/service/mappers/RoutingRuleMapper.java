package nexxus.routingrule.service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import nexxus.routingrule.dto.RoutingRuleDto;
import nexxus.routingrule.dto.RoutingRulePspDto;
import nexxus.routingrule.dto.UpdateRoutingRuleDto;
import nexxus.routingrule.entity.EmbeddableRoutingRuleId;
import nexxus.routingrule.entity.RoutingRule;
import nexxus.routingrule.entity.RoutingRulePsp;
import nexxus.shared.db.mappers.MapperCoreConfig;

@Mapper(config = MapperCoreConfig.class)
public interface RoutingRuleMapper {

  @Mapping(target = "id", source = "routingRuleId.id")
  @Mapping(target = "version", source = "routingRuleId.version")
  RoutingRuleDto toRoutingRuleDto(RoutingRule routingRule);

  @Mapping(
      target = "routingRuleId",
      expression = "java(createEmbeddableRoutingRuleId(dto.getId(), version))")
  RoutingRule toRoutingRule(RoutingRuleDto dto, Integer version);

  @Mapping(target = "conditionJson", source = "dto.conditionJson")
  void toUpdateRoutingRule(UpdateRoutingRuleDto dto, @MappingTarget RoutingRule routingRule);

  RoutingRulePspDto toRoutingRulePspDto(RoutingRulePsp routingRulePsp);

  @Mapping(target = "routingRuleId", source = "routingRuleId")
  @Mapping(target = "routingRuleVersion", source = "routingRuleVersion")
  RoutingRulePsp toRoutingRulePsp(
      RoutingRulePspDto dto, String routingRuleId, int routingRuleVersion);

  @Mapping(
      target = "routingRuleId",
      expression =
          "java(createEmbeddableRoutingRuleId(existing.getRoutingRuleId().getId(), existing.getRoutingRuleId().getVersion() + 1))")
  RoutingRule copyRoutingRuleWithIncrementedVersion(RoutingRule existing);

  default EmbeddableRoutingRuleId createEmbeddableRoutingRuleId(String id, Integer version) {
    return new EmbeddableRoutingRuleId(id, version);
  }
}
