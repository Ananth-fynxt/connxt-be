package connxt.transactionlimit.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

import connxt.flowaction.service.FlowActionService;
import connxt.psp.dto.IdNameDto;
import connxt.psp.service.PspService;
import connxt.shared.constants.ErrorCode;
import connxt.shared.constants.Status;
import connxt.transactionlimit.dto.TransactionLimitDto;
import connxt.transactionlimit.dto.TransactionLimitPspActionDto;
import connxt.transactionlimit.entity.TransactionLimit;
import connxt.transactionlimit.entity.TransactionLimitPsp;
import connxt.transactionlimit.entity.TransactionLimitPspAction;
import connxt.transactionlimit.repository.TransactionLimitPspActionRepository;
import connxt.transactionlimit.repository.TransactionLimitPspRepository;
import connxt.transactionlimit.repository.TransactionLimitRepository;
import connxt.transactionlimit.service.TransactionLimitService;
import connxt.transactionlimit.service.mappers.TransactionLimitMapper;
import connxt.transactionlimit.service.mappers.TransactionLimitPspActionMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionLimitServiceImpl implements TransactionLimitService {

  private final TransactionLimitRepository transactionLimitRepository;
  private final TransactionLimitPspActionRepository transactionLimitPspActionRepository;
  private final TransactionLimitPspRepository transactionLimitPspRepository;
  private final TransactionLimitMapper transactionLimitMapper;
  private final TransactionLimitPspActionMapper transactionLimitPspActionMapper;
  private final PspService pspService;
  private final FlowActionService flowActionService;

  @Override
  @Transactional
  public TransactionLimitDto create(@Valid TransactionLimitDto transactionLimitDto) {
    verifyTransactionLimitNotExists(transactionLimitDto);

    TransactionLimit transactionLimit =
        transactionLimitMapper.toTransactionLimit(transactionLimitDto, 1);
    transactionLimit.setStatus(Status.ENABLED);

    TransactionLimit savedTransactionLimit = transactionLimitRepository.save(transactionLimit);
    createAssociations(savedTransactionLimit, transactionLimitDto);
    return buildEnrichedTransactionLimitDto(savedTransactionLimit);
  }

  @Override
  public TransactionLimitDto readLatest(String id) {
    TransactionLimit transactionLimit =
        transactionLimitRepository
            .findLatestVersionById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorCode.TRANSACTION_LIMIT_NOT_FOUND.getCode()));
    return buildEnrichedTransactionLimitDto(transactionLimit);
  }

  @Override
  public List<TransactionLimitDto> readByBrandAndEnvironment(String brandId, String environmentId) {
    List<TransactionLimit> transactionLimits =
        transactionLimitRepository.findByBrandIdAndEnvironmentId(brandId, environmentId);
    return buildEnrichedTransactionLimitDtos(transactionLimits);
  }

  @Override
  public List<TransactionLimitDto> readByPspId(String pspId) {
    List<TransactionLimitPsp> transactionLimitPsps =
        transactionLimitPspRepository.findLatestVersionsByPspId(pspId);

    List<TransactionLimit> transactionLimits = new ArrayList<>();
    for (TransactionLimitPsp transactionLimitPsp : transactionLimitPsps) {
      Optional<TransactionLimit> byIdAndVersion =
          transactionLimitRepository.findByTransactionLimitIdIdAndTransactionLimitIdVersion(
              transactionLimitPsp.getTransactionLimitId(),
              transactionLimitPsp.getTransactionLimitVersion());
      if (byIdAndVersion.isPresent()) {
        transactionLimits.add(byIdAndVersion.get());
      }
    }
    return buildEnrichedTransactionLimitDtos(transactionLimits);
  }

  @Override
  public List<TransactionLimitDto> readLatestEnabledTransactionLimitsByCriteria(
      List<String> pspIds,
      String brandId,
      String environmentId,
      String flowActionId,
      String currency,
      Status status) {
    List<TransactionLimit> transactionLimits =
        transactionLimitRepository.findLatestEnabledTransactionLimitsByCriteria(
            pspIds, brandId, environmentId, flowActionId, currency, status);
    return buildEnrichedTransactionLimitDtos(transactionLimits);
  }

  @Override
  @Transactional
  public TransactionLimitDto update(String id, @Valid TransactionLimitDto transactionLimitDto) {
    TransactionLimit existingTransactionLimit =
        transactionLimitRepository
            .findLatestVersionById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorCode.TRANSACTION_LIMIT_NOT_FOUND.getCode()));

    // Validate name uniqueness for update (exclude current transaction limit)
    verifyTransactionLimitNameUniquenessForUpdate(existingTransactionLimit, transactionLimitDto);

    Integer newVersion = existingTransactionLimit.getTransactionLimitId().getVersion() + 1;

    TransactionLimit updatedTransactionLimit =
        transactionLimitMapper.toTransactionLimit(transactionLimitDto, newVersion);
    updatedTransactionLimit.setStatus(Status.ENABLED);

    TransactionLimit savedTransactionLimit =
        transactionLimitRepository.save(updatedTransactionLimit);
    createAssociations(savedTransactionLimit, transactionLimitDto);
    return buildEnrichedTransactionLimitDto(savedTransactionLimit);
  }

  @Override
  @Transactional
  public void delete(String id) {
    if (transactionLimitRepository.findLatestVersionById(id).isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, ErrorCode.TRANSACTION_LIMIT_NOT_FOUND.getCode());
    }

    transactionLimitPspActionRepository.deleteByTransactionLimitId(id);
    transactionLimitPspRepository.deleteByTransactionLimitId(id);
    transactionLimitRepository.deleteByTransactionLimitIdId(id);
  }

  private void createAssociations(
      TransactionLimit transactionLimit, TransactionLimitDto requestDto) {
    createPsps(transactionLimit, requestDto.getPsps());
    createPspActions(transactionLimit, requestDto.getPspActions());
  }

  private void createPspActions(
      TransactionLimit transactionLimit, List<TransactionLimitPspActionDto> pspActionDtos) {
    if (pspActionDtos != null && !pspActionDtos.isEmpty()) {
      List<TransactionLimitPspAction> pspActions = new ArrayList<>();
      for (TransactionLimitPspActionDto actionDto : pspActionDtos) {
        TransactionLimitPspAction pspAction =
            transactionLimitPspActionMapper.toTransactionLimitPspAction(
                actionDto,
                transactionLimit.getTransactionLimitId().getId(),
                transactionLimit.getTransactionLimitId().getVersion());
        pspActions.add(pspAction);
      }
      transactionLimitPspActionRepository.saveAll(pspActions);
    }
  }

  private void createPsps(TransactionLimit transactionLimit, List<IdNameDto> pspDtos) {
    if (pspDtos != null && !pspDtos.isEmpty()) {
      List<TransactionLimitPsp> transactionLimitPsps = new ArrayList<>();
      for (IdNameDto pspDto : pspDtos) {
        if (pspDto != null && pspDto.getId() != null) {
          TransactionLimitPsp transactionLimitPsp =
              TransactionLimitPsp.builder()
                  .transactionLimitId(transactionLimit.getTransactionLimitId().getId())
                  .transactionLimitVersion(transactionLimit.getTransactionLimitId().getVersion())
                  .pspId(pspDto.getId())
                  .build();
          transactionLimitPsps.add(transactionLimitPsp);
        }
      }
      transactionLimitPspRepository.saveAll(transactionLimitPsps);
    }
  }

  private void appendPspActions(
      TransactionLimit transactionLimit,
      TransactionLimitDto responseDto,
      Map<String, IdNameDto> flowActionMap) {
    List<TransactionLimitPspAction> pspActions =
        transactionLimitPspActionRepository.findByTransactionLimitIdAndTransactionLimitVersion(
            transactionLimit.getTransactionLimitId().getId(),
            transactionLimit.getTransactionLimitId().getVersion());

    List<TransactionLimitPspActionDto> enrichedPspActions =
        pspActions.stream()
            .map(transactionLimitPspActionMapper::toTransactionLimitPspActionDto)
            .map(
                dto -> {
                  if (dto.getFlowActionId() != null) {
                    IdNameDto flowAction = flowActionMap.get(dto.getFlowActionId());
                    if (flowAction != null) {
                      dto.setFlowActionName(flowAction.getName());
                    }
                  }
                  return dto;
                })
            .toList();

    responseDto.setPspActions(enrichedPspActions);
  }

  private void verifyTransactionLimitNotExists(TransactionLimitDto transactionLimitDto) {
    if (transactionLimitRepository.existsByBrandIdAndEnvironmentIdAndName(
        transactionLimitDto.getBrandId(),
        transactionLimitDto.getEnvironmentId(),
        transactionLimitDto.getName())) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, ErrorCode.TRANSACTION_LIMIT_ALREADY_EXISTS.getCode());
    }
  }

  private void verifyTransactionLimitNameUniquenessForUpdate(
      TransactionLimit existingTransactionLimit, TransactionLimitDto transactionLimitDto) {
    // Only validate if name has changed
    if (!existingTransactionLimit.getName().equals(transactionLimitDto.getName())) {
      if (transactionLimitRepository.existsByBrandIdAndEnvironmentIdAndName(
          transactionLimitDto.getBrandId(),
          transactionLimitDto.getEnvironmentId(),
          transactionLimitDto.getName())) {
        throw new ResponseStatusException(
            HttpStatus.CONFLICT, ErrorCode.TRANSACTION_LIMIT_ALREADY_EXISTS.getCode());
      }
    }
  }

  public List<TransactionLimitDto> buildEnrichedTransactionLimitDtos(
      List<TransactionLimit> transactionLimits) {
    if (CollectionUtils.isEmpty(transactionLimits)) {
      return Collections.emptyList();
    }

    Map<String, IdNameDto> pspIdNameDtoMap = getPspIdNameDtoMap(transactionLimits);
    Map<String, IdNameDto> flowActionIdNameDtoMap = getFlowActionIdNameDtoMap(transactionLimits);

    return buildTransactionLimitDtos(transactionLimits, pspIdNameDtoMap, flowActionIdNameDtoMap);
  }

  public TransactionLimitDto buildEnrichedTransactionLimitDto(TransactionLimit transactionLimit) {
    List<TransactionLimit> transactionLimits = List.of(transactionLimit);
    return buildEnrichedTransactionLimitDtos(transactionLimits).getFirst();
  }

  private Map<String, IdNameDto> getPspIdNameDtoMap(List<TransactionLimit> transactionLimits) {
    List<String> pspIds = getAllPspIds(transactionLimits);

    if (CollectionUtils.isEmpty(pspIds)) {
      return Collections.emptyMap();
    }

    return pspService.getPspIdNameDtoMap(pspIds);
  }

  private List<String> getAllPspIds(List<TransactionLimit> transactionLimits) {
    return transactionLimits.stream()
        .map(
            transactionLimit ->
                transactionLimitPspRepository.findByTransactionLimitIdAndTransactionLimitVersion(
                    transactionLimit.getTransactionLimitId().getId(),
                    transactionLimit.getTransactionLimitId().getVersion()))
        .filter(psps -> !psps.isEmpty())
        .flatMap(List::stream)
        .map(TransactionLimitPsp::getPspId)
        .distinct()
        .collect(Collectors.toList());
  }

  private Map<String, IdNameDto> getFlowActionIdNameDtoMap(
      List<TransactionLimit> transactionLimits) {
    List<String> allFlowActionIds = getAllFlowActionIds(transactionLimits);

    if (CollectionUtils.isEmpty(allFlowActionIds)) {
      return Collections.emptyMap();
    }

    return flowActionService.getFlowActionIdNameDtoMap(allFlowActionIds);
  }

  private List<String> getAllFlowActionIds(List<TransactionLimit> transactionLimits) {
    return transactionLimits.stream()
        .map(
            transactionLimit ->
                transactionLimitPspActionRepository
                    .findByTransactionLimitIdAndTransactionLimitVersion(
                        transactionLimit.getTransactionLimitId().getId(),
                        transactionLimit.getTransactionLimitId().getVersion()))
        .filter(actions -> !actions.isEmpty())
        .flatMap(List::stream)
        .map(TransactionLimitPspAction::getFlowActionId)
        .distinct()
        .collect(Collectors.toList());
  }

  private List<TransactionLimitDto> buildTransactionLimitDtos(
      List<TransactionLimit> transactionLimits,
      Map<String, IdNameDto> pspMap,
      Map<String, IdNameDto> flowActionMap) {
    return transactionLimits.stream()
        .map(
            transactionLimit -> {
              TransactionLimitDto dto =
                  transactionLimitMapper.toTransactionLimitDto(transactionLimit);
              appendPsps(transactionLimit, dto, pspMap);
              appendPspActions(transactionLimit, dto, flowActionMap);
              return dto;
            })
        .collect(Collectors.toList());
  }

  private void appendPsps(
      TransactionLimit transactionLimit,
      TransactionLimitDto responseDto,
      Map<String, IdNameDto> pspMap) {
    List<TransactionLimitPsp> transactionLimitPsps =
        transactionLimitPspRepository.findByTransactionLimitIdAndTransactionLimitVersion(
            transactionLimit.getTransactionLimitId().getId(),
            transactionLimit.getTransactionLimitId().getVersion());

    if (!transactionLimitPsps.isEmpty()) {
      List<String> pspIds =
          transactionLimitPsps.stream()
              .map(TransactionLimitPsp::getPspId)
              .collect(Collectors.toList());

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
