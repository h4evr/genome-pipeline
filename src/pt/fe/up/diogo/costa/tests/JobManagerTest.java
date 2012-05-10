package pt.fe.up.diogo.costa.tests;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

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
		program1.setId(1);
		
		RunnableForInputId<?> program2 = new SimpleRunnable();
		program2.fromString("ping -n 1 127.0.0.2");
		program2.setId(2);
		
		RunnableForInputId<?> program3 = new SimpleRunnable();
		program3.fromString("ping -n 1 127.0.0.3");
		program3.setId(3);
		
		RunnableForInputId<?> program4 = new SimpleRunnable();
		program4.fromString("ping -n 1 127.0.0.4");
		program4.setId(4);
		
		manager.getRunnables().put(program1.getId(), program1);
		manager.getRunnables().put(program2.getId(), program2);
		manager.getRunnables().put(program3.getId(), program3);
		manager.getRunnables().put(program4.getId(), program4);
				
		List<Integer> conditions;
		
		conditions = new ArrayList<Integer>(1);
		conditions.add(program3.getId());
		manager.getConditions().put(program2.getId(), conditions);
		
		conditions = new ArrayList<Integer>(2);
		conditions.add(program1.getId());
		conditions.add(program4.getId());
		manager.getConditions().put(program3.getId(), conditions);
		
		conditions = new ArrayList<Integer>(1);
		conditions.add(program1.getId());
		manager.getConditions().put(program4.getId(), conditions);
	}

	@Test
	public void test() {
		List<Long> input_ids = new ArrayList<Long>();
		
		for(long j = 0; j < 1; ++j) {
			input_ids.add(j);
		}
		
		manager.setupJobs(input_ids);
		manager.run(2);
		
		System.out.println(manager.toString());
	}
}
