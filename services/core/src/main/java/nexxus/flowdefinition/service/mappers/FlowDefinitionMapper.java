package nexxus.flowdefinition.service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import nexxus.flowdefinition.dto.FlowDefinitionDto;
import nexxus.flowdefinition.entity.FlowDefinition;
import nexxus.shared.db.mappers.MapperCoreConfig;

@Mapper(config = MapperCoreConfig.class)
public interface FlowDefinitionMapper {
  FlowDefinitionDto toFlowDefinitionDto(FlowDefinition flowDefinition);

  FlowDefinition toFlowDefinition(FlowDefinitionDto flowDefinitionDto);

  void toUpdateFlowDefinition(
      FlowDefinitionDto flowDefinitionDto, @MappingTarget FlowDefinition flowDefinition);
}
