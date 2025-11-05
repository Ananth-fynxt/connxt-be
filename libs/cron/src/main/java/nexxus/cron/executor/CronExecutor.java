package nexxus.cron.executor;

import nexxus.cron.dto.CronJobRequest;
import nexxus.cron.dto.CronJobResponse;

public interface CronExecutor {

  CronJobResponse execute(CronJobRequest request);
}
