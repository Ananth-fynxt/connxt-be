package nexxus.psp.service.impl;

import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

import nexxus.flowtarget.dto.FlowTargetDto;
import nexxus.flowtarget.service.FlowTargetService;
import nexxus.psp.dto.*;
import nexxus.psp.entity.MaintenanceWindow;
import nexxus.psp.entity.Psp;
import nexxus.psp.entity.PspOperation;
import nexxus.psp.repository.MaintenanceWindowRepository;
import nexxus.psp.repository.PspOperationRepository;
import nexxus.psp.repository.PspRepository;
import nexxus.psp.service.PspService;
import nexxus.psp.service.mappers.PspMapper;
import nexxus.shared.constants.ErrorCode;
import nexxus.shared.constants.Status;
import nexxus.shared.service.NameUniquenessService;
import nexxus.shared.util.CryptoUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PspServiceImpl implements PspService {

  private final PspRepository pspRepository;
  private final MaintenanceWindowRepository maintenanceWindowRepository;
  private final PspOperationRepository pspOperationRepository;
  private final FlowTargetService flowTargetService;
  private final PspMapper pspMapper;
  private final NameUniquenessService nameUniquenessService;
  private final CryptoUtil cryptoUtil;

  @Override
  @Transactional
  public PspDto create(PspDto pspDto) {
    flowTargetService.validateCredentialsForFlowTarget(
        pspDto.getFlowTargetId(), pspDto.getCredential());
    verifyPspDoesNotExists(pspDto);
    Psp psp = pspMapper.toEntity(pspDto);
    encryptCredentials(psp);
    Psp savedPsp = pspRepository.save(psp);
    return pspMapper.toDto(savedPsp);
  }

  private void verifyPspDoesNotExists(PspDto pspDto) {
    nameUniquenessService.validateForCreate(
        name ->
            pspRepository.existsByBrandIdAndEnvironmentIdAndFlowTargetIdAndName(
                pspDto.getBrandId(), pspDto.getEnvironmentId(), pspDto.getFlowTargetId(), name),
        "PSP",
        pspDto.getName());
  }

  @Override
  public PspDetailsDto getById(String pspId) {
    Psp psp = getPspIfExists(pspId);
    List<MaintenanceWindow> maintenanceWindows = maintenanceWindowRepository.findByPspId(pspId);
    List<PspOperation> operations = pspOperationRepository.findByPspId(pspId);
    FlowTargetDto flowTargetDto = flowTargetService.readWithAssociations(psp.getFlowTargetId());
    PspDetailsDto.FlowTargetInfo flowTargetInfo = pspMapper.toFlowTargetInfo(flowTargetDto);
    return pspMapper.toPspDetailsDto(psp, maintenanceWindows, operations, flowTargetInfo);
  }

  private Psp getPspIfExists(String pspId) {
    return pspRepository
        .findById(pspId)
        .orElseThrow(
            () ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND, ErrorCode.PSP_NOT_FOUND.getCode()));
  }

  @Override
  public List<PspSummaryDto> getByBrandAndEnvironment(String brandId, String environmentId) {
    return pspRepository.findByBrandIdAndEnvironmentId(brandId, environmentId).stream()
        .map(pspMapper::toPspSummaryDto)
        .toList();
  }

  @Override
  public List<PspSummaryDto> getByBrandAndEnvironmentByStatusAndCurrencyAndFlowAction(
      String brandId, String environmentId, String status, String currency, String flowActionId) {
    return pspRepository
        .findByBrandEnvStatusCurrencyAndFlowAction(
            brandId, environmentId, status, currency, flowActionId)
        .stream()
        .map(pspMapper::toPspSummaryDto)
        .toList();
  }

  @Override
  public List<PspSummaryDto> getByBrandAndEnvironmentByStatusAndFlowAction(
      String brandId, String environmentId, String status, String flowActionId) {
    return pspRepository
        .findByBrandEnvStatusAndFlowAction(brandId, environmentId, status, flowActionId)
        .stream()
        .map(pspMapper::toPspSummaryDto)
        .toList();
  }

  @Override
  public List<String> getSupportedCurrenciesByBrandAndEnvironment(
      String brandId, String environmentId) {
    return pspRepository.findSupportedCurrenciesByBrandAndEnvironment(brandId, environmentId);
  }

  @Override
  public List<String> getSupportedCountriesByBrandAndEnvironment(
      String brandId, String environmentId) {
    return pspRepository.findSupportedCountriesByBrandAndEnvironment(brandId, environmentId);
  }

  @Override
  @Transactional
  public PspDetailsDto update(String pspId, UpdatePspDto pspDto) {
    Psp existingPsp = getPspIfExists(pspId);
    validateCredentialsCountriesAndCurrencies(pspDto, existingPsp);

    // Validate name uniqueness for update (exclude current PSP)
    nameUniquenessService.validateForUpdateWithFlowContext(
        existingPsp.getName(),
        pspDto.getName(),
        existingPsp.getBrandId(),
        existingPsp.getEnvironmentId(),
        existingPsp.getFlowTargetId(),
        pspRepository::existsByBrandIdAndEnvironmentIdAndFlowTargetIdAndName,
        "PSP");

    pspMapper.updateEntityFromDto(pspDto, existingPsp);
    if (pspDto.getCredential() != null) {
      encryptCredentials(existingPsp);
    }

    deleteAllExistingConfiguration(pspId);

    List<MaintenanceWindow> latestMaintenanceWindow = createLatestMaintenanceWindow(pspId, pspDto);
    List<PspOperation> latestOperations = createLatestOperations(pspId, pspDto);
    Psp saved = pspRepository.save(existingPsp);
    FlowTargetDto flowTargetDto =
        flowTargetService.readWithAssociations(existingPsp.getFlowTargetId());
    PspDetailsDto.FlowTargetInfo flowTargetInfo = pspMapper.toFlowTargetInfo(flowTargetDto);

    return pspMapper.toPspDetailsDto(
        saved, latestMaintenanceWindow, latestOperations, flowTargetInfo);
  }

  private List<MaintenanceWindow> createLatestMaintenanceWindow(String pspId, UpdatePspDto pspDto) {
    List<UpdatePspDto.MaintenanceWindowDto> maintenanceWindow = pspDto.getMaintenanceWindow();
    if (CollectionUtils.isEmpty(maintenanceWindow)) {
      return Collections.emptyList();
    }
    List<MaintenanceWindow> maintenanceWindows =
        maintenanceWindow.stream().map(dto -> pspMapper.toMaintenanceWindow(dto, pspId)).toList();
    return maintenanceWindowRepository.saveAll(maintenanceWindows);
  }

  private List<PspOperation> createLatestOperations(String pspId, UpdatePspDto pspDto) {
    List<UpdatePspDto.PspOperationDto> operations = pspDto.getOperations();
    if (CollectionUtils.isEmpty(operations)) {
      return Collections.emptyList();
    }

    List<PspOperation> pspOperations =
        operations.stream()
            .map(
                dto ->
                    pspMapper.toPspOperation(
                        dto, pspDto.getBrandId(), pspDto.getEnvironmentId(), pspId))
            .toList();
    return pspOperationRepository.saveAll(pspOperations);
  }

  private void deleteAllExistingConfiguration(String pspId) {
    maintenanceWindowRepository.deleteByPspId(pspId);
    pspOperationRepository.deleteByPspId(pspId);
  }

  private void validateCredentialsCountriesAndCurrencies(UpdatePspDto pspDto, Psp existingPsp) {
    String flowTargetId =
        pspDto.getFlowTargetId() != null ? pspDto.getFlowTargetId() : existingPsp.getFlowTargetId();
    List<String> currencies = extractCurrenciesFromOperations(pspDto.getOperations());
    List<String> countries = extractCountriesFromOperations(pspDto.getOperations());
    flowTargetService.validateCredentialsCurrenciesAndCountries(
        flowTargetId, pspDto.getCredential(), currencies, countries);
  }

  private List<String> extractCurrenciesFromOperations(
      List<UpdatePspDto.PspOperationDto> operations) {
    if (operations == null) {
      return List.of();
    }

    Set<String> currencies = new HashSet<>();
    for (UpdatePspDto.PspOperationDto operation : operations) {
      currencies.addAll(operation.getCurrencies());
    }
    return new ArrayList<>(currencies);
  }

  private List<String> extractCountriesFromOperations(
      List<UpdatePspDto.PspOperationDto> operations) {
    if (operations == null) {
      return List.of();
    }

    return operations.stream()
        .filter(operation -> operation.getCountries() != null)
        .flatMap(operation -> operation.getCountries().stream())
        .distinct()
        .toList();
  }

  @Override
  public Psp getPspIfEnabled(String pspId) {
    Psp psp = getPspIfExists(pspId);
    if (psp.getStatus() != Status.ENABLED) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, ErrorCode.PSP_STATUS_INVALID.getCode());
    }
    return psp;
  }

  @Override
  @Transactional
  public PspSummaryDto updateStatus(String pspId, String status) {
    Psp psp = getPspIfExists(pspId);
    psp.setStatus(Status.valueOf(status));
    Psp saved = pspRepository.save(psp);
    return pspMapper.toPspSummaryDto(saved);
  }

  @Override
  public Map<String, IdNameDto> getPspIdNameDtoMap(List<String> pspIds) {
    if (CollectionUtils.isEmpty(pspIds)) {
      return Map.of();
    }

    List<Psp> psps = pspRepository.findAllById(pspIds);
    return pspMapper.toIdNameDtoMap(psps);
  }

  private void encryptCredentials(Psp psp) {
    if (psp.getCredential() != null) {
      try {
        psp.setCredential(cryptoUtil.encryptCredentialJsonNode(psp.getCredential()));
      } catch (Exception e) {
        throw new RuntimeException("Failed to encrypt credentials", e);
      }
    }
  }
}
