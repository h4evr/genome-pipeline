package pt.fe.up.diogo.costa;

import java.util.ArrayList;
import java.util.List;

public class Result<T> {
	protected List<T> values;
	protected boolean hasError;
	protected Exception error;
	
	public Result() {
		hasError = false;
	}
	
	public Result(T value) {
		values = new ArrayList<T>(1);
		values.add(value);
		hasError = false;
	}
	
	public Result(List<T> values) {
		this.values = values;
		hasError = false;
	}
	
	public Result(Exception error) {
		hasError = true;
		this.error = error;
	}
	
	public T getValue() {
		if(values != null && values.size() > 0) 
			return values.get(0);
		else
			return null;
	}
	
	public List<T> getValues() {
		return values;
	}
	
	public boolean hasError() {
		return hasError;
	}
	
	public Exception getError() {
		return error;
	}
}
