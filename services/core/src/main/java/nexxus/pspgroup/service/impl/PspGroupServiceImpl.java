package nexxus.pspgroup.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

import nexxus.flowaction.service.FlowActionService;
import nexxus.psp.dto.IdNameDto;
import nexxus.psp.service.PspService;
import nexxus.pspgroup.dto.PspGroupDto;
import nexxus.pspgroup.entity.PspGroup;
import nexxus.pspgroup.entity.PspGroupPsp;
import nexxus.pspgroup.repository.PspGroupPspRepository;
import nexxus.pspgroup.repository.PspGroupRepository;
import nexxus.pspgroup.service.PspGroupService;
import nexxus.pspgroup.service.mappers.PspGroupMapper;
import nexxus.shared.constants.ErrorCode;
import nexxus.shared.constants.Status;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PspGroupServiceImpl implements PspGroupService {

  private final PspGroupRepository pspGroupRepository;
  private final PspGroupPspRepository pspGroupPspRepository;
  private final PspService pspService;
  private final FlowActionService flowActionService;
  private final PspGroupMapper pspGroupMapper;

  @Override
  @Transactional
  public PspGroupDto create(@Valid PspGroupDto pspGroupDto) {
    verifyPspGroupNotExists(pspGroupDto);

    PspGroup pspGroup = pspGroupMapper.toPspGroup(pspGroupDto, 1);
    pspGroup.setStatus(Status.ENABLED);

    PspGroup savedPspGroup = pspGroupRepository.save(pspGroup);
    createPspAssociations(savedPspGroup, pspGroupDto);
    return buildEnrichedPspGroupDto(savedPspGroup);
  }

  @Override
  public PspGroupDto readLatest(String id) {
    PspGroup pspGroup =
        pspGroupRepository
            .findLatestVersionById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorCode.PSP_GROUP_NOT_FOUND.getCode()));
    return buildEnrichedPspGroupDto(pspGroup);
  }

  @Override
  public List<PspGroupDto> readByBrandAndEnvironment(String brandId, String environmentId) {
    List<PspGroup> pspGroups =
        pspGroupRepository.findByBrandIdAndEnvironmentId(brandId, environmentId);
    return buildEnrichedPspGroupDtos(pspGroups);
  }

  @Override
  public List<PspGroupDto> readByPspId(String pspId) {
    List<PspGroupPsp> pspGroupPsps = pspGroupPspRepository.findByPspId(pspId);
    List<PspGroup> list = pspGroupPsps.stream().map(PspGroupPsp::getPspGroup).toList();
    return buildEnrichedPspGroupDtos(list);
  }

  @Override
  @Transactional
  public PspGroupDto update(String id, @Valid PspGroupDto pspGroupDto) {
    PspGroup existingPspGroup =
        pspGroupRepository
            .findLatestVersionById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorCode.PSP_GROUP_NOT_FOUND.getCode()));

    // Validate name uniqueness for update (exclude current PSP group)
    verifyPspGroupNameUniquenessForUpdate(existingPspGroup, pspGroupDto);

    Integer newVersion = existingPspGroup.getPspGroupId().getVersion() + 1;
    PspGroup updatedPspGroup = pspGroupMapper.toPspGroup(pspGroupDto, newVersion);
    updatedPspGroup.getPspGroupId().setId(existingPspGroup.getPspGroupId().getId());
    updatedPspGroup.setStatus(Status.ENABLED);

    PspGroup savedPspGroup = pspGroupRepository.save(updatedPspGroup);
    createPspAssociations(savedPspGroup, pspGroupDto);
    return buildEnrichedPspGroupDto(savedPspGroup);
  }

  @Override
  @Transactional
  public void delete(String id) {
    if (pspGroupRepository.findLatestVersionById(id).isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, ErrorCode.PSP_GROUP_NOT_FOUND.getCode());
    }

    pspGroupPspRepository.deleteByPspGroupId(id);
    pspGroupRepository.deleteByPspGroupIdId(id);
  }

  private void verifyPspGroupNotExists(PspGroupDto pspGroupDto) {
    if (pspGroupRepository.existsByBrandIdAndEnvironmentIdAndFlowActionIdAndName(
        pspGroupDto.getBrandId(),
        pspGroupDto.getEnvironmentId(),
        pspGroupDto.getFlowActionId(),
        pspGroupDto.getName())) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, ErrorCode.PSP_GROUP_ALREADY_EXISTS.getCode());
    }
  }

  private void verifyPspGroupNameUniquenessForUpdate(
      PspGroup existingPspGroup, PspGroupDto pspGroupDto) {
    // Only validate if name has changed
    if (!existingPspGroup.getName().equals(pspGroupDto.getName())) {
      if (pspGroupRepository.existsByBrandIdAndEnvironmentIdAndFlowActionIdAndName(
          pspGroupDto.getBrandId(),
          pspGroupDto.getEnvironmentId(),
          pspGroupDto.getFlowActionId(),
          pspGroupDto.getName())) {
        throw new ResponseStatusException(
            HttpStatus.CONFLICT, ErrorCode.PSP_GROUP_ALREADY_EXISTS.getCode());
      }
    }
  }

  private void createPspAssociations(PspGroup savedPspGroup, PspGroupDto pspGroupDto) {
    if (pspGroupDto.getPsps() != null && !pspGroupDto.getPsps().isEmpty()) {
      List<PspGroupPsp> pspGroupPsps =
          pspGroupMapper.createPspGroupPsps(
              pspGroupDto.getPsps(),
              savedPspGroup.getPspGroupId().getId(),
              savedPspGroup.getPspGroupId().getVersion());
      List<PspGroupPsp> savedPspGroupPsps = pspGroupPspRepository.saveAll(pspGroupPsps);
      savedPspGroup.setPspGroupPsps(savedPspGroupPsps);
    }
  }

  public List<PspGroupDto> buildEnrichedPspGroupDtos(List<PspGroup> pspGroups) {
    if (CollectionUtils.isEmpty(pspGroups)) {
      return Collections.emptyList();
    }

    Map<String, IdNameDto> pspIdNameDtoMap = getPspIdNameDtoMap(pspGroups);
    Map<String, IdNameDto> flowActionIdNameDtoMap = getFlowActionIdNameDtoMap(pspGroups);

    return buildPspGroupDtos(pspGroups, pspIdNameDtoMap, flowActionIdNameDtoMap);
  }

  public PspGroupDto buildEnrichedPspGroupDto(PspGroup pspGroup) {
    List<PspGroup> pspGroups = List.of(pspGroup);
    return buildEnrichedPspGroupDtos(pspGroups).getFirst();
  }

  private Map<String, IdNameDto> getPspIdNameDtoMap(List<PspGroup> pspGroups) {
    List<String> pspIds = getAllPspIds(pspGroups);

    if (CollectionUtils.isEmpty(pspIds)) {
      return Collections.emptyMap();
    }

    return pspService.getPspIdNameDtoMap(pspIds);
  }

  private List<String> getAllPspIds(List<PspGroup> pspGroups) {
    return pspGroups.stream()
        .map(PspGroup::getPspGroupPsps)
        .filter(Objects::nonNull)
        .flatMap(List::stream)
        .map(PspGroupPsp::getPspId)
        .distinct()
        .collect(Collectors.toList());
  }

  private Map<String, IdNameDto> getFlowActionIdNameDtoMap(List<PspGroup> pspGroups) {
    List<String> allFlowActionIds = getAllFlowActionIds(pspGroups);

    if (CollectionUtils.isEmpty(allFlowActionIds)) {
      return Collections.emptyMap();
    }

    return flowActionService.getFlowActionIdNameDtoMap(allFlowActionIds);
  }

  private List<String> getAllFlowActionIds(List<PspGroup> pspGroups) {
    return pspGroups.stream()
        .map(PspGroup::getFlowActionId)
        .filter(Objects::nonNull)
        .distinct()
        .collect(Collectors.toList());
  }

  private List<PspGroupDto> buildPspGroupDtos(
      List<PspGroup> pspGroups,
      Map<String, IdNameDto> pspMap,
      Map<String, IdNameDto> flowActionMap) {
    return pspGroups.stream()
        .filter(group -> Objects.nonNull(group.getPspGroupPsps()))
        .map(
            group -> {
              List<IdNameDto> psps =
                  group.getPspGroupPsps().stream()
                      .map(PspGroupPsp::getPspId)
                      .map(pspMap::get)
                      .filter(Objects::nonNull)
                      .collect(Collectors.toList());
              PspGroupDto dto = pspGroupMapper.toPspGroupDto(group, psps);
              IdNameDto flowAction = flowActionMap.get(group.getFlowActionId());
              if (flowAction != null) {
                dto.setFlowActionName(flowAction.getName());
              }

              return dto;
            })
        .collect(Collectors.toList());
  }
}
