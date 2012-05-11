package pt.fe.up.diogo.costa.job;

import java.util.List;

public interface IJobManager {
	public List<Job> getJobs();
	public List<Job> getJobsWithErrors();
	
	public boolean hasJobRan(Job job);
	public Job getNextJob();
	
	public void setConfiguration(JobConfiguration config);
	public JobConfiguration getConfiguration();
	public boolean setupJobs(List<Long> input_ids);
	public void run();
	
	public void requestNewBlockOfJobs(String threadName);	
	public void finishedProcessingJobs(String threadName, List<Job> jobs);
}
