package connxt.psp.service;

import connxt.psp.entity.PspOperation;

public interface PspOperationsService {
  PspOperation getPspOperationIfEnabled(String pspId, String flowActionId);

  String fetchFlowDefinitionId(String pspId, String flowActionId);
}
