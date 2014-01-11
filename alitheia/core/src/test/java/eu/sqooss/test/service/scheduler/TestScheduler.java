package eu.sqooss.test.service.scheduler;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.fail;
import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.scheduler.SchedulerServiceImpl;
import eu.sqooss.service.scheduler.SchedulerException;
import eu.sqooss.service.tds.InvalidAccessorException;

public class TestScheduler {
    
    static SchedulerServiceImpl sched;
    
    @BeforeClass
    public static void setUp() {
    	try {
    		AlitheiaCore.testInstance();
    	} catch (Exception e) {
    		// Yeah this blows. But is works
    	}
    	sched = new SchedulerServiceImpl();
    }
    
    @Test
    public void TestExecutionThreads() throws Exception {
    	assertEquals(0,sched.getSchedulerStats().getWorkerThreads());
        sched.startExecute(2);
        assertEquals(2,sched.getSchedulerStats().getWorkerThreads());
    }

    @Test(expected=SchedulerException.class)
    public void TestCircularDependency1() throws Exception {
    	TestJob j1 = new TestJob(0, "circular1");
    	j1.addDependency(j1);
    }
    
    @Test(expected=SchedulerException.class)
    public void TestCircularDependency2() throws Exception {
    	TestJob j1 = new TestJob(0, "circular1");
    	TestJob j2 = new TestJob(3, "circular2");
    	j1.addDependency(j2);
    	j2.addDependency(j1);
    }
    
    @Test(expected=SchedulerException.class)
    public void TestCircularDependency3() throws Exception {
    	TestJob j1 = new TestJob(4, "circular1");
    	TestJob j2 = new TestJob(5, "circular2");
    	TestJob j3 = new TestJob(6, "circular3");
    	j1.addDependency(j2);
    	j2.addDependency(j3);
    	j3.addDependency(j1);
    }
    
    @Test
    public void TestCircularDependency4() throws Exception {
    	TestJob j1 = new TestJob(4, "circular1");
    	TestJob j2 = new TestJob(5, "circular2");
    	TestJob j3 = new TestJob(6, "circular3");
    	j1.addDependency(j2);
    	j2.addDependency(j3);
    	assertEquals(true,j2.dependsOn(j3));
    }
    
    @Test
    public void TestJobYield() throws Exception {
    	assertEquals(0, sched.getSchedulerStats().getTotalJobs());
        TestJob j1 = new TestJob(10, "Test");
        sched.enqueue(j1);
        assertEquals(1, sched.getSchedulerStats().getTotalJobs());
        TestJob j2 = new TestJob(10, "Test");
        sched.enqueue(j2);
        assertEquals(2, sched.getSchedulerStats().getTotalJobs());
        TestJob j3 = new TestJob(10, "Test");
        sched.enqueue(j3);
        assertEquals(3, sched.getSchedulerStats().getTotalJobs());
        TestJob j4 = new TestJob(10, "Test");
        sched.enqueue(j4);
        assertEquals(4, sched.getSchedulerStats().getTotalJobs());
        TestJob j5 = new TestJob(10, "Test");
        sched.enqueue(j5);
        assertEquals(5, sched.getSchedulerStats().getTotalJobs());
    }
    
    @AfterClass
    public static void tearDown() {
        while (sched.getSchedulerStats().getFinishedJobs() < 4)
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {}
            
        sched.stopExecute();
    }
}