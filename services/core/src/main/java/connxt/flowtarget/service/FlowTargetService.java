package connxt.flowtarget.service;

import java.util.List;

import connxt.flowtarget.dto.FlowTargetDto;

public interface FlowTargetService {

  FlowTargetDto create(FlowTargetDto dto);

  List<FlowTargetDto> readAll(String flowTypeId);

  FlowTargetDto read(String id);

  FlowTargetDto readWithAssociations(String id);

  List<FlowTargetDto> readByIds(List<String> ids);

  FlowTargetDto update(String flowTypeId, String id, FlowTargetDto dto);

  void delete(String id);

  void validateCredentialsForFlowTarget(String flowTargetId, String credentials);

  void validateCredentialsCurrenciesAndCountries(
      String flowTargetId, String credentials, List<String> currencies, List<String> countries);
}
