package connxt.flowtarget.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import connxt.shared.constants.Status;
import connxt.shared.validators.ValidJson;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowTargetDto {
  private String id;

  @NotBlank(message = "Flow target name is required")
  private String name;

  private String logo;

  @Builder.Default private Status status = Status.ENABLED;

  @JsonSetter(nulls = Nulls.SKIP)
  @Builder.Default
  @ValidJson
  private String credentialSchema = "{}";

  @JsonSetter(nulls = Nulls.SKIP)
  @Builder.Default
  @ValidJson
  private String inputSchema = "{}";

  @NotBlank(message = "Flow type ID is required")
  private String flowTypeId;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedAt;

  private String createdBy;

  private String updatedBy;

  @JsonProperty("supportedActions")
  private List<SupportedActionInfo> supportedActions;

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class SupportedActionInfo {
    @JsonProperty("flowDefinitionId")
    private String id;

    private String flowActionId;

    @JsonProperty("flowActionName")
    private String flowActionName;
  }
}
