package connxt.flowdefinition.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowDefinitionDto {
  private String id;

  @NotBlank(message = "Flow action ID is required")
  private String flowActionId;

  @NotBlank(message = "Flow target ID is required")
  private String flowTargetId;

  private String description;

  @NotBlank(message = "Code is required")
  private String code;

  private JsonNode flowConfiguration;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedAt;

  private String createdBy;

  private String updatedBy;
}
