package connxt.external.service;

import connxt.denovm.dto.DenoVMResult;
import connxt.external.dto.VmExecutionDto;

public interface VMExecuteService {
  DenoVMResult executeVmRequest(VmExecutionDto requestDto);
}
