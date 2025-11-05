package nexxus.riskrule.service.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import nexxus.psp.dto.IdNameDto;
import nexxus.riskrule.dto.RiskRuleDto;
import nexxus.riskrule.entity.EmbeddableRiskRuleId;
import nexxus.riskrule.entity.RiskRule;
import nexxus.riskrule.entity.RiskRulePsp;
import nexxus.shared.db.mappers.MapperCoreConfig;

@Mapper(config = MapperCoreConfig.class)
public interface RiskRuleMapper {

  @Mapping(target = "id", source = "riskRuleId.id")
  @Mapping(target = "version", source = "riskRuleId.version")
  RiskRuleDto toRiskRuleDto(RiskRule riskRule);

  @Mapping(
      target = "riskRuleId",
      expression = "java(createRiskRuleId(riskRuleDto.getId(), version))")
  @Mapping(target = "riskRulePsps", ignore = true)
  RiskRule toRiskRule(RiskRuleDto riskRuleDto, int version);

  @Mapping(
      target = "riskRuleId",
      expression = "java(createRiskRuleId(existingRiskRule.getRiskRuleId().getId(), version))")
  @Mapping(
      target = "riskRulePsps",
      expression =
          "java(mapExistingPspsToRiskRulePsps(existingRiskRule.getRiskRulePsps(), existingRiskRule.getRiskRuleId().getId(), version))")
  RiskRule createUpdatedRiskRule(RiskRule existingRiskRule, Integer version);

  void updateRiskRule(RiskRuleDto riskRuleDto, @MappingTarget RiskRule riskRule);

  @Mapping(
      target = "riskRuleId",
      expression =
          "java(createRiskRuleId(existing.getRiskRuleId().getId(), existing.getRiskRuleId().getVersion() + 1))")
  RiskRule copyRiskRuleWithIncrementedVersion(RiskRule existing);

  void toUpdateRiskRule(RiskRuleDto dto, @MappingTarget RiskRule riskRule);

  default EmbeddableRiskRuleId createRiskRuleId(String id, Integer version) {
    return new EmbeddableRiskRuleId(id, version);
  }

  default List<RiskRulePsp> createRiskRulePsps(
      List<IdNameDto> psps, String riskRuleId, Integer version) {
    if (psps == null || psps.isEmpty()) {
      return List.of();
    }

    return psps.stream()
        .filter(psp -> psp != null && psp.getId() != null)
        .map(
            psp ->
                RiskRulePsp.builder()
                    .riskRuleId(riskRuleId)
                    .riskRuleVersion(version)
                    .pspId(psp.getId())
                    .build())
        .collect(Collectors.toList());
  }

  default List<RiskRulePsp> mapExistingPspsToRiskRulePsps(
      List<RiskRulePsp> existingPsps, String riskRuleId, Integer version) {
    if (existingPsps == null || existingPsps.isEmpty()) {
      return List.of();
    }

    return existingPsps.stream()
        .filter(existingPsp -> existingPsp != null && existingPsp.getPspId() != null)
        .map(
            existingPsp ->
                RiskRulePsp.builder()
                    .riskRuleId(riskRuleId)
                    .riskRuleVersion(version)
                    .pspId(existingPsp.getPspId())
                    .build())
        .collect(Collectors.toList());
  }
}
