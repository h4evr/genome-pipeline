package pt.fe.up.diogo.costa;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pt.fe.up.diogo.costa.config.IniReader;
import pt.fe.up.diogo.costa.job.JobConfiguration;
import pt.fe.up.diogo.costa.job.JobManager;
import pt.fe.up.diogo.costa.runnable.RunnableForInputId;
import pt.fe.up.diogo.costa.runnable.SimpleRunnable;

public class Pipeline {	
	public static void main(String[] args) {
		JobConfiguration configuration = new JobConfiguration();
		JobManager manager = new JobManager();
		
		IniReader conf = IniReader.readFile("pipeline.ini");
		
		Map<String, String> runnableSection = conf.get("runnables");
		
		if(runnableSection.size() == 0) {
			System.out.println("No runnables found.");
			return;
		}
		
		for(String runnableName : runnableSection.values()) {
			Map<String, String> runnableInfo = conf.get(runnableName);
			
			RunnableForInputId<?> runnable = new SimpleRunnable();
			runnable.setId(Integer.parseInt(runnableInfo.get("id")));
			runnable.fromString(runnableInfo.get("program"));
			
			configuration.getRunnables().put(runnable.getId(), runnable);
		}
		
		Map<String, String> conditionsSection = conf.get("conditions");
		
		for(Map.Entry<String, String> runSrc : conditionsSection.entrySet()) {
			if(runSrc.getKey().equalsIgnoreCase("goal")) 
				continue;
			
			List<Integer> requirements = new ArrayList<Integer>();
			for(String v : runSrc.getValue().split(",")) {
				requirements.add(Integer.parseInt(v.trim()));
			}
			
			configuration.getConditions().put(Integer.parseInt(runSrc.getKey()), requirements);
		}
		
		configuration.setGoal(Integer.parseInt(conditionsSection.get("goal")));
		
		manager.setConfiguration(configuration);
		
		manager.setInputIdsPerThread(10);
		manager.setNumThreads(3);
		
		List<Long> input_ids = new ArrayList<Long>();
		
		for(long j = 0; j < 30; ++j) {
			input_ids.add(j);
		}
		
		manager.setupJobs(input_ids);
		manager.run();
		
		System.out.println(manager.toString());
	}
}
