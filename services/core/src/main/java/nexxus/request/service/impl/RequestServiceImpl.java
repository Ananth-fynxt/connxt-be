package nexxus.request.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nexxus.fee.dto.FeeDto;
import nexxus.fee.service.FeeService;
import nexxus.psp.service.filter.fee.FeeCalculationService;
import nexxus.psp.service.resolution.PspResolutionResult;
import nexxus.psp.service.resolution.PspResolutionService;
import nexxus.request.dto.RequestInputDto;
import nexxus.request.dto.RequestOutputDto;
import nexxus.request.entity.Request;
import nexxus.request.entity.RequestFee;
import nexxus.request.entity.RequestPsp;
import nexxus.request.entity.RequestRiskRule;
import nexxus.request.entity.RequestTransactionLimit;
import nexxus.request.repository.RequestFeeRepository;
import nexxus.request.repository.RequestPspRepository;
import nexxus.request.repository.RequestRepository;
import nexxus.request.repository.RequestRiskRuleRepository;
import nexxus.request.repository.RequestTransactionLimitRepository;
import nexxus.request.service.RequestService;
import nexxus.request.service.mappers.RequestFeeMapper;
import nexxus.request.service.mappers.RequestMapper;
import nexxus.request.service.mappers.RequestPspMapper;
import nexxus.request.service.mappers.RequestRiskRuleMapper;
import nexxus.request.service.mappers.RequestTransactionLimitMapper;
import nexxus.riskrule.dto.RiskRuleDto;
import nexxus.transactionlimit.dto.TransactionLimitDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

  private final RequestRepository requestRepository;
  private final RequestPspRepository requestPspRepository;
  private final RequestFeeRepository requestFeeRepository;
  private final RequestRiskRuleRepository requestRiskRuleRepository;
  private final RequestTransactionLimitRepository requestTransactionLimitRepository;
  private final RequestMapper requestMapper;
  private final RequestPspMapper requestPspMapper;
  private final RequestFeeMapper requestFeeMapper;
  private final RequestRiskRuleMapper requestRiskRuleMapper;
  private final RequestTransactionLimitMapper requestTransactionLimitMapper;
  private final PspResolutionService pspResolutionService;
  private final FeeCalculationService feeCalculationService;
  private final FeeService feeService;

  @Override
  @Transactional
  public RequestOutputDto fetchPsp(@Valid RequestInputDto requestInputDto) {
    // Step 1: Create and save Request entity using mapper
    Request request = requestMapper.toRequest(requestInputDto);
    Request savedRequest = requestRepository.save(request);

    // Step 2: Resolve PSPs (fetch + filter - WITHOUT fee calculation for performance)
    PspResolutionResult resolutionResult = pspResolutionService.resolvePsps(requestInputDto);

    // Step 3: Load fee rules ONLY ONCE for the final filtered PSPs (performance optimization)
    List<FeeDto> feeRules = loadFeeRulesForFinalPsps(requestInputDto, resolutionResult);

    // Step 4: Calculate fees ONLY ONCE on final PSP list (minimal effort)
    List<RequestOutputDto.PspInfo> pspsWithFees =
        feeCalculationService.calculateFeesForPsps(
            resolutionResult.getFilteredPsps(),
            feeRules,
            requestInputDto,
            resolutionResult.isRequiresCurrencyConversion());

    // Step 5: Save RequestPsp entities using mapper and create associations
    createPsps(savedRequest, pspsWithFees);

    // Step 6: Save related entities (fees, risk rules, transaction limits)
    saveRelatedEntities(
        savedRequest.getId(),
        resolutionResult.getRiskRules(),
        feeRules,
        resolutionResult.getTransactionLimits());

    // Step 7: Build response - return PSPs data with requestId
    RequestOutputDto responseDto =
        RequestOutputDto.builder().requestId(savedRequest.getId()).psps(pspsWithFees).build();

    return responseDto;
  }

  /** Load fee rules ONLY for the final filtered PSPs */
  private List<FeeDto> loadFeeRulesForFinalPsps(
      RequestInputDto request, PspResolutionResult resolutionResult) {
    if (resolutionResult.getFilteredPsps().isEmpty()) {
      return List.of();
    }

    List<String> pspIds =
        resolutionResult.getFilteredPsps().stream()
            .map(nexxus.psp.entity.Psp::getId)
            .collect(Collectors.toList());

    return feeService.readLatestEnabledFeeRulesByCriteria(
        pspIds,
        request.getBrandId(),
        request.getEnvironmentId(),
        request.getActionId(),
        request.getCurrency(),
        nexxus.shared.constants.Status.ENABLED);
  }

  private void createPsps(Request request, List<RequestOutputDto.PspInfo> pspInfos) {
    if (pspInfos != null && !pspInfos.isEmpty()) {
      List<RequestPsp> requestPsps =
          pspInfos.stream()
              .map(pspInfo -> requestPspMapper.toRequestPsp(request.getId(), pspInfo))
              .collect(Collectors.toList());
      requestPspRepository.saveAll(requestPsps);
    }
  }

  private void saveRelatedEntities(
      String requestId,
      List<RiskRuleDto> riskRules,
      List<FeeDto> feeRules,
      List<TransactionLimitDto> transactionLimits) {

    // Save Risk Rules
    if (riskRules != null && !riskRules.isEmpty()) {
      List<RequestRiskRule> requestRiskRules =
          riskRules.stream()
              .map(riskRule -> requestRiskRuleMapper.toRequestRiskRule(requestId, riskRule))
              .collect(Collectors.toList());
      requestRiskRuleRepository.saveAll(requestRiskRules);
    }

    // Save Fee Rules
    if (feeRules != null && !feeRules.isEmpty()) {
      List<RequestFee> requestFees =
          feeRules.stream()
              .map(feeRule -> requestFeeMapper.toRequestFee(requestId, feeRule))
              .collect(Collectors.toList());
      requestFeeRepository.saveAll(requestFees);
    }

    // Save Transaction Limits
    if (transactionLimits != null && !transactionLimits.isEmpty()) {
      List<RequestTransactionLimit> requestTransactionLimits =
          transactionLimits.stream()
              .map(
                  transactionLimit ->
                      requestTransactionLimitMapper.toRequestTransactionLimit(
                          requestId, transactionLimit))
              .collect(Collectors.toList());
      requestTransactionLimitRepository.saveAll(requestTransactionLimits);
    }
  }
}
