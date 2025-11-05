package connxt.flowtype.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import connxt.flowtype.dto.FlowTypeDto;
import connxt.flowtype.entity.FlowType;
import connxt.flowtype.repository.FlowTypeRepository;
import connxt.flowtype.service.FlowTypeService;
import connxt.flowtype.service.mappers.FlowTypeMapper;
import connxt.shared.constants.ErrorCode;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FlowTypeServiceImpl implements FlowTypeService {

  private final FlowTypeRepository flowTypeRepository;
  private final FlowTypeMapper flowTypeMapper;

  @Override
  @Transactional
  public FlowTypeDto create(FlowTypeDto dto) {
    verifyFlowTypeNotExists(dto.getName());
    FlowType flowType = flowTypeMapper.toFlowType(dto);
    return flowTypeMapper.toFlowTypeDto(flowTypeRepository.save(flowType));
  }

  @Override
  public List<FlowTypeDto> readAll() {
    return flowTypeRepository.findAll().stream().map(flowTypeMapper::toFlowTypeDto).toList();
  }

  @Override
  public FlowTypeDto read(String id) {
    FlowType flowType =
        flowTypeRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorCode.FLOW_TYPE_NOT_FOUND.getCode()));
    return flowTypeMapper.toFlowTypeDto(flowType);
  }

  @Override
  @Transactional
  public FlowTypeDto update(String id, FlowTypeDto dto) {
    FlowType existingFlowType =
        flowTypeRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorCode.FLOW_TYPE_NOT_FOUND.getCode()));
    flowTypeMapper.toUpdateFlowType(dto, existingFlowType);
    return flowTypeMapper.toFlowTypeDto(flowTypeRepository.save(existingFlowType));
  }

  @Override
  @Transactional
  public void delete(String id) {
    verifyFlowTypeExists(id);
    flowTypeRepository.deleteById(id);
  }

  @Override
  public FlowTypeDto findByName(String name) {
    FlowType flowType =
        flowTypeRepository
            .findByName(name)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorCode.FLOW_TYPE_NOT_FOUND.getCode()));
    return flowTypeMapper.toFlowTypeDto(flowType);
  }

  private void verifyFlowTypeNotExists(String name) {
    if (flowTypeRepository.existsByName(name)) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, ErrorCode.FLOW_TYPE_ALREADY_EXISTS.getCode());
    }
  }

  private void verifyFlowTypeExists(String id) {
    if (!flowTypeRepository.existsById(id)) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, ErrorCode.FLOW_TYPE_NOT_FOUND.getCode());
    }
  }
}
