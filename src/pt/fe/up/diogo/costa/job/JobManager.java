package pt.fe.up.diogo.costa.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.fe.up.diogo.costa.Result;
import pt.fe.up.diogo.costa.runnable.RunnableForInputId;

public class JobManager implements IJobManager {
	private List<Job> jobs;
	private Map<Integer, List<Job>> jobsByRunnable;
	private Map<Integer, List<Integer>> conditions;
	private HashMap<Integer, RunnableForInputId<?>> runnables;
	
	public JobManager() {
	}
	
	@Override
	public List<Job> getJobs() {
		if(jobs == null) {
			jobs = new ArrayList<Job>();
			jobsByRunnable = new HashMap<Integer, List<Job>>();
		}
		
		return jobs;
	}

	@Override
	public List<Job> getJobsWithErrors() {
		List<Job> res = new ArrayList<Job>(getJobs().size());
		
		for(Job j : getJobs()) {
			if(JobStatus.ERROR.equals(j.getStatus())) {
				res.add(j);
			}
		}
		
		return res;
	}

	@Override
	public boolean hasJobRan(Job job) {
		return job.getStatus().compareTo(JobStatus.RUNNING) > 0;
	}

	@Override
	public Job getNextJob() {
		Job nextJob = null;
		
		if(getJobs().size() == 0)
			return null;
		
		for(Job j : getJobs()) {
			if(!hasJobRan(j)) {
				if(areConditionsMet(j)) {
					nextJob = j;
					break;
				}
			}
		}
		
		return nextJob;
	}
	
	public boolean hasRunnableJobsFinished(Integer runnableId) {
		if(runnableId == null || 
		   !runnables.containsKey(runnableId)) {
			return true;
		}
		
		List<Job> runnableJobs = jobsByRunnable.get(runnableId);
		
		for(Job j : runnableJobs) {
			if(!hasJobRan(j))
				return false;
		}
		
		return true;
	}

	@Override
	public Map<Integer, List<Integer>> getConditions() {
		if(conditions == null)
			conditions = new HashMap<Integer, List<Integer>>();
		return conditions;
	}
	
	public boolean setupJobs(List<Long> input_ids) {
		if(getRunnables().size() == 0)
			return false;
		
		jobs = new ArrayList<Job>(input_ids.size() * getRunnables().size() + 1);
		jobsByRunnable = new HashMap<Integer, List<Job>>();
		
		Long jobId = 0L;
		
		for(Integer runnableId : getRunnables().keySet()) {
			List<Job> runnableJobs = new ArrayList<Job>(input_ids.size());
			
			for(Long inputId : input_ids) {
				++jobId;
				
				Job j = new Job();
				j.setId(jobId.longValue());
				j.setRunnableId(runnableId);
				j.setInputId(inputId);
				
				runnableJobs.add(j);
				jobs.add(j);
			}

			jobsByRunnable.put(runnableId, runnableJobs);
		}
		
		return true;
	}

	@Override
	public void run(Integer runnableIdGoal) {
		if(getJobs().size() == 0)
			return;
		
		if(getRunnables().size() == 0)
			return;
		
		while(true) {
			Job nextJob = getNextJob();
			
			if(nextJob != null) {
				runJob(nextJob);
			} else {
				break;
			}

			if(hasRunnableJobsFinished(runnableIdGoal)) {
				break;
			}
		}
	}
	
	@Override
	public void runJob(Job j) {
		if(j.getRunnableId() == null ||
		   !getRunnables().containsKey(j.getRunnableId())) {
			j.setStatus(JobStatus.ERROR);
			return;
		}
		
		System.out.println("Running job " + j.getId() + " for input id " + j.getInputId());
		
		RunnableForInputId<?> runnable = getRunnables().get(j.getRunnableId());
		runnable.setInputId(j.getInputId());
		
		Result<?> res = runnable.run();
		
		if(res.hasError()) {
			j.setStatus(JobStatus.ERROR);
		} else {
			j.setStatus(JobStatus.SUCCESS);
		}
		
		j.setLastRun(new Date());
	}
	
	private boolean areConditionsMet(Job j) {
		if(!conditions.containsKey(j.getRunnableId()))
			return true;
		
		List<Integer> previousJobs = conditions.get(j.getRunnableId());
		
		for(Integer pj : previousJobs) {
			List<Job> runnableJobs = jobsByRunnable.get(pj);
			
			for(Job job : runnableJobs) {
				if(job.getInputId().equals(j.getInputId()) &&
					!hasJobRan(job)) {
					return false;
				}
			}
		}
		
		return true;
	}

	@Override
	public Map<Integer, RunnableForInputId<?>> getRunnables() {
		if(runnables == null)
			runnables = new HashMap<Integer, RunnableForInputId<?>>();
		
		return runnables;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		for(Job j : getJobs()) {
			sb.append("Job ").append(j.getDetails() != null ? j.getDetails().getName() : j.getId()).append(": ").append(j.getStatus().toString()).append("\n");
		}
		
		return sb.toString();
	}

}
