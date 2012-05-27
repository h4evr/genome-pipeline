package pt.fe.up.diogo.costa.job;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import pt.fe.up.diogo.costa.data.JobDao;

public class JobManager implements IJobManager {
	private List<Job> jobs;
	private Map<Integer, List<Job>> jobsByRunnable;
	private List<Long> inputIds;
	
	private HashMap<String, JobThread> threads;
	private int numThreads = 3;
	private int inputIdsPerThread = 1;
	private final static int timeout = 200;
	private int lastInputIdIndex = 0;
	
	private boolean hasFinished = false;
	private Queue<String> requests = new ArrayBlockingQueue<String>(100);

	private JobConfiguration configuration;
	
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
				if(JobUtils.areConditionsMet(configuration, getJobs(), j)) {
					nextJob = j;
					break;
				}
			}
		}
		
		return nextJob;
	}
	
	public boolean hasRunnableJobsFinished(Integer runnableId) {
		if(runnableId == null || 
		   !configuration.getRunnables().containsKey(runnableId)) {
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
	public boolean setupJobs(List<Long> input_ids) {
		if(configuration == null)
			return false;
		
		if(configuration.getRunnables().size() == 0)
			return false;
		
		inputIds = input_ids;
		lastInputIdIndex = 0;
		jobs = new ArrayList<Job>(input_ids.size() * configuration.getRunnables().size() + 1);
		jobsByRunnable = new HashMap<Integer, List<Job>>();
		
		try {
			for(Integer runnableId : configuration.getRunnables().keySet()) {
				List<Job> runnableJobs = new ArrayList<Job>(input_ids.size());
				
				for(Long inputId : input_ids) {
					Job j = new Job();
					
					j.setRunnableId(runnableId);
					j.setInputId(inputId);
					
					JobDao.getInstance().saveJob(j);
					
					runnableJobs.add(j);
					jobs.add(j);
				}
	
				jobsByRunnable.put(runnableId, runnableJobs);
			}
		} catch(SQLException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	@Override
	public void run() {
		if(getJobs().size() == 0)
			return;
		
		if(configuration == null)
			return;
		
		if(configuration.getRunnables().size() == 0)
			return;
				
		if(configuration.getGoal() == null)
			return;
		
		if(inputIds.size() == 0)
			return;
		
		hasFinished = false;
		requests.clear();
		
		threads = new HashMap<String, JobThread>();
		
		for(int nT = 0; nT < numThreads; ++nT) {
			String threadName = "proc" + Integer.toString(nT);
			threads.put(threadName, new JobThread(threadName, this));
		}
		
		for(JobThread thread : threads.values()) {
			new Thread(thread).start();
		}
		
		synchronized(this) {
			while(true) {
				if(hasFinished) {
					for(JobThread thread : threads.values()) {
						thread.stop();
					}
					
					break;
				}
				
				//System.out.println("[MASTER] - Checking for requests...");
				
				while(requests.size() > 0) {
					String threadName = requests.poll();
					List<Job> nextJobs = getNextBlockOfJobs();
					if(nextJobs != null) {
						//System.out.println("[MASTER] - Send jobs to " + threadName);
						threads.get(threadName).setNextJobs(nextJobs);
					}
				}
				
				try {
					wait(timeout);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
		
	public synchronized void requestNewBlockOfJobs(String threadName) {		
		requests.add(threadName);
	}
	
	private List<Job> getNextBlockOfJobs() {
		if(inputIds == null || inputIds.size() == 0 || lastInputIdIndex >= inputIds.size()) {
			return null;
		}
		
		int lastIndex = lastInputIdIndex + inputIdsPerThread;
		if(lastIndex > inputIds.size())
			lastIndex = inputIds.size();
		
		//System.out.println("[MASTER] - nextInputIds : " + lastInputIdIndex + " - " + (lastIndex - 1));
		
		List<Long> nextInputIds = inputIds.subList(lastInputIdIndex, lastIndex);
		lastInputIdIndex = lastIndex;

		List<Job> nextJobs = new ArrayList<Job>(configuration.getRunnables().size() * nextInputIds.size());
		
		for(List<Job> runnableJobs : jobsByRunnable.values()) {
			for(Job j : runnableJobs) {
				if(nextInputIds.contains(j.getInputId())) {
					nextJobs.add(j);
				}
			}
		}
		
		if(nextJobs.size() == 0)
			return null;
		
		return nextJobs;
	}
	
	public synchronized void finishedProcessingJobs(String threadName, List<Job> js) {
		//System.out.println("[MASTER] - Received jobs from " + threadName);

		hasFinished = hasRunnableJobsFinished(configuration.getGoal());
	}
	
	public void setConfiguration(JobConfiguration config) {
		this.configuration = config;
	}
	
	public JobConfiguration getConfiguration() {
		return this.configuration;
	}
	
	public int getNumThreads() {
		return numThreads;
	}

	public void setNumThreads(int numThreads) {
		this.numThreads = numThreads;
	}

	public int getInputIdsPerThread() {
		return inputIdsPerThread;
	}

	public void setInputIdsPerThread(int inputIdsPerThread) {
		this.inputIdsPerThread = inputIdsPerThread;
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
