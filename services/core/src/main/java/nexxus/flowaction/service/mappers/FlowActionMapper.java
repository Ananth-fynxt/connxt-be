package nexxus.flowaction.service.mappers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import nexxus.flowaction.dto.FlowActionDto;
import nexxus.flowaction.entity.FlowAction;
import nexxus.psp.dto.IdNameDto;
import nexxus.shared.db.mappers.MapperCoreConfig;

@Mapper(config = MapperCoreConfig.class)
public interface FlowActionMapper {

  FlowActionDto toFlowActionDto(FlowAction flowAction);

  FlowAction toFlowAction(FlowActionDto flowActionDto);

  void toUpdateFlowAction(FlowActionDto flowActionDto, @MappingTarget FlowAction flowAction);

  @Mapping(target = "id", source = "id")
  @Mapping(target = "name", source = "name")
  IdNameDto toIdNameDto(FlowAction flowAction);

  List<IdNameDto> toIdNameDto(List<FlowAction> flowActions);

  default Map<String, IdNameDto> toIdNameDtoMap(List<FlowAction> flowActions) {
    if (flowActions == null) {
      return Map.of();
    }
    return flowActions.stream().collect(Collectors.toMap(FlowAction::getId, this::toIdNameDto));
  }
}
