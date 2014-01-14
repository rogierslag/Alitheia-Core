package eu.sqooss.impl.service.scheduler;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.SchedulerException;
import eu.sqooss.service.scheduler.Job.State;

public class DependencyManager {

	private static DependencyManager instance;
	private ConcurrentHashMap<Job, List<Job>> dependencies;
	
	/** 
	 * A private constructor, the {@link DependencyManager} should be initialized 
	 * with {@link DependencyManager#getInstance()}
	 */
	private DependencyManager() {  
		this.dependencies = new ConcurrentHashMap<Job, List<Job>>();
	}
	
	/**
	 * Get the current {@link DependencyManager} or create a new one
	 * @return the current {@link DependencyManager}
	 */
	public static DependencyManager getInstance(){
		if(instance == null){
			instance = new DependencyManager();
		}
		return instance;
	}
	/**
	 * Get the current {@link DependencyManager} or create a new one
	 * This method can be used to force a clean {@link DependencyManager}
	 * @param force
	 * @return the current {@link DependencyManager} or a new one if forced
	 */
	public static DependencyManager getInstance(boolean force){
		if(instance == null || force){
			instance = new DependencyManager();
		}
		return instance;
	}
	
	/**
	 * Check whether the {@link Job} child depends on the {@link Job} parent
	 * @param Job child 
	 * @param Job parent
	 * @return boolean 
	 */
	public boolean dependsOn(Job child, Job parent){
        synchronized(dependencies) {
        	//Get the dependencies of the child
        	List<Job> deps = dependencies.get(child);
        	if(deps == null)
        		return false;
        	for (Job j: deps) {
        		//Check if they contain the parent, or if they contain a job that is dependent on parent (aka dependent by proxy)
				return j.equals(parent) || dependsOn(j, parent); 
			}
            return false;
        }
	}
	
	/**
	 * Check whether the dependencies of{@link Job} j are met so that
	 * it can be executed.
	 * @param h
	 * @return boolean
	 */
	public boolean canExecute(Job j){
		List<Job> deps = this.dependencies.get(j);
		if (deps == null) {
			return true;
		} else {
			for (Job job : deps) {
				if (job.state() != Job.State.Finished) {
					System.out.println(job.toString() +" is not finished");
					return false;
				}
			}
			return true;
		}
	}
	
	/**
	 * Add a dependency {@link Job} parent for {@link Job} child. This means 
	 * that child cannot be run before parent is finished. Child is dependent on 
	 * parent
	 * 
	 * @param child
	 * @param parent
	 * @throws SchedulerException 
	 */
	public synchronized void  addDependency(Job child, Job parent) throws SchedulerException{
		if ( (child.state() != State.Created) && (child.state() != State.Yielded) ) {
        	throw new SchedulerException("Job dependencies cannot be added after the job has been queued.");
        }
		if( dependsOn(parent,child) || (child==parent) ) {
            throw new SchedulerException("Job dependencies are not allowed to be cyclic.");
        }
		
		List<Job> deps; 
		if((deps = this.dependencies.get(child)) ==null){
			deps = new LinkedList<Job>();
		}
		deps.add(parent);
		this.dependencies.put(child, deps);
	}
	
	/**
	 * Remove a dependency from {@link Job} from. This means that {@link Job} from
	 * is no longer dependent of {@link Job} which and can be executed even if which
	 * is not yet finished
	 * @param from
	 * @param which
	 * @return whether or not a dependency was removed
	 */
	public boolean removeDependency(Job from, Job which){
		List<Job> deps; 
		if((deps = this.dependencies.get(from)) ==null){
			return false;
		}
		return deps.remove(which);
	}
	
	/**
	 * Add a dependee to {@link Job} to. This means that as of now {@link Job} which is 
	 * dependent on {@link Job} to and can not be executed before it is finished.
	 * @param to
	 * @param which
	 */
	//TODO not sure if this is necessary...
	// because it is only used to notify them when the dependencies change so that the are switch from
	// blocked queue to workqueue. These are not used anymore
	@Deprecated
	public void addDependee(Job to, Job which){
		
	}
	
	
	/**
	 * Remove a dependee to {@link Job} to. This means that as of now {@link Job} which is 
	 * no longer dependent on {@link Job} to.
	 * @param from
	 * @param which
	 */
	//TODO not sure if this is necessary...
		// because it is only used to notify them when the dependencies change so that the are switch from
		// blocked queue to workqueue. These are not used anymore
		@Deprecated
	public void removeDependee(Job from, Job which){
		
	}
	
	/**
	 * Get a list of dependencies from {@link Job} from
	 * @param from
	 * @return Returns the list of dependencies of From or null if there are none.
	 */
	public List<Job> getDependency(Job from){
		return this.dependencies.get(from);
	}

//	/**
//	 * Returns whether {@link Job} a depends on {@link Job} b
//	 * @param A Job
//	 * @param B Job
//	 * @return
//	 */
//	public boolean dependOnEachOther(Job a, Job b) {
//		List<Job> deps = this.dependencies.get(a);
//		return deps.contains(b);
//	}
//	
//	public void add(Job parent, Job child) {
//		List<Job> existingDependencies = this.dependencies.get(parent);
//		if (existingDependencies == null) {
//			existingDependencies = new ArrayList<Job>();
//		}
//		existingDependencies.add(child);
//		this.dependencies.put(parent, existingDependencies);
//	}
//
//	public void remove(Job parent, Job child) {
//		List<Job> existingDependencies = this.dependencies.get(parent);
//		if (existingDependencies == null) {
//			existingDependencies = new ArrayList<Job>();
//		}
//		existingDependencies.remove(child);
//		this.dependencies.put(parent, existingDependencies);
//	}
//
//	public void remove(Job parent) {
//		this.dependencies.remove(parent);
//	}
}
