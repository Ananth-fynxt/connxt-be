package connxt.webhook.dto;

import java.time.LocalDateTime;
import java.util.Map;

import connxt.shared.constants.WebhookExecutionStatus;

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
public class WebhookResponse {

  private String webhookId;
  private String correlationId;
  private WebhookExecutionStatus executionStatus;
  private Integer responseStatus;
  private String responsePayload;
  private String errorMessage;
  private Long executionTimeMs;
  private Integer attemptNumber;
  private LocalDateTime scheduledAt;
  private LocalDateTime executedAt;
  private LocalDateTime completedAt;
  private String jobId;
  private Map<String, String> responseHeaders;
  private Boolean isSuccess;
}
