package connxt.webhook.dto;

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
public class WebhookStats {

  private String webhookId;
  private long totalAttempts;
  private long successfulAttempts;
  private long failedAttempts;
  private String successRate;
  private Long averageExecutionTimeMs;
  private Long lastExecutionTimeMs;
  private String lastExecutionStatus;
}
