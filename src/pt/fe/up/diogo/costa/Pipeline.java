package pt.fe.up.diogo.costa;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pt.fe.up.diogo.costa.config.IniReader;
import pt.fe.up.diogo.costa.data.SequenceDao;
import pt.fe.up.diogo.costa.data.JobDao;
import pt.fe.up.diogo.costa.data.Sequence;
import pt.fe.up.diogo.costa.db.Database;
import pt.fe.up.diogo.costa.input.FastaInput;
import pt.fe.up.diogo.costa.input.IInput;
import pt.fe.up.diogo.costa.job.JobConfiguration;
import pt.fe.up.diogo.costa.job.JobManager;
import pt.fe.up.diogo.costa.runnable.RunnableFactory;
import pt.fe.up.diogo.costa.runnable.RunnableForInputId;

public class Pipeline {	
	private static final boolean deleteAllJobsOnStart = true;
	
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
			
			RunnableForInputId<?> runnable = RunnableFactory.buildRunnable(RunnableFactory.Type.fromName(runnableInfo.get("module")));
			
			if(runnable != null) {
				runnable.setId(Integer.parseInt(runnableInfo.get("id")));
				runnable.fromString(runnableInfo.get("program"));
				
				configuration.getRunnables().put(runnable.getId(), runnable);
			} else {
				System.err.println("Runnable module not recognized: " + runnableInfo.get("module"));
				return;
			}
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
		
		Map<String, String> setupSection = conf.get("setup");
		
		String fileToLoad = setupSection.get("sequences");
		
		if(fileToLoad == null) {
			System.err.println("No sequences file specified in the SETUP section.");
			return;
		}
		
		IInput in = new FastaInput();
		
		if(!in.open(fileToLoad)) {
			System.err.println("File not found: " + fileToLoad);
			return;
		}
		
		List<Long> input_ids = new ArrayList<Long>();
		
		Sequence s = null;
		try {
			while((s = in.readNextSequence()) != null) {
				System.out.println("Loading sequence " + s.getName() + "...");
				SequenceDao.getInstance().saveSequence(s);
				input_ids.add(new Long(s.getId()));
			}
			
			in.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			if(deleteAllJobsOnStart) {
				JobDao.getInstance().deleteAllJobs();
			}
			
			System.out.println("Setting up jobs...");
			manager.setupJobs(input_ids);
			
			System.out.println("Starting analysis!");
			manager.run();
			
			System.out.println("Finished!!");
			System.out.println(manager.toString());			
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		Database.close();
	}
}
