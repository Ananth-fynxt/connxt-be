package connxt.psp.service;

import java.util.List;

import connxt.psp.entity.PspOperation;

public interface PspOperationsService {
  long getCountByPspIdsAndFlowActionIdAndCurrency(
      List<String> pspIds, String flowActionId, String currency);

  boolean validateByPspIdsAndFlowActionIdAndCurrency(
      List<String> pspIds, String flowActionId, String currency);

  PspOperation getPspOperationIfEnabled(String pspId, String flowActionId);

  String fetchFlowDefinitionId(String pspId, String flowActionId);
}
