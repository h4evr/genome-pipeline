package pt.fe.up.diogo.costa.runnable;

public abstract class RunnableForInputId<T> implements IRunnable<T> {
	protected long jobId;
	protected int id;
	
	public RunnableForInputId(long jobId) {
		this.jobId = jobId;
	}
	
	public RunnableForInputId() {
		jobId = -1L;
	}
	
	public long getJobId() {
		return jobId;
	}

	public void setJobId(long jobId) {
		this.jobId = jobId;
	}
	
	@Override
	public int hashCode() {
		return id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
