package nexxus.routingrule.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

import nexxus.psp.dto.IdNameDto;
import nexxus.psp.service.PspService;
import nexxus.routingrule.dto.RoutingRuleDto;
import nexxus.routingrule.dto.RoutingRulePspDto;
import nexxus.routingrule.dto.UpdateRoutingRuleDto;
import nexxus.routingrule.entity.RoutingRule;
import nexxus.routingrule.entity.RoutingRulePsp;
import nexxus.routingrule.repository.RoutingRulePspRepository;
import nexxus.routingrule.repository.RoutingRuleRepository;
import nexxus.routingrule.service.RoutingRuleService;
import nexxus.routingrule.service.mappers.RoutingRuleMapper;
import nexxus.shared.constants.ErrorCode;
import nexxus.shared.service.NameUniquenessService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoutingRuleServiceImpl implements RoutingRuleService {

  private final RoutingRuleRepository routingRuleRepository;
  private final RoutingRulePspRepository pspRepository;
  private final RoutingRuleMapper routingRuleMapper;
  private final PspService pspService;
  private final NameUniquenessService nameUniquenessService;

  @Override
  @Transactional
  public RoutingRuleDto create(RoutingRuleDto routingRuleDto) {
    nameUniquenessService.validateForCreate(
        name ->
            routingRuleRepository.existsByBrandIdAndEnvironmentIdAndName(
                routingRuleDto.getBrandId(), routingRuleDto.getEnvironmentId(), name),
        "Routing Rule",
        routingRuleDto.getName());

    RoutingRule routingRule = routingRuleMapper.toRoutingRule(routingRuleDto, 1);
    RoutingRule savedRoutingRule = routingRuleRepository.save(routingRule);
    createPsps(
        routingRuleDto.getPsps(),
        savedRoutingRule.getRoutingRuleId().getId(),
        savedRoutingRule.getRoutingRuleId().getVersion());
    return buildEnrichedRoutingRuleDto(savedRoutingRule);
  }

  @Override
  @Transactional
  public RoutingRuleDto update(String id, UpdateRoutingRuleDto updateRoutingRuleDto) {
    RoutingRule existingRoutingRule = getRoutingRuleIfExists(id);

    // Validate name uniqueness for update (exclude current routing rule)
    nameUniquenessService.validateForUpdateWithFlowContext(
        existingRoutingRule.getName(),
        updateRoutingRuleDto.getName(),
        existingRoutingRule.getBrandId(),
        existingRoutingRule.getEnvironmentId(),
        null,
        (brandId, environmentId, flowActionId, name) ->
            routingRuleRepository.existsByBrandIdAndEnvironmentIdAndNameAndIdNot(
                brandId, environmentId, name, id),
        "Routing Rule");

    // Create a new RoutingRule with incremented version
    RoutingRule updatedRoutingRule =
        routingRuleMapper.copyRoutingRuleWithIncrementedVersion(existingRoutingRule);
    routingRuleMapper.toUpdateRoutingRule(updateRoutingRuleDto, updatedRoutingRule);
    RoutingRule savedRoutingRule = routingRuleRepository.save(updatedRoutingRule);
    createPsps(
        updateRoutingRuleDto.getPsps(),
        savedRoutingRule.getRoutingRuleId().getId(),
        savedRoutingRule.getRoutingRuleId().getVersion());
    return buildEnrichedRoutingRuleDto(savedRoutingRule);
  }

  private RoutingRule getRoutingRuleIfExists(String id) {
    Optional<RoutingRule> existingRoutingRuleOpt =
        routingRuleRepository.findTopByRoutingRuleIdIdOrderByRoutingRuleIdVersionDesc(id);
    if (existingRoutingRuleOpt.isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          ErrorCode.ROUTING_RULE_NOT_FOUND.getCode() + " Routing rule not found with ID: " + id);
    }
    return existingRoutingRuleOpt.get();
  }

  @Override
  @Transactional
  public void delete(String id) {
    // Delete should not happen here. Do soft delete.
    Optional<RoutingRule> routingRuleOpt =
        routingRuleRepository.findTopByRoutingRuleIdIdOrderByRoutingRuleIdVersionDesc(id);
    if (routingRuleOpt.isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          ErrorCode.ROUTING_RULE_NOT_FOUND.getCode() + " Routing rule not found with ID: " + id);
    }

    RoutingRule routingRule = routingRuleOpt.get();

    Long count =
        routingRuleRepository.countByBrandIdAndEnvironmentId(
            routingRule.getBrandId(), routingRule.getEnvironmentId());
    if (count <= 1) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, ErrorCode.ROUTING_LAST_RULE_DELETE_FORBIDDEN.getCode());
    }

    // Delete PSPs first
    pspRepository.deleteAllByRoutingRuleId(id);
    routingRuleRepository.deleteByRoutingRuleIdId(id);
  }

  @Override
  public RoutingRuleDto getById(String id) {
    RoutingRule routingRule = getRoutingRuleIfExists(id);
    return buildEnrichedRoutingRuleDto(routingRule);
  }

  @Override
  public List<RoutingRuleDto> readAllByBrandAndEnvironment(String brandId, String environmentId) {
    List<RoutingRule> routingRules =
        routingRuleRepository.findByBrandIdAndEnvironmentId(brandId, environmentId);
    return buildEnrichedRoutingRuleDtos(routingRules);
  }

  private void createPsps(List<RoutingRulePspDto> psps, String routingRuleId, Integer version) {
    List<RoutingRulePsp> pspList = new ArrayList<>();
    for (RoutingRulePspDto pspDto : psps) {
      RoutingRulePsp psp = routingRuleMapper.toRoutingRulePsp(pspDto, routingRuleId, version);
      pspList.add(psp);
    }
    pspRepository.saveAll(pspList);
  }

  @Override
  public RoutingRuleDto findActiveRoutingRuleById(String routingRuleId) {
    RoutingRule routingRule = routingRuleRepository.findActiveRoutingRuleById(routingRuleId);
    return buildEnrichedRoutingRuleDto(routingRule);
  }

  public List<RoutingRulePsp> findRoutingRulePspsByIdAndVersion(
      String routingRuleId, Integer routingRuleVersion) {
    return pspRepository.findByRoutingRuleIdAndRoutingRuleVersion(
        routingRuleId, routingRuleVersion);
  }

  @Override
  public List<RoutingRuleDto> findEnabledRoutingRulesByBrandAndEnvironment(
      String brandId, String environmentId) {
    List<RoutingRule> routingRules =
        routingRuleRepository.findEnabledRoutingRulesByBrandAndEnvironment(brandId, environmentId);
    return buildEnrichedRoutingRuleDtos(routingRules);
  }

  public List<RoutingRuleDto> buildEnrichedRoutingRuleDtos(List<RoutingRule> routingRules) {
    if (CollectionUtils.isEmpty(routingRules)) {
      return Collections.emptyList();
    }

    Map<String, IdNameDto> pspIdNameDtoMap = getPspIdNameDtoMap(routingRules);

    return buildRoutingRuleDtos(routingRules, pspIdNameDtoMap);
  }

  public RoutingRuleDto buildEnrichedRoutingRuleDto(RoutingRule routingRule) {
    List<RoutingRule> routingRules = List.of(routingRule);
    return buildEnrichedRoutingRuleDtos(routingRules).getFirst();
  }

  private Map<String, IdNameDto> getPspIdNameDtoMap(List<RoutingRule> routingRules) {
    List<String> pspIds = getAllPspIds(routingRules);

    if (CollectionUtils.isEmpty(pspIds)) {
      return Collections.emptyMap();
    }

    return pspService.getPspIdNameDtoMap(pspIds);
  }

  private List<String> getAllPspIds(List<RoutingRule> routingRules) {
    return routingRules.stream()
        .map(
            routingRule ->
                pspRepository.findByRoutingRuleIdAndRoutingRuleVersion(
                    routingRule.getRoutingRuleId().getId(),
                    routingRule.getRoutingRuleId().getVersion()))
        .filter(psps -> !psps.isEmpty())
        .flatMap(List::stream)
        .map(RoutingRulePsp::getPspId)
        .distinct()
        .collect(Collectors.toList());
  }

  private List<RoutingRuleDto> buildRoutingRuleDtos(
      List<RoutingRule> routingRules, Map<String, IdNameDto> pspMap) {
    return routingRules.stream()
        .map(
            routingRule -> {
              RoutingRuleDto dto = routingRuleMapper.toRoutingRuleDto(routingRule);
              appendPsps(routingRule, dto, pspMap);
              return dto;
            })
        .collect(Collectors.toList());
  }

  private void appendPsps(
      RoutingRule routingRule, RoutingRuleDto responseDto, Map<String, IdNameDto> pspMap) {
    List<RoutingRulePsp> routingRulePsps =
        pspRepository.findByRoutingRuleIdAndRoutingRuleVersion(
            routingRule.getRoutingRuleId().getId(), routingRule.getRoutingRuleId().getVersion());

    if (!routingRulePsps.isEmpty()) {
      List<RoutingRulePspDto> enrichedPsps =
          routingRulePsps.stream()
              .map(
                  psp -> {
                    RoutingRulePspDto dto = routingRuleMapper.toRoutingRulePspDto(psp);
                    if (dto.getPspId() != null) {
                      IdNameDto pspInfo = pspMap.get(dto.getPspId());
                      if (pspInfo != null) {
                        dto.setPspName(pspInfo.getName());
                      }
                    }
                    return dto;
                  })
              .collect(Collectors.toList());

      responseDto.setPsps(enrichedPsps);
    } else {
      responseDto.setPsps(Collections.emptyList());
    }
  }
}
