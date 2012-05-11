package pt.fe.up.diogo.costa.job;

import java.util.List;

public abstract class JobUtils {
		
	public static boolean hasJobRan(Job job) {
		return job.getStatus().compareTo(JobStatus.RUNNING) > 0;
	}
	
	public static boolean areConditionsMet(JobConfiguration configuration, List<Job> jobs, Job j) {
		if(!configuration.getConditions().containsKey(j.getRunnableId()))
			return true;
		
		List<Integer> previousJobs = configuration.getConditions().get(j.getRunnableId());
		
		if(previousJobs.size() == 0)
			return true;
		
		for(Job job : jobs) {
			for(Integer pj : previousJobs) {
				if(!job.getRunnableId().equals(pj))
					continue;
				
				if(job.getInputId().equals(j.getInputId()) &&
					!hasJobRan(job)) {
					return false;
				}
			}
		}
		
		return true;
	}
}
