package nexxus.transactionlimit.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import nexxus.psp.dto.IdNameDto;
import nexxus.shared.constants.Status;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionLimitDto {
  private String id;

  private Integer version;

  @NotBlank(message = "Transaction limit name is required")
  private String name;

  @NotBlank(message = "Brand ID is required")
  private String brandId;

  @NotBlank(message = "Environment ID is required")
  private String environmentId;

  @NotBlank(message = "Currency is required")
  private String currency;

  @NotEmpty(message = "At least one country is required")
  private List<String> countries;

  @NotEmpty(message = "At least one customer tag is required")
  private List<String> customerTags;

  @Builder.Default private Status status = Status.ENABLED;

  @NotEmpty(message = "At least one PSP action is required")
  @Valid
  private List<TransactionLimitPspActionDto> pspActions;

  @NotEmpty(message = "At least one PSP is required")
  private List<IdNameDto> psps;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedAt;

  private String createdBy;

  private String updatedBy;
}
