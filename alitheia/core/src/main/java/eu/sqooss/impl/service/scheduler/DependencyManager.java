package eu.sqooss.impl.service.scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eu.sqooss.service.scheduler.Job;

class DependencyManager {

	private HashMap<Job, List<Job>> dependencies;

	public DependencyManager() {
		this.dependencies = new HashMap<Job, List<Job>>();
	}

	public void add(Job parent, Job child) {
		synchronized (dependencies) {

			List<Job> existingDependencies = this.dependencies.get(parent);
			if (existingDependencies == null) {
				existingDependencies = new ArrayList<Job>();
			}
			existingDependencies.add(child);
			this.dependencies.put(parent, existingDependencies);
		}
	}

	public void remove(Job parent, Job child) {
		synchronized (dependencies) {

			List<Job> existingDependencies = this.dependencies.get(parent);
			if (existingDependencies == null) {
				existingDependencies = new ArrayList<Job>();
			}
			existingDependencies.remove(child);
			this.dependencies.put(parent, existingDependencies);
		}
	}
	
	public void remove(Job parent) {
		synchronized (dependencies) {
			this.dependencies.remove(parent);
		}
	}
	
	public boolean canExecute(Job j) {
		List<Job> dependencies = this.dependencies.get(j);
		return dependencies == null || dependencies.isEmpty(); 
	}
}
