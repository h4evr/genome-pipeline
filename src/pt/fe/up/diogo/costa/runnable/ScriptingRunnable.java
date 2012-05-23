package pt.fe.up.diogo.costa.runnable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import pt.fe.up.diogo.costa.Result;

public class ScriptingRunnable extends RunnableForInputId<Object>  {
	private String program;
	private List<String> arguments;
	
	@Override
	public String getProgram() {
		return program;
	}

	@Override
	public void setProgram(String program) {
		this.program = program;
	}

	@Override
	public List<String> getArguments() {
		return arguments;
	}

	@Override
	public void setArguments(List<String> args) {
		this.arguments = args;		
	}

	@Override
	public Result<Object> run() {
		if(getProgram() == null)
			return new Result<Object>(new Exception("No program specified!"));
		
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine js = manager.getEngineByName("JavaScript");
		
		js.put("self", this);
		
		InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(getProgram());
		
		if(stream == null) {
			try {
			stream = new FileInputStream(getProgram());
			} catch(FileNotFoundException e) {
				return new Result<Object>(e);
			}
		}
		
		try {
			InputStreamReader reader = new InputStreamReader(stream);
			return new Result<Object>(js.eval(reader));
		} catch(ScriptException e) {
			return new Result<Object>(e);
		}
	}

	@Override
	public boolean fromString(String cmd) {
		return false;
	}
	
}
