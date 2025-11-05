package connxt.request.service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import connxt.request.entity.RequestRiskRule;
import connxt.riskrule.dto.RiskRuleDto;
import connxt.shared.db.mappers.MapperCoreConfig;

@Mapper(config = MapperCoreConfig.class)
public interface RequestRiskRuleMapper {

  @Mapping(target = "requestId", source = "requestId")
  @Mapping(target = "riskRuleId", source = "riskRuleDto.id")
  @Mapping(target = "riskRuleVersion", source = "riskRuleDto.version")
  RequestRiskRule toRequestRiskRule(String requestId, RiskRuleDto riskRuleDto);
}
