package justlive.earth.breeze.frost.core.notify;

import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import justlive.earth.breeze.frost.api.model.JobInfo;
import justlive.earth.breeze.frost.core.job.JobSchedule;
import justlive.earth.breeze.frost.core.persistence.JobRepository;

/**
 * 子任务事件通知器
 * 
 * @author wubo
 *
 */
public class ChildrenJobEventNotifier extends AbstractEventNotifier {

  @Autowired
  JobRepository jobRepository;

  @Autowired
  JobSchedule jobSchedule;

  @Override
  protected boolean shouldNotify(Event event) {
    JobInfo jobInfo = jobRepository.findJobInfoById(event.getData().getJobId());
    return jobInfo != null && Objects.equals(event.getType(), Event.TYPE.EXECUTE_SUCCESS.name())
        && jobInfo.getChildJobIds() != null && jobInfo.getChildJobIds().length > 0;
  }

  @Override
  protected void doNotify(Event event) {
    JobInfo jobInfo = jobRepository.findJobInfoById(event.getData().getJobId());
    if (jobInfo.getChildJobIds() != null) {
      for (String jobId : jobInfo.getChildJobIds()) {
        jobSchedule.triggerChildJob(jobId, event.getData().getLoggerId());
      }
    }
  }

}
