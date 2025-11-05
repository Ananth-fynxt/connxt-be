package connxt.riskrule.service.impl;

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

import connxt.flowaction.service.FlowActionService;
import connxt.psp.dto.IdNameDto;
import connxt.psp.service.PspOperationsService;
import connxt.psp.service.PspService;
import connxt.riskrule.dto.RiskRuleDto;
import connxt.riskrule.entity.RiskRule;
import connxt.riskrule.entity.RiskRulePsp;
import connxt.riskrule.repository.RiskRulePspRepository;
import connxt.riskrule.repository.RiskRuleRepository;
import connxt.riskrule.service.RiskRuleService;
import connxt.riskrule.service.mappers.RiskRuleMapper;
import connxt.shared.constants.ErrorCode;
import connxt.shared.constants.RiskAction;
import connxt.shared.constants.RiskType;
import connxt.shared.constants.Status;
import connxt.shared.service.NameUniquenessService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RiskRuleServiceImpl implements RiskRuleService {

  private final RiskRuleRepository riskRuleRepository;
  private final RiskRulePspRepository riskRulePspRepository;
  private final PspOperationsService pspOperationsService;
  private final PspService pspService;
  private final FlowActionService flowActionService;
  private final RiskRuleMapper riskRuleMapper;
  private final NameUniquenessService nameUniquenessService;

  @Override
  @Transactional
  public RiskRuleDto create(RiskRuleDto dto) {
    verifyRiskRuleNotExists(dto);
    validateCustomerCriteria(dto);
    List<String> pspIds = dto.getPsps().stream().map(IdNameDto::getId).collect(Collectors.toList());
    validateConfiguration(dto, pspIds);
    RiskRule riskRule = riskRuleMapper.toRiskRule(dto, 1);
    RiskRule savedRiskRule = riskRuleRepository.save(riskRule);
    createRiskRulePspAssociations(savedRiskRule, dto);
    return buildEnrichedRiskRuleDto(savedRiskRule);
  }

  @Override
  @Transactional
  public RiskRuleDto update(String id, @Valid RiskRuleDto dto) {
    RiskRule existingRiskRule = getRiskRuleIfExists(id);
    validateCustomerCriteria(dto);
    List<String> pspIds = dto.getPsps().stream().map(IdNameDto::getId).collect(Collectors.toList());
    validateConfiguration(dto, pspIds);

    // Validate name uniqueness for update (exclude current risk rule)
    nameUniquenessService.validateForUpdateWithFlowContext(
        existingRiskRule.getName(),
        dto.getName(),
        dto.getBrandId(),
        dto.getEnvironmentId(),
        dto.getFlowActionId(),
        riskRuleRepository::existsByBrandIdAndEnvironmentIdAndFlowActionIdAndName,
        "Risk Rule");

    RiskRule newRiskRule = riskRuleMapper.copyRiskRuleWithIncrementedVersion(existingRiskRule);
    riskRuleMapper.updateRiskRule(dto, newRiskRule);
    RiskRule savedRiskRule = riskRuleRepository.save(newRiskRule);
    createRiskRulePspAssociations(savedRiskRule, dto);
    return buildEnrichedRiskRuleDto(savedRiskRule);
  }

  @Override
  public List<RiskRuleDto> readAll() {
    List<RiskRule> riskRules = riskRuleRepository.findAll();
    return buildEnrichedRiskRuleDtos(riskRules);
  }

  @Override
  public RiskRuleDto read(String id, Integer version) {
    RiskRule riskRule = getRiskRuleIfExists(id, version);
    return buildEnrichedRiskRuleDto(riskRule);
  }

  @Override
  public RiskRuleDto readLatest(String id) {
    RiskRule riskRule = getRiskRuleIfExists(id);
    return buildEnrichedRiskRuleDto(riskRule);
  }

  @Override
  public List<RiskRuleDto> readByBrandAndEnvironment(String brandId, String environmentId) {
    List<RiskRule> riskRules =
        riskRuleRepository.findByBrandIdAndEnvironmentId(brandId, environmentId);
    return buildEnrichedRiskRuleDtos(riskRules);
  }

  @Override
  public List<RiskRuleDto> readByPspId(String pspId) {
    List<RiskRule> riskRules = riskRuleRepository.findLatestRiskRulesByPspId(pspId);
    return buildEnrichedRiskRuleDtos(riskRules);
  }

  @Override
  public List<RiskRuleDto> readByPspIds(List<String> pspIds) {
    if (CollectionUtils.isEmpty(pspIds)) {
      return Collections.emptyList();
    }

    List<RiskRulePsp> latestRiskRulePsps =
        riskRulePspRepository.findLatestRiskRulePspsByPspIds(pspIds);
    List<RiskRule> riskRules =
        latestRiskRulePsps.stream()
            .map(RiskRulePsp::getRiskRule)
            .filter(Objects::nonNull)
            .distinct() // Remove duplicates in case same risk rule is associated with multiple PSPs
            .collect(Collectors.toList());
    return buildEnrichedRiskRuleDtos(riskRules);
  }

  @Override
  public List<RiskRuleDto> readLatestEnabledRiskRulesByCriteria(
      List<String> pspIds,
      String brandId,
      String environmentId,
      String flowActionId,
      String currency,
      RiskAction action,
      Status status) {
    List<RiskRule> riskRules =
        riskRuleRepository.findLatestEnabledRiskRulesByCriteria(
            pspIds, brandId, environmentId, flowActionId, currency, action, status);
    return buildEnrichedRiskRuleDtos(riskRules);
  }

  @Override
  @Transactional
  public void delete(String id) {
    if (riskRuleRepository.findLatestVersionById(id).isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, ErrorCode.RISK_RULE_NOT_FOUND.getCode());
    }

    riskRulePspRepository.deleteByRiskRuleId(id);
    riskRuleRepository.deleteByRiskRuleIdId(id);
  }

  private void validateConfiguration(RiskRuleDto dto, List<String> pspIds) {
    boolean isValidPair =
        pspOperationsService.validateByPspIdsAndFlowActionIdAndCurrency(
            pspIds, dto.getFlowActionId(), dto.getCurrency());
    if (!isValidPair) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, ErrorCode.PSP_CONFIGURATION_ERROR.getCode());
    }
  }

  private void createRiskRulePspAssociations(RiskRule savedRiskRule, RiskRuleDto dto) {
    if (dto.getPsps() != null && !dto.getPsps().isEmpty()) {
      List<RiskRulePsp> riskRulePsps =
          riskRuleMapper.createRiskRulePsps(
              dto.getPsps(),
              savedRiskRule.getRiskRuleId().getId(),
              savedRiskRule.getRiskRuleId().getVersion());
      List<RiskRulePsp> savedRiskRulePsps = riskRulePspRepository.saveAll(riskRulePsps);
      savedRiskRule.setRiskRulePsps(savedRiskRulePsps);
    }
  }

  private RiskRule getRiskRuleIfExists(String id, Integer version) {
    return riskRuleRepository
        .findByRiskRuleIdIdAndRiskRuleIdVersion(id, version)
        .orElseThrow(
            () ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND, ErrorCode.RISK_RULE_NOT_FOUND.getCode()));
  }

  private RiskRule getRiskRuleIfExists(String id) {
    return riskRuleRepository
        .findLatestVersionById(id)
        .orElseThrow(
            () ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND, ErrorCode.RISK_RULE_NOT_FOUND.getCode()));
  }

  private void verifyRiskRuleNotExists(RiskRuleDto dto) {
    nameUniquenessService.validateForCreate(
        name ->
            riskRuleRepository.existsByBrandIdAndEnvironmentIdAndFlowActionIdAndName(
                dto.getBrandId(), dto.getEnvironmentId(), dto.getFlowActionId(), name),
        "Risk Rule",
        dto.getName());
  }

  private void validateCustomerCriteria(RiskRuleDto dto) {
    if (RiskType.CUSTOMER.equals(dto.getType())) {
      verifyCriteriaTypeAndValuePresent(dto);
    } else if (RiskType.DEFAULT.equals(dto.getType())) {
      verifyCriteriaTypeAndValueNotPresent(dto);
    }
  }

  private static void verifyCriteriaTypeAndValueNotPresent(RiskRuleDto dto) {
    boolean hasCriteriaFields =
        !Objects.isNull(dto.getCriteriaType())
            || (dto.getCriteriaValue() != null && !dto.getCriteriaValue().isEmpty());

    if (hasCriteriaFields) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "criteriaType and criteriaValue must not be provided when type is DEFAULT");
    }
  }

  private static void verifyCriteriaTypeAndValuePresent(RiskRuleDto dto) {
    if (Objects.isNull(dto.getCriteriaType())) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, ErrorCode.RISK_RULE_CRITERIA_TYPE_REQUIRED.getCode());
    }
    if (dto.getCriteriaValue() == null || dto.getCriteriaValue().isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, ErrorCode.RISK_RULE_CRITERIA_VALUE_REQUIRED.getCode());
    }
  }

  public List<RiskRuleDto> buildEnrichedRiskRuleDtos(List<RiskRule> riskRules) {
    if (CollectionUtils.isEmpty(riskRules)) {
      return Collections.emptyList();
    }

    Map<String, IdNameDto> pspIdNameDtoMap = getPspIdNameDtoMap(riskRules);
    Map<String, IdNameDto> flowActionIdNameDtoMap = getFlowActionIdNameDtoMap(riskRules);
    return buildRiskRuleDtos(riskRules, pspIdNameDtoMap, flowActionIdNameDtoMap);
  }

  public RiskRuleDto buildEnrichedRiskRuleDto(RiskRule riskRule) {
    List<RiskRule> riskRules = List.of(riskRule);
    return buildEnrichedRiskRuleDtos(riskRules).getFirst();
  }

  private Map<String, IdNameDto> getPspIdNameDtoMap(List<RiskRule> riskRules) {
    List<String> pspIds = getAllPspIds(riskRules);

    if (CollectionUtils.isEmpty(pspIds)) {
      return Collections.emptyMap();
    }

    return pspService.getPspIdNameDtoMap(pspIds);
  }

  private Map<String, IdNameDto> getFlowActionIdNameDtoMap(List<RiskRule> riskRules) {
    List<String> allFlowActionIds = getAllFlowActionIds(riskRules);

    if (CollectionUtils.isEmpty(allFlowActionIds)) {
      return Collections.emptyMap();
    }

    return flowActionService.getFlowActionIdNameDtoMap(allFlowActionIds);
  }

  private List<String> getAllPspIds(List<RiskRule> riskRules) {
    return riskRules.stream()
        .map(RiskRule::getRiskRulePsps)
        .filter(Objects::nonNull)
        .flatMap(List::stream)
        .map(RiskRulePsp::getPspId)
        .distinct()
        .collect(Collectors.toList());
  }

  private List<String> getAllFlowActionIds(List<RiskRule> riskRules) {
    return riskRules.stream()
        .map(RiskRule::getFlowActionId)
        .filter(Objects::nonNull)
        .distinct()
        .collect(Collectors.toList());
  }

  private List<RiskRuleDto> buildRiskRuleDtos(
      List<RiskRule> riskRules,
      Map<String, IdNameDto> pspMap,
      Map<String, IdNameDto> flowActionMap) {
    return riskRules.stream()
        .filter(rule -> Objects.nonNull(rule.getRiskRulePsps()))
        .map(
            rule -> {
              List<IdNameDto> psps =
                  rule.getRiskRulePsps().stream()
                      .map(RiskRulePsp::getPspId)
                      .map(pspMap::get)
                      .filter(Objects::nonNull)
                      .collect(Collectors.toList());
              RiskRuleDto dto = riskRuleMapper.toRiskRuleDto(rule);
              dto.setPsps(psps);
              // Add FlowAction name
              IdNameDto flowAction = flowActionMap.get(rule.getFlowActionId());
              if (flowAction != null) {
                dto.setFlowActionName(flowAction.getName());
              }
              return dto;
            })
        .collect(Collectors.toList());
  }
}
