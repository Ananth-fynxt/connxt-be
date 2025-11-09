package connxt.flowdefinition.service;

import java.util.List;

import connxt.flowdefinition.dto.FlowDefinitionDto;

public interface FlowDefinitionService {

  FlowDefinitionDto create(FlowDefinitionDto dto);

  List<FlowDefinitionDto> readAll();

  List<FlowDefinitionDto> readAllByFlowTargetId(String flowTargetId);

  FlowDefinitionDto read(String id);

  FlowDefinitionDto update(String id, FlowDefinitionDto dto);

  void delete(String id);
}
