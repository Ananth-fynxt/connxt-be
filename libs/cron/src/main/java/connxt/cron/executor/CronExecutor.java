package connxt.cron.executor;

import connxt.cron.dto.CronJobRequest;
import connxt.cron.dto.CronJobResponse;

public interface CronExecutor {

  CronJobResponse execute(CronJobRequest request);
}
