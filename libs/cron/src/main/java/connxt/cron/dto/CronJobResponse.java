package connxt.cron.dto;

import java.time.LocalDateTime;

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
public class CronJobResponse {

  private String jobId;
  private String correlationId;
  private String executionStatus;
  private Object result;
  private String errorMessage;
  private Long executionTimeMs;
  private Integer attemptNumber;
  private LocalDateTime scheduledAt;
  private LocalDateTime executedAt;
  private LocalDateTime completedAt;
  private CronJobRequest.JobType jobType;
  private String description;
  private Boolean isSuccess;
}
