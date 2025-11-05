package connxt.flowaction.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

import connxt.flowaction.dto.FlowActionDto;
import connxt.flowaction.entity.FlowAction;
import connxt.flowaction.repository.FlowActionRepository;
import connxt.flowaction.service.FlowActionService;
import connxt.flowaction.service.mappers.FlowActionMapper;
import connxt.psp.dto.IdNameDto;
import connxt.shared.constants.ErrorCode;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FlowActionServiceImpl implements FlowActionService {

  private final FlowActionRepository flowActionRepository;
  private final FlowActionMapper flowActionMapper;

  @Override
  public FlowActionDto create(FlowActionDto dto) {
    verifyFlowActionNotExists(dto.getFlowTypeId(), dto.getName());
    FlowAction flowAction = flowActionMapper.toFlowAction(dto);
    return flowActionMapper.toFlowActionDto(flowActionRepository.save(flowAction));
  }

  @Override
  public List<FlowActionDto> readAll() {
    return flowActionRepository.findAll().stream().map(flowActionMapper::toFlowActionDto).toList();
  }

  @Override
  public FlowActionDto read(String id) {
    FlowAction flowAction =
        flowActionRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorCode.FLOW_ACTION_NOT_FOUND.getCode()));
    return flowActionMapper.toFlowActionDto(flowAction);
  }

  @Override
  public List<FlowActionDto> findByFlowTypeId(String flowTypeId) {
    return flowActionRepository.findByFlowTypeId(flowTypeId).stream()
        .map(flowActionMapper::toFlowActionDto)
        .toList();
  }

  @Override
  public FlowActionDto findByNameAndFlowTypeId(String name, String flowTypeId) {
    FlowAction flowAction =
        flowActionRepository
            .findByNameAndFlowTypeId(name, flowTypeId)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorCode.FLOW_ACTION_NOT_FOUND.getCode()));
    return flowActionMapper.toFlowActionDto(flowAction);
  }

  @Override
  @Transactional
  public FlowActionDto update(FlowActionDto dto) {
    FlowAction existingFlowAction =
        flowActionRepository
            .findById(dto.getId())
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorCode.FLOW_ACTION_NOT_FOUND.getCode()));
    flowActionMapper.toUpdateFlowAction(dto, existingFlowAction);
    FlowAction flowAction = flowActionRepository.save(existingFlowAction);
    return flowActionMapper.toFlowActionDto(flowAction);
  }

  @Override
  @Transactional
  public void delete(String id) {
    verifyFlowActionExists(id);
    flowActionRepository.deleteById(id);
  }

  private void verifyFlowActionNotExists(String flowTypeId, String name) {
    if (flowActionRepository.existsByNameAndFlowTypeId(name, flowTypeId)) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, ErrorCode.FLOW_ACTION_ALREADY_EXISTS.getCode());
    }
  }

  private void verifyFlowActionExists(String id) {
    if (!flowActionRepository.existsById(id)) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, ErrorCode.FLOW_ACTION_NOT_FOUND.getCode());
    }
  }

  @Override
  public Map<String, IdNameDto> getFlowActionIdNameDtoMap(List<String> flowActionIds) {
    if (CollectionUtils.isEmpty(flowActionIds)) {
      return Collections.emptyMap();
    }

    List<FlowAction> flowActions = flowActionRepository.findAllById(flowActionIds);
    return flowActionMapper.toIdNameDtoMap(flowActions);
  }
}
