package connxt.psp.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import connxt.fee.dto.FeeDto;
import connxt.riskrule.dto.RiskRuleDto;
import connxt.shared.constants.Status;
import connxt.transactionlimit.dto.TransactionLimitDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PspSummaryDto {

  private String id;
  private String name;
  private String description;
  private String logo;
  private Status status;
  private String brandId;
  private String environmentId;
  private String flowTargetId;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedAt;

  private String createdBy;
  private String updatedBy;
  private List<RiskRuleDto> riskRules;
  private List<FeeDto> feeRules;
  private List<TransactionLimitDto> transactionLimits;
}
