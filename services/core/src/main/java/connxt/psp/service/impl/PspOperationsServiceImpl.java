package connxt.psp.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import connxt.psp.entity.PspOperation;
import connxt.psp.repository.PspOperationRepository;
import connxt.psp.service.PspOperationsService;
import connxt.psp.service.mappers.PspMapper;
import connxt.shared.constants.ErrorCode;
import connxt.shared.constants.Status;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PspOperationsServiceImpl implements PspOperationsService {

  private final PspOperationRepository pspOperationRepository;
  private final PspMapper pspMapper;

  public boolean validateByPspIdsAndFlowActionIdAndCurrency(
      List<String> pspIds, String flowActionId, String currency) {
    long recordCount = getCountByPspIdsAndFlowActionIdAndCurrency(pspIds, flowActionId, currency);
    return recordCount == pspIds.size();
  }

  public long getCountByPspIdsAndFlowActionIdAndCurrency(
      List<String> pspIds, String flowActionId, String currency) {
    return pspOperationRepository.countByPspIdsAndFlowActionIdAndCurrency(
        pspIds, flowActionId, currency);
  }

  @Override
  public PspOperation getPspOperationIfEnabled(String pspId, String flowActionId) {
    PspOperation pspOperation =
        pspOperationRepository.findByPspIdAndFlowActionId(pspId, flowActionId);
    if (pspOperation.getStatus() != Status.ENABLED) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, ErrorCode.PSP_OPERATION_STATUS_INVALID.getCode());
    }
    return pspOperation;
  }

  @Override
  public String fetchFlowDefinitionId(String pspId, String flowActionId) {
    log.debug(
        "Fetching flow definition ID for pspId: {} and flowActionId: {}", pspId, flowActionId);

    PspOperation pspOperation =
        pspOperationRepository.findByPspIdAndFlowActionId(pspId, flowActionId);
    String flowDefinitionId = pspMapper.extractFlowDefinitionId(pspOperation);

    log.debug(
        "Found flow definition ID: {} for pspId: {} and flowActionId: {}",
        flowDefinitionId,
        pspId,
        flowActionId);
    return flowDefinitionId;
  }
}
