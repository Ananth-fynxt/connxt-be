package connxt.flowdefinition.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import connxt.flowdefinition.dto.FlowDefinitionDto;
import connxt.flowdefinition.entity.FlowDefinition;
import connxt.flowdefinition.repository.FlowDefinitionRepository;
import connxt.flowdefinition.service.FlowDefinitionService;
import connxt.flowdefinition.service.mappers.FlowDefinitionMapper;
import connxt.shared.constants.ErrorCode;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FlowDefinitionServiceImpl implements FlowDefinitionService {

  private final FlowDefinitionRepository flowDefinitionRepository;
  private final FlowDefinitionMapper flowDefinitionMapper;

  @Override
  @Transactional
  public FlowDefinitionDto create(FlowDefinitionDto dto) {
    verifyFlowDefinitionNotExists(dto.getCode());
    FlowDefinition flowDefinition = flowDefinitionMapper.toFlowDefinition(dto);
    return flowDefinitionMapper.toFlowDefinitionDto(flowDefinitionRepository.save(flowDefinition));
  }

  @Override
  public List<FlowDefinitionDto> readAll() {
    return flowDefinitionRepository.findAll().stream()
        .map(flowDefinitionMapper::toFlowDefinitionDto)
        .toList();
  }

  @Override
  public List<FlowDefinitionDto> readAllByFlowTargetId(String flowTargetId) {
    return flowDefinitionRepository.findByFlowTargetId(flowTargetId).stream()
        .map(flowDefinitionMapper::toFlowDefinitionDto)
        .toList();
  }

  @Override
  public FlowDefinitionDto read(String id) {
    FlowDefinition flowDefinition =
        flowDefinitionRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorCode.FLOW_DEFINITION_NOT_FOUND.getCode()));
    return flowDefinitionMapper.toFlowDefinitionDto(flowDefinition);
  }

  @Override
  public FlowDefinitionDto update(String id, FlowDefinitionDto dto) {
    FlowDefinition exisitingFlowDefinition =
        flowDefinitionRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorCode.FLOW_DEFINITION_NOT_FOUND.getCode()));
    flowDefinitionMapper.toUpdateFlowDefinition(dto, exisitingFlowDefinition);
    return flowDefinitionMapper.toFlowDefinitionDto(
        flowDefinitionRepository.save(exisitingFlowDefinition));
  }

  @Override
  public void delete(String id) {
    verifyFlowDefinitionExists(id);
    flowDefinitionRepository.deleteById(id);
  }

  private void verifyFlowDefinitionNotExists(String code) {
    if (flowDefinitionRepository.existsByCode(code)) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, ErrorCode.FLOW_DEFINITION_ALREADY_EXISTS.getCode());
    }
  }

  private void verifyFlowDefinitionExists(String id) {
    if (!flowDefinitionRepository.existsById(id)) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, ErrorCode.FLOW_DEFINITION_NOT_FOUND.getCode());
    }
  }
}
