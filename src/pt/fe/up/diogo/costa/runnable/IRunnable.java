package pt.fe.up.diogo.costa.runnable;

import java.util.List;

import pt.fe.up.diogo.costa.Result;

public interface IRunnable<T> {
	public String getProgram();
	public void setProgram(String program);
	public List<String> getArguments();
	public void setArguments(List<String> args);
	public Result<T> run();
	public boolean fromString(String cmd);
}
