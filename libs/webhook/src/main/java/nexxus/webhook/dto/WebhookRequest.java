package nexxus.webhook.dto;

import java.time.LocalDateTime;
import java.util.Map;

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
public class WebhookRequest {

  private String webhookId;
  private String url;
  private String payload;
  private String correlationId;
  private Integer attemptNumber;
  private Integer maxRetries;
  private Map<String, String> headers;
  private Integer timeoutMs;
  private LocalDateTime scheduledAt;
  private String jobId;
}
