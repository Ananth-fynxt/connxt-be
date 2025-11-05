package connxt.flowaction.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import connxt.shared.validators.ValidJson;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowActionDto {
  private String id;

  @NotBlank(message = "Name is required")
  private String name;

  @NotEmpty(message = "Steps cannot be empty")
  private List<String> steps;

  private String flowTypeId;

  @NotBlank(message = "Input schema is required")
  @ValidJson
  private String inputSchema;

  @NotBlank(message = "Output schema is required")
  @ValidJson
  private String outputSchema;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedAt;

  private String createdBy;

  private String updatedBy;
}
