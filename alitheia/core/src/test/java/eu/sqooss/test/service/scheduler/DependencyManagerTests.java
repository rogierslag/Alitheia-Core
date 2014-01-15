package eu.sqooss.test.service.scheduler;

import static org.junit.Assert.*;

import org.junit.Test;

import eu.sqooss.impl.service.scheduler.DependencyManager;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.SchedulerException;

public class DependencyManagerTests {

	@Test
	public final void testGetInstance() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testDependsOn() throws SchedulerException {
		Job j1 = new TestJobObject(1, "j1");
		Job j2 = new TestJobObject(1, "j2");
		Job j3 = new TestJobObject(1, "j3");
		Job j4 = new TestJobObject(1, "j4");
		DependencyManager d = DependencyManager.getInstance();
		d.addDependency(j1, j2);
		d.addDependency(j2, j3);
		d.addDependency(j3, j4);
		assertTrue(d.dependsOn(j1, j4));
		
	}

	@Test
	public final void testCanExecute() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testAddDependency() throws SchedulerException {
		Job j1 = new TestJobObject(1, "j1");
		Job j2 = new TestJobObject(1, "j2");
		DependencyManager d = DependencyManager.getInstance(true);
		d.addDependency(j1, j2);
		assertTrue("J1 should depend on j2 now", d.dependsOn(j1,j2));
	}

	@Test
	public final void testRemoveDependency() throws SchedulerException {
		Job j1 = new TestJobObject(1, "j1");
		Job j2 = new TestJobObject(1, "j2");
		DependencyManager d = DependencyManager.getInstance(true);
		d.addDependency(j1, j2);
		assertTrue("J1 should depend on j2 now", d.dependsOn(j1,j2));
		d.removeDependency(j1, j2);
		assertFalse("J1 should not depend on j2 anymore", d.dependsOn(j1,j2));
		
		
	}

	@Test
	public final void testAddDependee() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testRemoveDependee() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testGetDependency() {
		fail("Not yet implemented"); // TODO
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
