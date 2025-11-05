package connxt.request.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import connxt.fee.dto.FeeDto;
import connxt.fee.service.FeeService;
import connxt.psp.service.filter.fee.FeeCalculationService;
import connxt.psp.service.resolution.PspResolutionResult;
import connxt.psp.service.resolution.PspResolutionService;
import connxt.request.dto.RequestInputDto;
import connxt.request.dto.RequestOutputDto;
import connxt.request.entity.Request;
import connxt.request.entity.RequestFee;
import connxt.request.entity.RequestPsp;
import connxt.request.entity.RequestRiskRule;
import connxt.request.entity.RequestTransactionLimit;
import connxt.request.repository.RequestFeeRepository;
import connxt.request.repository.RequestPspRepository;
import connxt.request.repository.RequestRepository;
import connxt.request.repository.RequestRiskRuleRepository;
import connxt.request.repository.RequestTransactionLimitRepository;
import connxt.request.service.RequestService;
import connxt.request.service.mappers.RequestFeeMapper;
import connxt.request.service.mappers.RequestMapper;
import connxt.request.service.mappers.RequestPspMapper;
import connxt.request.service.mappers.RequestRiskRuleMapper;
import connxt.request.service.mappers.RequestTransactionLimitMapper;
import connxt.riskrule.dto.RiskRuleDto;
import connxt.transactionlimit.dto.TransactionLimitDto;

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
            .map(connxt.psp.entity.Psp::getId)
            .collect(Collectors.toList());

    return feeService.readLatestEnabledFeeRulesByCriteria(
        pspIds,
        request.getBrandId(),
        request.getEnvironmentId(),
        request.getActionId(),
        request.getCurrency(),
        connxt.shared.constants.Status.ENABLED);
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
