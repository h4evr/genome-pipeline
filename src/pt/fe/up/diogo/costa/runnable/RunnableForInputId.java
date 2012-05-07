package pt.fe.up.diogo.costa.runnable;

public abstract class RunnableForInputId<T> implements IRunnable<T> {
	protected long input_id;
	protected int id;
	
	public RunnableForInputId(long input_id) {
		this.input_id = input_id;
	}
	
	public RunnableForInputId() {
		input_id = -1L;
	}
	
	public long getInputId() {
		return input_id;
	}

	public void setInputId(long input_id) {
		this.input_id = input_id;
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
