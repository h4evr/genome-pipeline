package pt.fe.up.diogo.costa.job;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.fe.up.diogo.costa.runnable.RunnableForInputId;

public class JobConfiguration implements Serializable {
	private static final long serialVersionUID = 517942418460681396L;

	private Map<Integer, List<Integer>> conditions;
	private HashMap<Integer, RunnableForInputId<?>> runnables;
	private Integer goal;
	
	public Map<Integer, List<Integer>> getConditions() {
		if(conditions == null)
			conditions = new HashMap<Integer, List<Integer>>();
		return conditions;
	}
	
	public Map<Integer, RunnableForInputId<?>> getRunnables() {
		if(runnables == null)
			runnables = new HashMap<Integer, RunnableForInputId<?>>();
		
		return runnables;
	}
	
	public Integer getGoal()  {
		return this.goal;
	}
	
	public void setGoal(Integer goal) {
		this.goal = goal;
	}
}
