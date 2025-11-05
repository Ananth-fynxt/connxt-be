package connxt.webhook.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import connxt.shared.constants.WebhookExecutionStatus;
import connxt.shared.db.AuditingEntity;
import connxt.shared.db.PostgreSQLEnumType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "webhook_logs")
@Builder
public class WebhookLog extends AuditingEntity {

  @WebhookLogId
  @Id
  @Column(name = "id")
  private String id;

  @Column(name = "webhook_id")
  private String webhookId;

  @Column(name = "response_status")
  private Integer responseStatus;

  @Column(name = "is_success")
  private Boolean isSuccess;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "request_payload", columnDefinition = "JSONB")
  private String requestPayload;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "response_payload", columnDefinition = "JSONB")
  private String responsePayload;

  @Column(name = "error_message")
  private String errorMessage;

  @Column(name = "execution_time_ms")
  private Long executionTimeMs;

  @Column(name = "attempt_number")
  @Builder.Default
  private Integer attemptNumber = 1;

  @Type(
      value = PostgreSQLEnumType.class,
      parameters =
          @org.hibernate.annotations.Parameter(
              name = "enumClass",
              value = "connxt.shared.constants.WebhookExecutionStatus"))
  @Enumerated(EnumType.STRING)
  @Column(name = "execution_status", columnDefinition = "webhook_execution_status")
  @Builder.Default
  private WebhookExecutionStatus executionStatus = WebhookExecutionStatus.PENDING;

  @Column(name = "scheduled_at")
  private LocalDateTime scheduledAt;

  @Column(name = "executed_at")
  private LocalDateTime executedAt;

  @Column(name = "completed_at")
  private LocalDateTime completedAt;

  @Column(name = "retry_after")
  private LocalDateTime retryAfter;

  @Column(name = "job_id")
  private String jobId;

  @Column(name = "correlation_id")
  private String correlationId;

  @Column(name = "user_agent")
  private String userAgent;

  @Column(name = "content_type")
  private String contentType;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "response_headers", columnDefinition = "JSONB")
  private String responseHeaders;
}
