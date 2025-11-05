package nexxus.flowtype.service;

import java.util.List;

import nexxus.flowtype.dto.FlowTypeDto;

public interface FlowTypeService {

  FlowTypeDto create(FlowTypeDto dto);

  List<FlowTypeDto> readAll();

  FlowTypeDto read(String id);

  FlowTypeDto update(String id, FlowTypeDto dto);

  void delete(String id);

  FlowTypeDto findByName(String name);
}
