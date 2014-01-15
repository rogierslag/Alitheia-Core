package eu.sqooss.test.service.scheduler;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito.*;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.scheduler.DependencyManager;
import eu.sqooss.impl.service.scheduler.SchedulerServiceImpl;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.SchedulerException;

public class DependencyManagerTests {

	 @BeforeClass
	    public static void setUp() {
	    	try {
	    		AlitheiaCore.testInstance();
	    	} catch (Exception e) {
	    		// Yeah this blows. But is works
	    	}
	    }
	 
	@Test
	public final void testGetInstance() {
		DependencyManager dm = DependencyManager.getInstance();
		assertTrue("getInstance should return the samen object",
				dm == DependencyManager.getInstance());
		assertTrue("getInstance(true) should return a different object", 
				dm != DependencyManager.getInstance(true));
		assertTrue("getInstance(false) should return the same object", 
				dm != DependencyManager.getInstance(false));
	}

	@Test
	public final void testDependsOn() throws SchedulerException {
		Job j1 = new TestJobObject(1, "j1");
		Job j2 = new TestJobObject(1, "j2");
		Job j3 = new TestJobObject(1, "j3");
		Job j4 = new TestJobObject(1, "j4");
		Job j5 = new TestJobObject(1, "j5");
		DependencyManager d = DependencyManager.getInstance();
		assertFalse("j1 depends on nothing yet", d.dependsOn(j1, j2));
		d.addDependency(j1, j2);
		assertTrue("j1 depends on j2 now", d.dependsOn(j1, j2));
		d.addDependency(j2, j3);
		d.addDependency(j3, j4);
		d.addDependency(j5, j1);
		assertTrue("j1 depends on j4 by proxy", d.dependsOn(j1, j4));
		assertTrue("j1 does not depend on j5", d.dependsOn(j1, j5));
		assertTrue("j5 does depend on j1", d.dependsOn(j5, j1));
		
	}

	@Test
	public final void testCanExecute() throws SchedulerException {
		//First init some things we need.
		Job j1 = new TestJobObject(1, "j1");
		Job j2 = new TestJobObject(1, "j2");
		DependencyManager d = DependencyManager.getInstance();
		
		assertTrue("J1 can run cause it has no dependencies", d.canExecute(j1));
		d.addDependency(j1, j2);
		assertTrue("J1 cant run cause j2 is not finished", d.canExecute(j2));
		SchedulerServiceImpl sched = new SchedulerServiceImpl();
		sched.enqueue(j1);
		sched.enqueue(j2);
		assertTrue("j2 has no dependencies so can execute", d.canExecute(j2));
		sched.startExecute(1);
		j2.waitForFinished();
		synchronized (sched) {
			assertTrue("Check that j1 is not finished yet", j1.state() != Job.State.Finished);
			assertTrue("j2 is now finished", j2.state() == Job.State.Finished);
			assertTrue("J1 can run now since j2 is finished", d.canExecute(j1));
		}
		j1.waitForFinished();
		sched.shutDown();
		
	}

	@Test
	public final void testAddDependency() throws SchedulerException {
		Job j1 = new TestJobObject(1, "j1");
		Job j2 = new TestJobObject(1, "j2");
		Job j3 = new TestJobObject(1, "j3");
		DependencyManager d = DependencyManager.getInstance(true);
		d.addDependency(j1, j2);
		d.addDependency(j1, j3);
		assertTrue("J1 should depend on j2 now", d.dependsOn(j1,j2));
		assertTrue("J1 should depend on j3 now", d.dependsOn(j1,j3));
	}

	@Test(expected=SchedulerException.class)
	public final void testAddDependencyOnRunningJob() throws SchedulerException{
		TestJobObject j1 = new TestJobObject(1, "j1");
		TestJobObject j2 = new TestJobObject(1, "j2");
		DependencyManager d = DependencyManager.getInstance(true);
		
		j1.mockState(Job.State.Running);
		d.addDependency(j1, j2); //Should throw exception
	}
	
	@Test(expected=SchedulerException.class)
	public final void testAddDependencyOnFinishedJOb() throws SchedulerException{
		TestJobObject j1 = new TestJobObject(1, "j1");
		TestJobObject j2 = new TestJobObject(1, "j2");
		DependencyManager d = DependencyManager.getInstance(true);
		
		j1.mockState(Job.State.Finished);
		d.addDependency(j1, j2); //Should throw exception
	}
	
	@Test(expected=SchedulerException.class)
	public final void testAddDependencyOnFailed() throws SchedulerException{
		TestJobObject j1 = new TestJobObject(1, "j1");
		TestJobObject j2 = new TestJobObject(1, "j2");
		DependencyManager d = DependencyManager.getInstance(true);
		
		j1.mockState(Job.State.Error);
		d.addDependency(j1, j2); //Should throw exception
	}
	
	@Test(expected=SchedulerException.class)
	public final void testAddCyclicDependency() throws SchedulerException{
		TestJobObject j1 = new TestJobObject(1, "j1");
		TestJobObject j2 = new TestJobObject(1, "j2");
		DependencyManager d = DependencyManager.getInstance(true);
		d.addDependency(j1, j2);
		j1.mockState(Job.State.Error);
		d.addDependency(j2, j1); //Should throw exception no cyclic deps
	}
	
	@Test
	public final void testRemoveDependency() throws SchedulerException {
		Job j1 = new TestJobObject(1, "j1");
		Job j2 = new TestJobObject(1, "j2");
		DependencyManager d = DependencyManager.getInstance(true);
		d.removeDependency(j1, j2);
		assertFalse("J1 should not depend on j2 yet", d.dependsOn(j1,j2));
		d.addDependency(j1, j2);
		assertTrue("J1 should depend on j2 now", d.dependsOn(j1,j2));
		d.removeDependency(j1, j2);
		assertFalse("J1 should not depend on j2 anymore", d.dependsOn(j1,j2));
		
		
	}

	@Test
	public final void testAddDependee() {
//		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testRemoveDependee() {
//		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testGetDependency() throws SchedulerException {
		Job j1 = new TestJobObject(1, "j1");
		Job j2 = new TestJobObject(1, "j2");
		Job j3 = new TestJobObject(1, "j3");
		DependencyManager d = DependencyManager.getInstance(true);
		d.addDependency(j1, j2);
		d.addDependency(j1, j3);
		LinkedList<Job> l = new LinkedList<Job>();
		l.add(j2);
		l.add(j3);
		assertEquals("J1 should have j2 and j3 as dependency", l, d.getDependency(j1));
	}

	@Test
	public final void testAdd() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testRemoveJobJob() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testRemoveJob() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testDependOnEachOther() {
		fail("Not yet implemented"); // TODO
	}

}
