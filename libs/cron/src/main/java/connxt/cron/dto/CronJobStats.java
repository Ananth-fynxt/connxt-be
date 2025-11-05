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
public class CronJobStats {

  private String jobId;
  private String jobType;
  private Integer totalExecutions;
  private Integer successfulExecutions;
  private Integer failedExecutions;
  private Long averageExecutionTimeMs;
  private LocalDateTime lastExecutedAt;
  private LocalDateTime lastSuccessfulExecution;
  private LocalDateTime lastFailedExecution;
  private String lastErrorMessage;
  private Integer currentRetryCount;
  private Boolean isActive;
}
