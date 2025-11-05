package nexxus.cron.dto;

import java.time.LocalDateTime;

import org.jobrunr.jobs.lambdas.JobRequest;

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
public class CronJobRequest {

  private String jobId;
  private String correlationId;
  private Integer attemptNumber;
  private Integer maxRetries;
  private LocalDateTime scheduledAt;
  private String description;
  private JobType jobType;
  private Runnable runnableTask;
  private JobRequest jobRequest;

  public enum JobType {
    RUNNABLE,
    JOB_REQUEST
  }
}
