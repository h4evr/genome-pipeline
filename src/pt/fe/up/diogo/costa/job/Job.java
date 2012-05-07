package pt.fe.up.diogo.costa.job;

import java.util.Date;

public class Job {
	protected long id;
	protected String host;
	protected long port;
	protected Integer runnableId;
	protected Date lastRun;
	protected JobStatus status;
	protected JobDetail details;
	
	public Job() {
		status = JobStatus.STOPPED;
	}
	
	public Date getLastRun() {
		return lastRun;
	}

	public void setLastRun(Date lastRun) {
		this.lastRun = lastRun;
	}

	public JobStatus getStatus() {
		return status;
	}

	public void setStatus(JobStatus status) {
		this.status = status;
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getHost() {
		return host;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public long getPort() {
		return port;
	}
	
	public void setPort(long port) {
		this.port = port;
	}
	
	public Integer getRunnableId() {
		return runnableId;
	}
	
	public void setRunnableId(Integer runnable) {
		this.runnableId = runnable;
	}

	public JobDetail getDetails() {
		return details;
	}

	public void setDetails(JobDetail details) {
		this.details = details;
	}
	
	@Override
	public int hashCode() {
		return (int)this.id;
	}
}
