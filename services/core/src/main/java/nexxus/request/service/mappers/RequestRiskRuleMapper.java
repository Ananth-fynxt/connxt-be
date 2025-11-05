package nexxus.request.service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import nexxus.request.entity.RequestRiskRule;
import nexxus.riskrule.dto.RiskRuleDto;
import nexxus.shared.db.mappers.MapperCoreConfig;

@Mapper(config = MapperCoreConfig.class)
public interface RequestRiskRuleMapper {

  @Mapping(target = "requestId", source = "requestId")
  @Mapping(target = "riskRuleId", source = "riskRuleDto.id")
  @Mapping(target = "riskRuleVersion", source = "riskRuleDto.version")
  RequestRiskRule toRequestRiskRule(String requestId, RiskRuleDto riskRuleDto);
}
