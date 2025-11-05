package connxt.webhook.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import connxt.shared.constants.Status;
import connxt.shared.constants.WebhookStatusType;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookDto {
  private String id;

  @NotNull(message = "Webhook status type is required")
  private WebhookStatusType statusType;

  @NotBlank(message = "Webhook URL is required")
  @Pattern(regexp = "^https?://.*", message = "URL must start with http:// or https://")
  private String url;

  @Min(value = 0, message = "Retry count must be at least 0")
  @Max(value = 10, message = "Retry count must be at most 10")
  @Builder.Default
  private Integer retry = 3;

  @NotBlank(message = "Brand ID is required")
  private String brandId;

  @NotBlank(message = "Environment ID is required")
  private String environmentId;

  @Builder.Default private Status status = Status.ENABLED;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedAt;

  private String createdBy;

  private String updatedBy;
}
