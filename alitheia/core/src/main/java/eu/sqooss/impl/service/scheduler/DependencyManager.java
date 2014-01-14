package eu.sqooss.impl.service.scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import eu.sqooss.service.scheduler.Job;

class DependencyManager {

	private ConcurrentHashMap<Job, List<Job>> dependencies;

	public DependencyManager() {
		this.dependencies = new ConcurrentHashMap<Job, List<Job>>();
	}

	public void add(Job parent, Job child) {
		List<Job> existingDependencies = this.dependencies.get(parent);
		if (existingDependencies == null) {
			existingDependencies = new ArrayList<Job>();
		}
		existingDependencies.add(child);
		this.dependencies.put(parent, existingDependencies);
	}

	public void remove(Job parent, Job child) {
		List<Job> existingDependencies = this.dependencies.get(parent);
		if (existingDependencies == null) {
			existingDependencies = new ArrayList<Job>();
		}
		existingDependencies.remove(child);
		this.dependencies.put(parent, existingDependencies);
	}

	public void remove(Job parent) {
		this.dependencies.remove(parent);
	}

	public boolean canExecute(Job j) {
		List<Job> deps = this.dependencies.get(j);
		if (deps == null) {
			return true;
		} else {
			for (Job job : deps) {
				if (job.state() != Job.State.Finished) {
					return false;
				}
			}
			return true;
		}
	}
}
