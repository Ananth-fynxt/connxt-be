package connxt.flowdefinition.service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import connxt.flowdefinition.dto.FlowDefinitionDto;
import connxt.flowdefinition.entity.FlowDefinition;
import connxt.shared.db.mappers.MapperCoreConfig;

@Mapper(config = MapperCoreConfig.class)
public interface FlowDefinitionMapper {
  FlowDefinitionDto toFlowDefinitionDto(FlowDefinition flowDefinition);

  FlowDefinition toFlowDefinition(FlowDefinitionDto flowDefinitionDto);

  void toUpdateFlowDefinition(
      FlowDefinitionDto flowDefinitionDto, @MappingTarget FlowDefinition flowDefinition);
}
