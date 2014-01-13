package eu.sqooss.test.service.scheduler;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import eu.sqooss.impl.service.scheduler.JobPriorityComparator;
import eu.sqooss.service.scheduler.Job;

public class JobPriorityComparatorTests {


	@Test
	public final void testCompare() {
		Job j1 = new TestJobObject(20, "J1",0l);
		Job j2 = new TestJobObject(20, "J2",10l);
		
		JobPriorityComparator comparator = new JobPriorityComparator();
		assertEquals(-10, comparator.compare(j1, j2));
		
		assertEquals(10, comparator.compare(j2, j1));
		
		Job j3 = new TestJobObject(20, "AA");
		Job j4 = new TestJobObject(20, "BB");
		assertEquals(0, comparator.compare(j3, j4));
		
	}

	@Test
	public final void testEqualsObject() {
		JobPriorityComparator comparator1 = new JobPriorityComparator();
		JobPriorityComparator comparator2 = new JobPriorityComparator();
		Job j1 = new FailingJob("A");
		assertEquals(true, comparator1.equals(comparator2));
		assertEquals(false, comparator1.equals(j1));
	}

}
