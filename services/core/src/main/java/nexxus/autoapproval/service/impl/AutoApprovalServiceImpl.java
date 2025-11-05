package nexxus.autoapproval.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

import nexxus.autoapproval.dto.AutoApprovalDto;
import nexxus.autoapproval.entity.AutoApproval;
import nexxus.autoapproval.entity.AutoApprovalPsp;
import nexxus.autoapproval.repository.AutoApprovalPspRepository;
import nexxus.autoapproval.repository.AutoApprovalRepository;
import nexxus.autoapproval.service.AutoApprovalService;
import nexxus.autoapproval.service.mappers.AutoApprovalMapper;
import nexxus.flowaction.service.FlowActionService;
import nexxus.psp.dto.IdNameDto;
import nexxus.psp.service.PspService;
import nexxus.shared.constants.ErrorCode;
import nexxus.shared.constants.Status;
import nexxus.shared.service.NameUniquenessService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AutoApprovalServiceImpl implements AutoApprovalService {

  private final AutoApprovalRepository autoApprovalRepository;
  private final AutoApprovalPspRepository autoApprovalPspRepository;
  private final AutoApprovalMapper autoApprovalMapper;
  private final NameUniquenessService nameUniquenessService;
  private final PspService pspService;
  private final FlowActionService flowActionService;

  @Override
  @Transactional
  public AutoApprovalDto create(@Valid AutoApprovalDto autoApprovalDto) {
    nameUniquenessService.validateForCreate(
        name ->
            autoApprovalRepository.existsByBrandIdAndEnvironmentIdAndFlowActionIdAndName(
                autoApprovalDto.getBrandId(),
                autoApprovalDto.getEnvironmentId(),
                autoApprovalDto.getFlowActionId(),
                name),
        "AutoApproval",
        autoApprovalDto.getName());

    AutoApproval autoApproval = autoApprovalMapper.toAutoApproval(autoApprovalDto, 1);
    autoApproval.setStatus(Status.ENABLED);

    AutoApproval savedAutoApproval = autoApprovalRepository.save(autoApproval);
    createAssociations(savedAutoApproval, autoApprovalDto);
    return buildEnrichedAutoApprovalDto(savedAutoApproval);
  }

  @Override
  public AutoApprovalDto readLatest(String id) {
    AutoApproval autoApproval =
        autoApprovalRepository
            .findLatestVersionById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorCode.AUTO_APPROVAL_NOT_FOUND.getCode()));
    return buildEnrichedAutoApprovalDto(autoApproval);
  }

  @Override
  public List<AutoApprovalDto> readByBrandAndEnvironment(String brandId, String environmentId) {
    List<AutoApproval> autoApprovals =
        autoApprovalRepository.findByBrandIdAndEnvironmentId(brandId, environmentId);
    return buildEnrichedAutoApprovalDtos(autoApprovals);
  }

  @Override
  public List<AutoApprovalDto> readByPspId(String pspId) {
    List<AutoApprovalPsp> autoApprovalPsps = autoApprovalPspRepository.findByPspId(pspId);
    List<AutoApproval> autoApprovals = new ArrayList<>();
    for (AutoApprovalPsp autoApprovalPsp : autoApprovalPsps) {
      Optional<AutoApproval> byIdAndVersion =
          autoApprovalRepository.findByAutoApprovalIdIdAndAutoApprovalIdVersion(
              autoApprovalPsp.getAutoApprovalId(), autoApprovalPsp.getAutoApprovalVersion());
      byIdAndVersion.ifPresent(autoApprovals::add);
    }
    return buildEnrichedAutoApprovalDtos(autoApprovals);
  }

  @Override
  @Transactional
  public AutoApprovalDto update(String id, @Valid AutoApprovalDto autoApprovalDto) {
    AutoApproval existingAutoApproval =
        autoApprovalRepository
            .findLatestVersionById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorCode.AUTO_APPROVAL_NOT_FOUND.getCode()));

    nameUniquenessService.validateForUpdateWithFlowContext(
        existingAutoApproval.getName(),
        autoApprovalDto.getName(),
        autoApprovalDto.getBrandId(),
        autoApprovalDto.getEnvironmentId(),
        autoApprovalDto.getFlowActionId(),
        (brandId, environmentId, flowActionId, name) ->
            autoApprovalRepository.existsByBrandIdAndEnvironmentIdAndFlowActionIdAndNameAndIdNot(
                brandId, environmentId, flowActionId, name, id),
        "AutoApproval");

    Integer newVersion = existingAutoApproval.getAutoApprovalId().getVersion() + 1;

    AutoApproval updatedAutoApproval =
        autoApprovalMapper.createUpdatedAutoApproval(
            existingAutoApproval, autoApprovalDto, newVersion);

    AutoApproval savedAutoApproval = autoApprovalRepository.save(updatedAutoApproval);
    createAssociations(savedAutoApproval, autoApprovalDto);
    return buildEnrichedAutoApprovalDto(savedAutoApproval);
  }

  @Override
  @Transactional
  public void delete(String id) {
    if (autoApprovalRepository.findLatestVersionById(id).isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, ErrorCode.AUTO_APPROVAL_NOT_FOUND.getCode());
    }

    autoApprovalPspRepository.deleteByAutoApprovalId(id);
    autoApprovalRepository.deleteByAutoApprovalIdId(id);
  }

  public BigDecimal getMaxAmountForFlowAction(String flowActionId) {
    Optional<AutoApproval> latestVersionByFlowActionId =
        autoApprovalRepository.findFirstLatestVersionByFlowActionId(flowActionId);
    if (latestVersionByFlowActionId.isPresent()) {
      return latestVersionByFlowActionId.get().getMaxAmount();
    } else {
      return new BigDecimal(0);
    }
  }

  private void createAssociations(AutoApproval autoApproval, AutoApprovalDto requestDto) {
    createPsps(autoApproval, requestDto.getPsps());
  }

  private void createPsps(AutoApproval autoApproval, List<IdNameDto> psps) {
    if (psps != null && !psps.isEmpty()) {
      List<AutoApprovalPsp> autoApprovalPsps = new ArrayList<>();
      for (IdNameDto psp : psps) {
        AutoApprovalPsp autoApprovalPsp =
            AutoApprovalPsp.builder()
                .autoApprovalId(autoApproval.getAutoApprovalId().getId())
                .autoApprovalVersion(autoApproval.getAutoApprovalId().getVersion())
                .pspId(psp.getId())
                .build();
        autoApprovalPsps.add(autoApprovalPsp);
      }
      autoApprovalPspRepository.saveAll(autoApprovalPsps);
    }
  }

  public List<AutoApprovalDto> buildEnrichedAutoApprovalDtos(List<AutoApproval> autoApprovals) {
    if (CollectionUtils.isEmpty(autoApprovals)) {
      return Collections.emptyList();
    }

    Map<String, IdNameDto> pspIdNameDtoMap = getPspIdNameDtoMap(autoApprovals);
    Map<String, IdNameDto> flowActionIdNameDtoMap = getFlowActionIdNameDtoMap(autoApprovals);

    return buildAutoApprovalDtos(autoApprovals, pspIdNameDtoMap, flowActionIdNameDtoMap);
  }

  public AutoApprovalDto buildEnrichedAutoApprovalDto(AutoApproval autoApproval) {
    List<AutoApproval> autoApprovals = List.of(autoApproval);
    return buildEnrichedAutoApprovalDtos(autoApprovals).getFirst();
  }

  private Map<String, IdNameDto> getPspIdNameDtoMap(List<AutoApproval> autoApprovals) {
    List<String> pspIds = getAllPspIds(autoApprovals);

    if (CollectionUtils.isEmpty(pspIds)) {
      return Collections.emptyMap();
    }

    return pspService.getPspIdNameDtoMap(pspIds);
  }

  private List<String> getAllPspIds(List<AutoApproval> autoApprovals) {
    return autoApprovals.stream()
        .map(
            autoApproval ->
                autoApprovalPspRepository.findByAutoApprovalIdAndAutoApprovalVersion(
                    autoApproval.getAutoApprovalId().getId(),
                    autoApproval.getAutoApprovalId().getVersion()))
        .filter(psps -> !psps.isEmpty())
        .flatMap(List::stream)
        .map(AutoApprovalPsp::getPspId)
        .distinct()
        .collect(Collectors.toList());
  }

  private Map<String, IdNameDto> getFlowActionIdNameDtoMap(List<AutoApproval> autoApprovals) {
    List<String> allFlowActionIds = getAllFlowActionIds(autoApprovals);

    if (CollectionUtils.isEmpty(allFlowActionIds)) {
      return Collections.emptyMap();
    }

    return flowActionService.getFlowActionIdNameDtoMap(allFlowActionIds);
  }

  private List<String> getAllFlowActionIds(List<AutoApproval> autoApprovals) {
    return autoApprovals.stream()
        .map(AutoApproval::getFlowActionId)
        .filter(Objects::nonNull)
        .distinct()
        .collect(Collectors.toList());
  }

  private List<AutoApprovalDto> buildAutoApprovalDtos(
      List<AutoApproval> autoApprovals,
      Map<String, IdNameDto> pspMap,
      Map<String, IdNameDto> flowActionMap) {
    return autoApprovals.stream()
        .map(
            autoApproval -> {
              AutoApprovalDto dto = autoApprovalMapper.toAutoApprovalDto(autoApproval);
              appendPsps(autoApproval, dto, pspMap);
              addFlowActionName(dto, flowActionMap);
              return dto;
            })
        .collect(Collectors.toList());
  }

  private void addFlowActionName(
      AutoApprovalDto responseDto, Map<String, IdNameDto> flowActionMap) {
    if (responseDto.getFlowActionId() != null) {
      IdNameDto flowAction = flowActionMap.get(responseDto.getFlowActionId());
      if (flowAction != null) {
        responseDto.setFlowActionName(flowAction.getName());
      }
    }
  }

  private void appendPsps(
      AutoApproval autoApproval, AutoApprovalDto responseDto, Map<String, IdNameDto> pspMap) {
    List<AutoApprovalPsp> autoApprovalPsps =
        autoApprovalPspRepository.findByAutoApprovalIdAndAutoApprovalVersion(
            autoApproval.getAutoApprovalId().getId(),
            autoApproval.getAutoApprovalId().getVersion());

    if (!autoApprovalPsps.isEmpty()) {
      List<String> pspIds =
          autoApprovalPsps.stream().map(AutoApprovalPsp::getPspId).collect(Collectors.toList());

      List<IdNameDto> enrichedPsps =
          pspIds.stream()
              .map(pspId -> pspMap.getOrDefault(pspId, IdNameDto.builder().id(pspId).build()))
              .collect(Collectors.toList());

      responseDto.setPsps(enrichedPsps);
    } else {
      responseDto.setPsps(Collections.emptyList());
    }
  }
}
