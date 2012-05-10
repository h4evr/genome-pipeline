package pt.fe.up.diogo.costa.job;

import java.util.List;
import java.util.Map;

import pt.fe.up.diogo.costa.runnable.RunnableForInputId;

public interface IJobManager {
	public List<Job> getJobs();
	public List<Job> getJobsWithErrors();
	
	public boolean hasJobRan(Job job);
	public Job getNextJob();
	
	public Map<Integer, List<Integer>> getConditions();
	
	public void runJob(Job j);
	
	public boolean setupJobs(List<Long> input_ids);
	public void run(Integer runnableIdGoal);
	
	public Map<Integer, RunnableForInputId<?>> getRunnables();
}
