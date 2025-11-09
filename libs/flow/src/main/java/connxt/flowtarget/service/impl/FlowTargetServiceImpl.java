package connxt.flowtarget.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

import connxt.flowaction.entity.FlowAction;
import connxt.flowaction.repository.FlowActionRepository;
import connxt.flowdefinition.entity.FlowDefinition;
import connxt.flowdefinition.repository.FlowDefinitionRepository;
import connxt.flowtarget.dto.FlowTargetDto;
import connxt.flowtarget.entity.FlowTarget;
import connxt.flowtarget.repository.FlowTargetRepository;
import connxt.flowtarget.service.FlowTargetService;
import connxt.flowtarget.service.mappers.FlowTargetMapper;
import connxt.shared.constants.ErrorCode;
import connxt.shared.constants.Status;
import connxt.shared.validators.JsonSchemaAndPayloadValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FlowTargetServiceImpl implements FlowTargetService {

  private final FlowTargetRepository flowTargetRepository;
  private final FlowDefinitionRepository flowDefinitionRepository;
  private final FlowActionRepository flowActionRepository;
  private final FlowTargetMapper flowTargetMapper;
  private final JsonSchemaAndPayloadValidator jsonSchemaAndPayloadValidator;

  @Override
  @Transactional
  public FlowTargetDto create(FlowTargetDto dto) {
    ensureFlowTargetDoesNotExists(dto.getFlowTypeId(), dto.getName());
    FlowTarget flowTarget = flowTargetMapper.toFlowTarget(dto);
    flowTarget.setStatus(Status.ENABLED);
    return flowTargetMapper.toFlowTargetDto(flowTargetRepository.save(flowTarget));
  }

  @Override
  public List<FlowTargetDto> readAll(String flowTypeId) {
    return flowTargetRepository.findByFlowTypeId(flowTypeId).stream()
        .map(this::buildFlowTargetWithAssociations)
        .toList();
  }

  @Override
  public FlowTargetDto read(String id) {
    FlowTarget flowTarget =
        flowTargetRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorCode.FLOW_TARGET_NOT_FOUND.getCode()));
    return flowTargetMapper.toFlowTargetDto(flowTarget);
  }

  @Override
  public FlowTargetDto readWithAssociations(String id) {
    FlowTarget flowTarget =
        flowTargetRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorCode.FLOW_TARGET_NOT_FOUND.getCode()));
    return buildFlowTargetWithAssociations(flowTarget);
  }

  @Override
  public List<FlowTargetDto> readByIds(List<String> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return List.of();
    }

    List<FlowTarget> flowTargets = flowTargetRepository.findAllById(ids);
    return flowTargets.stream().map(this::buildFlowTargetWithAssociations).toList();
  }

  @Override
  @Transactional
  public FlowTargetDto update(String flowTypeId, String id, FlowTargetDto dto) {
    FlowTarget flowTarget =
        flowTargetRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorCode.FLOW_TARGET_NOT_FOUND.getCode()));
    flowTargetMapper.toUpdateFlowTarget(dto, flowTarget);
    return flowTargetMapper.toFlowTargetDto(flowTargetRepository.save(flowTarget));
  }

  @Override
  @Transactional
  public void delete(String id) {
    ensureFlowTargetExists(id);
    flowTargetRepository.deleteById(id);
  }

  @Override
  public void validateCredentialsForFlowTarget(String flowTargetId, String credentials) {
    FlowTargetDto flowTargetDto = read(flowTargetId);
    jsonSchemaAndPayloadValidator.validateAndThrow(
        flowTargetDto.getCredentialSchema(), credentials);
  }

  private FlowTargetDto buildFlowTargetWithAssociations(FlowTarget flowTarget) {
    FlowTargetDto flowTargetDto = flowTargetMapper.toFlowTargetDto(flowTarget);

    List<FlowDefinition> flowDefinitions =
        flowDefinitionRepository.findByFlowTargetId(flowTarget.getId());

    List<String> actionIds = flowDefinitions.stream().map(FlowDefinition::getFlowActionId).toList();

    Map<String, FlowAction> actionsById =
        flowActionRepository.findAllById(actionIds).stream()
            .collect(Collectors.toMap(FlowAction::getId, Function.identity()));

    List<FlowTargetDto.SupportedActionInfo> supportedActions =
        flowDefinitions.stream()
            .map(
                fd -> {
                  FlowAction action = actionsById.get(fd.getFlowActionId());
                  if (action == null) {
                    return null;
                  }
                  return FlowTargetDto.SupportedActionInfo.builder()
                      .id(fd.getId())
                      .flowActionId(action.getId())
                      .flowActionName(action.getName())
                      .build();
                })
            .filter(Objects::nonNull)
            .toList();

    flowTargetDto.setSupportedActions(supportedActions);
    return flowTargetDto;
  }

  private void ensureFlowTargetDoesNotExists(String flowTypeId, String name) {
    if (flowTargetRepository.existsByFlowTypeIdAndName(flowTypeId, name)) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, ErrorCode.FLOW_TARGET_ALREADY_EXISTS.getCode());
    }
  }

  private void ensureFlowTargetExists(String id) {
    if (!flowTargetRepository.existsById(id)) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, ErrorCode.FLOW_TARGET_NOT_FOUND.getCode());
    }
  }
}
