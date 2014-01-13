package eu.sqooss.impl.service.scheduler;

import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.Scheduler;

public class OneShotWorker extends BaseWorker {

	/**
	 * Creates a new OneShotWorker 
	 * @param s
	 */
	public OneShotWorker(Scheduler s) {
		super(s);
	}

	/**
	 * Only one job is executed by this worker
	 */
	@Override
	public void run() {
		  try {
          	Job job = m_scheduler.takeJob();
          	// get a job from the scheduler
              super.executeJob(job);
          } catch (InterruptedException e) {
              // we were interrupted so this workes finishes
          }
	}
}
