package pt.fe.up.diogo.costa.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.fe.up.diogo.costa.Result;
import pt.fe.up.diogo.costa.runnable.RunnableForInputId;

public class JobManager implements IJobManager {
	private HashMap<Long, Job> jobsById;
	private List<Job> jobs;
	private Map<Job, List<Job>> conditions;
	private Map<Long, List<Long>> jobsByInputId;
	private HashMap<Integer, RunnableForInputId<?>> runnables;
	
	public JobManager() {
		jobsByInputId = new HashMap<Long, List<Long>>();
	}
	
	@Override
	public List<Job> getJobs() {
		if(jobs == null) {
			jobsById = new HashMap<Long, Job>();
			jobs = new ArrayList<Job>();
		}
		
		return jobs;
	}

	@Override
	public void setJobs(List<Job> jobs) {
		this.jobs = jobs;
		this.jobsById = new HashMap<Long, Job>();
		
		for(Job j : jobs) {
			this.jobsById.put(j.getId(), j);
		}
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
	public boolean hasJobRanOnInputId(Job job, long input_id) {
		return jobsByInputId.containsKey(input_id) && 
			   jobsByInputId.get(input_id) != null &&
			   jobsByInputId.get(input_id).contains(job.getId());
	}

	@Override
	public Job getNextJobForInputId(long input_id) {
		Job nextJob = null;
		
		if(getJobs().size() == 0)
			return null;
		
		if(!jobsByInputId.containsKey(input_id)) {
			return getJobs().get(0);
		}
		
		for(Job j : getJobs()) {
			if(!hasJobRanOnInputId(j, input_id)) {
				if(areConditionsMet(j, input_id)) {
					nextJob = j;
					break;
				}
			}
		}
		
		return nextJob;
	}

	@Override
	public Map<Job, List<Job>> getConditions() {
		if(conditions == null)
			conditions = new HashMap<Job, List<Job>>();
		return conditions;
	}

	@Override
	public void run(List<Long> input_ids) {
		if(getJobs().size() == 0)
			return;
		
		if(getRunnables().size() == 0)
			return;
		
		Job goal = getJobs().get(getJobs().size() - 1);
		
		while(true) {
			for(Long inputId : input_ids) {
				Job nextJob = getNextJobForInputId(inputId);
				
				if(nextJob != null) {
					nextJob.setStatus(JobStatus.RUNNING);
					runJobForInputId(nextJob, inputId);
					if(!JobStatus.ERROR.equals(nextJob.getStatus())) {
						nextJob.setStatus(JobStatus.STOPPED);
					}
				}
			}
			
			if(JobStatus.ERROR.equals(goal.getStatus())) {
				break;
			} else {
				boolean wasGoalReached = true;
				
				for(Long inputId : input_ids) {
					if(!hasJobRanOnInputId(goal, inputId)) {
						wasGoalReached = false;
						break;
					}
				}
				
				if(wasGoalReached)
					break;
			}
		}
	}
	
	@Override
	public void runJobForInputId(Job j, long input_id) {
		if(j.getRunnableId() == null ||
		   !getRunnables().containsKey(j.getRunnableId())) 
			return;
		
		System.out.println("Running job " + j.getId() + " for input id " + input_id);
		
		RunnableForInputId<?> runnable = getRunnables().get(j.getRunnableId());
		runnable.setInputId(input_id);
		
		Result<?> res = runnable.run();
		
		if(res.hasError()) {
			j.setStatus(JobStatus.ERROR);
		} else {
			if(!jobsByInputId.containsKey(input_id))
				jobsByInputId.put(input_id, new ArrayList<Long>(getJobs().size()));
			jobsByInputId.get(input_id).add(j.getId());
		}
	}
	
	private boolean areConditionsMet(Job j, long input_id) {
		if(!conditions.containsKey(j))
			return true;
		
		List<Job> previousJobs = conditions.get(j);
		
		for(Job pj : previousJobs) {
			if(!hasJobRanOnInputId(pj, input_id)) {
				return false;
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
