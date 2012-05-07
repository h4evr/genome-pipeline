package pt.fe.up.diogo.costa.tests;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import pt.fe.up.diogo.costa.job.Job;
import pt.fe.up.diogo.costa.job.JobManager;
import pt.fe.up.diogo.costa.runnable.RunnableForInputId;
import pt.fe.up.diogo.costa.runnable.SimpleRunnable;

public class JobManagerTest {

	private static JobManager manager;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		manager = new JobManager();
		
		RunnableForInputId<?> program1 = new SimpleRunnable();
		program1.fromString("ping -n 1 127.0.0.1");
		
		RunnableForInputId<?> program2 = new SimpleRunnable();
		program2.fromString("ping -n 1 127.0.0.2");
		
		RunnableForInputId<?> program3 = new SimpleRunnable();
		program3.fromString("ping -n 1 127.0.0.3");
		
		RunnableForInputId<?> program4 = new SimpleRunnable();
		program4.fromString("ping -n 1 127.0.0.4");
		
		manager.getRunnables().put(1, program1);
		manager.getRunnables().put(2, program2);
		manager.getRunnables().put(3, program3);
		manager.getRunnables().put(4, program4);
		
		Job j1 = new Job();
		j1.setId(1);
		j1.setHost("127.0.0.1");
		j1.setPort(9999);
		j1.setRunnableId(1);
		
		Job j2 = new Job();
		j2.setId(2);
		j2.setHost("127.0.0.1");
		j2.setPort(9999);
		j2.setRunnableId(2);
		
		Job j3 = new Job();
		j3.setId(3);
		j3.setHost("127.0.0.1");
		j3.setPort(9999);
		j3.setRunnableId(3);
		
		Job j4 = new Job();
		j4.setId(4);
		j4.setHost("127.0.0.1");
		j4.setPort(9999);
		j4.setRunnableId(4);
		
		manager.getJobs().add(j1);
		manager.getJobs().add(j2);
		manager.getJobs().add(j3);
		manager.getJobs().add(j4);
		
		List<Job> jobs;
		
		jobs = new ArrayList<Job>(1);
		jobs.add(j1);
		manager.getConditions().put(j2, jobs);
		
		jobs = new ArrayList<Job>(2);
		jobs.add(j1);
		jobs.add(j4);
		manager.getConditions().put(j3, jobs);
		
		jobs = new ArrayList<Job>(1);
		jobs.add(j1);
		manager.getConditions().put(j4, jobs);
	}

	@Test
	public void test() {
		List<Long> input_ids = new ArrayList<Long>();
		
		for(long j = 0; j < 10; ++j) {
			input_ids.add(j);
		}
		
		manager.run(input_ids);
		
		System.out.println(manager.toString());
	}
}
