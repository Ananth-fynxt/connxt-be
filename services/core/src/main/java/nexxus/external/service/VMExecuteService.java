package nexxus.external.service;

import nexxus.denovm.dto.DenoVMResult;
import nexxus.external.dto.VmExecutionDto;

public interface VMExecuteService {
  DenoVMResult executeVmRequest(VmExecutionDto requestDto);
}
