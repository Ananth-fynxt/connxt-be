package nexxus.flowaction.service;

import java.util.List;
import java.util.Map;

import nexxus.flowaction.dto.FlowActionDto;
import nexxus.psp.dto.IdNameDto;

public interface FlowActionService {

  FlowActionDto create(FlowActionDto dto);

  List<FlowActionDto> readAll();

  FlowActionDto read(String id);

  FlowActionDto update(FlowActionDto dto);

  void delete(String id);

  List<FlowActionDto> findByFlowTypeId(String flowTypeId);

  FlowActionDto findByNameAndFlowTypeId(String name, String flowTypeId);

  Map<String, IdNameDto> getFlowActionIdNameDtoMap(List<String> flowActionIds);
}
