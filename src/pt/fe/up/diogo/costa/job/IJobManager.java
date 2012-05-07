package pt.fe.up.diogo.costa.job;

import java.util.List;
import java.util.Map;

import pt.fe.up.diogo.costa.runnable.RunnableForInputId;

public interface IJobManager {
	public List<Job> getJobs();
	public void setJobs(List<Job> jobs);
	public List<Job> getJobsWithErrors();
	
	public boolean hasJobRanOnInputId(Job job, long input_id);
	public Job getNextJobForInputId(long input_id);
	
	public Map<Job, List<Job>> getConditions();
	
	public void runJobForInputId(Job j, long input_id);
	
	public void run(List<Long> input_ids);
	
	public Map<Integer, RunnableForInputId<?>> getRunnables();
}
