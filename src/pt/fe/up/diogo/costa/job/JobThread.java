package pt.fe.up.diogo.costa.job;

import java.util.Date;
import java.util.List;

import pt.fe.up.diogo.costa.Result;
import pt.fe.up.diogo.costa.runnable.RunnableForInputId;

public class JobThread implements Runnable {
	private IJobManager jobManager;
	private JobConfiguration configuration;
	private List<Job> jobs;
	private String id;
	private boolean stop = false;
	
	private final static int timeout = 1000;
	
	public JobThread (String id, IJobManager jobManager) {
		this.id = id;
		this.jobManager = jobManager;
		this.configuration = jobManager.getConfiguration();
	}

	public synchronized void setNextJobs(List<Job> jobs) {
		this.jobs = jobs;
	
		if(this.jobs == null) {
			stop = true;
		} /*else {
			System.out.println("[" + id + "] Got " + jobs.size() + " jobs.");
		}*/
		
		this.notify();
	}
	
	public void stop() {
		stop = true;
	}
	
	@Override
	public void run() {
		stop = false;
		
		synchronized(this) {
			while(true) {
				if(stop)
					return;
				
				jobManager.requestNewBlockOfJobs(id);
				
				while(jobs == null) {
					try {
						wait(timeout);
						
						if(stop)
							return;
					} catch (InterruptedException e) { }
				}
				
				if(jobs != null) {
					Job j = null;
					
					while((j = getNextJob()) != null) {
						//System.out.println("[" + id + "] Running job " + j.getId() + ", input id: " + j.getInputId());
						runJob(j);
					}
					
					jobManager.finishedProcessingJobs(id, jobs);
					
					jobs = null;
				}
			}
		}
	}
	
	public Job getNextJob() {
		Job nextJob = null;
		
		if(jobs.size() == 0)
			return null;
		
		for(Job j : jobs) {
			if(!JobUtils.hasJobRan(j)) {
				if(JobUtils.areConditionsMet(configuration, jobs, j)) {
					nextJob = j;
					break;
				}
			}
		}
		
		return nextJob;
	}
	
	public void runJob(Job j) {
		if(j.getRunnableId() == null ||
		   !configuration.getRunnables().containsKey(j.getRunnableId())) {
			j.setStatus(JobStatus.ERROR);
			return;
		}
		
		//System.out.println("Running job " + j.getId() + " for input id " + j.getInputId());
		
		RunnableForInputId<?> runnable = configuration.getRunnables().get(j.getRunnableId());
		runnable.setInputId(j.getInputId());
		
		Result<?> res = runnable.run();
		
		if(res.hasError()) {
			j.setStatus(JobStatus.ERROR);
		} else {
			j.setStatus(JobStatus.SUCCESS);
		}
		
		j.setLastRun(new Date());
	}
}
