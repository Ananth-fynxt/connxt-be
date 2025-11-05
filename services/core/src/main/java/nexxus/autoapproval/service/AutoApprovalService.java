package nexxus.autoapproval.service;

import java.math.BigDecimal;
import java.util.List;

import nexxus.autoapproval.dto.AutoApprovalDto;

public interface AutoApprovalService {

  AutoApprovalDto create(AutoApprovalDto autoApprovalDto);

  AutoApprovalDto readLatest(String id);

  List<AutoApprovalDto> readByBrandAndEnvironment(String brandId, String environmentId);

  List<AutoApprovalDto> readByPspId(String pspId);

  AutoApprovalDto update(String id, AutoApprovalDto autoApprovalDto);

  void delete(String id);

  BigDecimal getMaxAmountForFlowAction(String flowActionId);
}
