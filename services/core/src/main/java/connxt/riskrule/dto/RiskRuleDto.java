package connxt.riskrule.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import connxt.psp.dto.IdNameDto;
import connxt.riskrule.dto.validation.ValidCustomerCriteria;
import connxt.shared.constants.*;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ValidCustomerCriteria
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class RiskRuleDto {
  private String id;

  private Integer version;

  @NotNull(message = "Type is required")
  private RiskType type;

  @NotNull(message = "Action is required")
  private RiskAction action;

  @NotBlank(message = "Currency is required")
  private String currency;

  @NotNull(message = "Duration is required")
  private RiskDuration duration;

  @NotBlank(message = "Name is required")
  private String name;

  private RiskCustomerCriteriaType criteriaType;

  private List<String> criteriaValue;

  @NotNull(message = "Max amount is required")
  @DecimalMin(value = "0.0", inclusive = false, message = "Max amount must be greater than 0")
  private BigDecimal maxAmount;

  @NotBlank(message = "Brand ID is required")
  private String brandId;

  @NotBlank(message = "Environment ID is required")
  private String environmentId;

  @NotBlank(message = "Flow Action ID is required")
  private String flowActionId;

  private String flowActionName;

  @Builder.Default private Status status = Status.ENABLED;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedAt;

  private String createdBy;

  private String updatedBy;

  private List<IdNameDto> psps;
}
