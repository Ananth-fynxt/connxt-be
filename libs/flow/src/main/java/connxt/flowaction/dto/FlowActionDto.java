package connxt.flowaction.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import connxt.shared.validators.ValidJson;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

  @NotBlank(message = "Flow type ID is required")
  private String flowTypeId;

  @JsonSetter(nulls = Nulls.SKIP)
  @Builder.Default
  @ValidJson
  private String inputSchema = "{}";

  @JsonSetter(nulls = Nulls.SKIP)
  @Builder.Default
  @ValidJson
  private String outputSchema = "{}";

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedAt;

  private String createdBy;

  private String updatedBy;
}
