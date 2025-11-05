package connxt.fee.service.impl;

import java.util.ArrayList;
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

import connxt.fee.dto.FeeComponentDto;
import connxt.fee.dto.FeeDto;
import connxt.fee.entity.Fee;
import connxt.fee.entity.FeeComponent;
import connxt.fee.entity.FeePsp;
import connxt.fee.repository.FeeComponentRepository;
import connxt.fee.repository.FeePspRepository;
import connxt.fee.repository.FeeRepository;
import connxt.fee.service.FeeService;
import connxt.fee.service.mappers.FeeComponentMapper;
import connxt.fee.service.mappers.FeeMapper;
import connxt.flowaction.service.FlowActionService;
import connxt.psp.dto.IdNameDto;
import connxt.psp.service.PspService;
import connxt.shared.constants.ErrorCode;
import connxt.shared.constants.Status;
import connxt.shared.service.NameUniquenessService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeeServiceImpl implements FeeService {

  private final FeeRepository feeRepository;
  private final FeeComponentRepository feeComponentRepository;
  private final FeePspRepository feePspRepository;
  private final FeeMapper feeMapper;
  private final FeeComponentMapper feeComponentMapper;
  private final PspService pspService;
  private final FlowActionService flowActionService;
  private final NameUniquenessService nameUniquenessService;

  @Override
  @Transactional
  public FeeDto create(@Valid FeeDto feeDto) {
    verifyFeeNotExists(feeDto);

    Fee fee = feeMapper.toFee(feeDto, 1);
    fee.setStatus(Status.ENABLED);

    Fee savedFee = feeRepository.save(fee);
    createAssociations(savedFee, feeDto);
    return buildEnrichedFeeDto(savedFee);
  }

  @Override
  public FeeDto readLatest(String id) {
    Fee fee =
        feeRepository
            .findLatestVersionById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorCode.FEE_NOT_FOUND.getCode()));
    return buildEnrichedFeeDto(fee);
  }

  @Override
  public List<FeeDto> readByBrandAndEnvironment(String brandId, String environmentId) {
    List<Fee> fees = feeRepository.findByBrandIdAndEnvironmentId(brandId, environmentId);
    return buildEnrichedFeeDtos(fees);
  }

  @Override
  public List<FeeDto> readByPspId(String pspId) {
    List<Fee> fees = feeRepository.findLatestFeesByPspId(pspId);
    return buildEnrichedFeeDtos(fees);
  }

  @Override
  public List<FeeDto> readLatestEnabledFeeRulesByCriteria(
      List<String> pspIds,
      String brandId,
      String environmentId,
      String flowActionId,
      String currency,
      Status status) {
    List<Fee> fees =
        feeRepository.findLatestEnabledFeeRulesByCriteria(
            pspIds, brandId, environmentId, flowActionId, currency, status);
    return buildEnrichedFeeDtos(fees);
  }

  @Override
  @Transactional
  public FeeDto update(String id, @Valid FeeDto feeDto) {
    Fee existingFee =
        feeRepository
            .findLatestVersionById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorCode.FEE_NOT_FOUND.getCode()));

    nameUniquenessService.validateForUpdateWithFlowContext(
        existingFee.getName(),
        feeDto.getName(),
        feeDto.getBrandId(),
        feeDto.getEnvironmentId(),
        feeDto.getFlowActionId(),
        feeRepository::existsByBrandIdAndEnvironmentIdAndFlowActionIdAndName,
        "Fee");

    Integer newVersion = existingFee.getFeeId().getVersion() + 1;

    Fee updatedFee = feeMapper.toFee(feeDto, newVersion);
    updatedFee.getFeeId().setId(existingFee.getFeeId().getId());
    updatedFee.setStatus(Status.ENABLED);

    Fee savedFee = feeRepository.save(updatedFee);
    createAssociations(savedFee, feeDto);
    return buildEnrichedFeeDto(savedFee);
  }

  @Override
  @Transactional
  public void delete(String id) {
    if (feeRepository.findLatestVersionById(id).isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.FEE_NOT_FOUND.getCode());
    }

    feeComponentRepository.deleteByFeeComponentIdFeeId(id);
    feePspRepository.deleteByFeeId(id);
    feeRepository.deleteByFeeIdId(id);
  }

  private void createAssociations(Fee fee, FeeDto requestDto) {
    createPsps(fee, requestDto.getPsps());
    createComponents(fee, requestDto.getComponents());
  }

  private void createComponents(Fee fee, List<FeeComponentDto> feeComponentDtos) {
    if (feeComponentDtos != null && !feeComponentDtos.isEmpty()) {
      List<FeeComponent> feeComponents = new ArrayList<>();
      for (FeeComponentDto componentDto : feeComponentDtos) {
        FeeComponent component =
            feeComponentMapper.toFeeComponent(
                componentDto, fee.getFeeId().getId(), fee.getFeeId().getVersion());
        feeComponents.add(component);
      }
      feeComponentRepository.saveAll(feeComponents);
    }
  }

  private void createPsps(Fee fee, List<IdNameDto> feePspDtos) {
    if (feePspDtos != null && !feePspDtos.isEmpty()) {
      List<FeePsp> feePsps = new ArrayList<>();
      for (IdNameDto pspDto : feePspDtos) {
        if (pspDto != null && pspDto.getId() != null) {
          FeePsp feePsp =
              FeePsp.builder()
                  .feeId(fee.getFeeId().getId())
                  .feeVersion(fee.getFeeId().getVersion())
                  .pspId(pspDto.getId())
                  .build();
          feePsps.add(feePsp);
        }
      }
      feePspRepository.saveAll(feePsps);
    }
  }

  public List<FeeDto> buildEnrichedFeeDtos(List<Fee> fees) {
    if (CollectionUtils.isEmpty(fees)) {
      return Collections.emptyList();
    }

    Map<String, IdNameDto> pspIdNameDtoMap = getPspIdNameDtoMap(fees);
    Map<String, IdNameDto> flowActionIdNameDtoMap = getFlowActionIdNameDtoMap(fees);

    return buildFeeDtos(fees, pspIdNameDtoMap, flowActionIdNameDtoMap);
  }

  public FeeDto buildEnrichedFeeDto(Fee fee) {
    List<Fee> fees = List.of(fee);
    return buildEnrichedFeeDtos(fees).getFirst();
  }

  private Map<String, IdNameDto> getPspIdNameDtoMap(List<Fee> fees) {
    List<String> pspIds = getAllPspIds(fees);

    if (CollectionUtils.isEmpty(pspIds)) {
      return Collections.emptyMap();
    }

    return pspService.getPspIdNameDtoMap(pspIds);
  }

  private List<String> getAllPspIds(List<Fee> fees) {
    return fees.stream()
        .map(
            fee ->
                feePspRepository.findByFeeIdAndFeeVersion(
                    fee.getFeeId().getId(), fee.getFeeId().getVersion()))
        .filter(psps -> !psps.isEmpty())
        .flatMap(List::stream)
        .map(FeePsp::getPspId)
        .distinct()
        .collect(Collectors.toList());
  }

  private Map<String, IdNameDto> getFlowActionIdNameDtoMap(List<Fee> fees) {
    List<String> allFlowActionIds = getAllFlowActionIds(fees);

    if (CollectionUtils.isEmpty(allFlowActionIds)) {
      return Collections.emptyMap();
    }

    return flowActionService.getFlowActionIdNameDtoMap(allFlowActionIds);
  }

  private List<String> getAllFlowActionIds(List<Fee> fees) {
    return fees.stream()
        .map(Fee::getFlowActionId)
        .filter(Objects::nonNull)
        .distinct()
        .collect(Collectors.toList());
  }

  private List<FeeDto> buildFeeDtos(
      List<Fee> fees, Map<String, IdNameDto> pspMap, Map<String, IdNameDto> flowActionMap) {
    return fees.stream()
        .map(
            fee -> {
              FeeDto dto = feeMapper.toFeeDto(fee);
              appendPsps(fee, dto, pspMap);
              appendComponents(fee, dto);
              addFlowActionName(dto, flowActionMap);
              return dto;
            })
        .collect(Collectors.toList());
  }

  private void addFlowActionName(FeeDto responseDto, Map<String, IdNameDto> flowActionMap) {
    if (responseDto.getFlowActionId() != null) {
      IdNameDto flowAction = flowActionMap.get(responseDto.getFlowActionId());
      if (flowAction != null) {
        responseDto.setFlowActionName(flowAction.getName());
      }
    }
  }

  private void appendPsps(Fee fee, FeeDto responseDto, Map<String, IdNameDto> pspMap) {
    List<FeePsp> feePsps =
        feePspRepository.findByFeeIdAndFeeVersion(
            fee.getFeeId().getId(), fee.getFeeId().getVersion());

    if (!feePsps.isEmpty()) {
      List<String> pspIds = feePsps.stream().map(FeePsp::getPspId).collect(Collectors.toList());

      List<IdNameDto> enrichedPsps =
          pspIds.stream()
              .map(pspId -> pspMap.getOrDefault(pspId, IdNameDto.builder().id(pspId).build()))
              .collect(Collectors.toList());

      responseDto.setPsps(enrichedPsps);
    } else {
      responseDto.setPsps(Collections.emptyList());
    }
  }

  private void appendComponents(Fee fee, FeeDto responseDto) {
    List<FeeComponent> feeComponents =
        feeComponentRepository.findByFeeComponentIdFeeIdAndFeeComponentIdFeeVersion(
            fee.getFeeId().getId(), fee.getFeeId().getVersion());
    responseDto.setComponents(
        feeComponents.stream().map(feeComponentMapper::toFeeComponentDto).toList());
  }

  private void verifyFeeNotExists(FeeDto feeDto) {
    nameUniquenessService.validateForCreate(
        name ->
            feeRepository.existsByBrandIdAndEnvironmentIdAndFlowActionIdAndName(
                feeDto.getBrandId(), feeDto.getEnvironmentId(), feeDto.getFlowActionId(), name),
        "Fee",
        feeDto.getName());
  }
}
