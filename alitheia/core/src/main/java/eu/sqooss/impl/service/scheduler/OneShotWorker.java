package eu.sqooss.impl.service.scheduler;

import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.scheduler.SchedulerException;

public class OneShotWorker extends BaseWorker {

	private Job job;

	/**
	 * Creates a new OneShotWorker
	 * 
	 * @param s
	 */
	public OneShotWorker(Scheduler s) {
		super(s);
	}

	public OneShotWorker(Scheduler s, Job job) {
		super(s);
		this.job = job;
	}

	/**
	 * Only one job is executed by this worker
	 */
	@Override
	public void run() {
		try {
			if (this.job == null) {
				this.job = m_scheduler.takeJob();
			} else {
				this.job = m_scheduler.takeJob(this.job);
			}

			// get a job from the scheduler
			super.executeJob(this.job);
		} catch (InterruptedException e) {
			// we were interrupted so this workes finishes
		} catch (SchedulerException e) {
			//No valid job was received so this worker is killed.
			this.m_scheduler.deallocateFromThreadpool(this);
			Thread.currentThread().interrupt();
		}
	}
}
