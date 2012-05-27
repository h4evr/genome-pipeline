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
import pt.fe.up.diogo.costa.data.AnnotationDao;
import pt.fe.up.diogo.costa.data.JobDao;
import pt.fe.up.diogo.costa.data.SequenceDao;
import pt.fe.up.diogo.costa.utils.ScriptingUtils;

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
		js.put("jobDao", JobDao.getInstance());
		js.put("sequenceDao", SequenceDao.getInstance());
		js.put("out", System.out);
		js.put("annotationDao", AnnotationDao.getInstance());
		js.put("ScriptingUtils", ScriptingUtils.class);
		
		InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(getProgram());
		
		if(stream == null) {
			try {
				stream = new FileInputStream(getProgram());
			} catch(FileNotFoundException e) {
				e.printStackTrace();
				return new Result<Object>(e);
			}
		}
		
		try {
			InputStreamReader reader = new InputStreamReader(stream);
			return new Result<Object>(js.eval(reader));
		} catch(ScriptException e) {
			e.printStackTrace();
			return new Result<Object>(e);
		}
	}

	@Override
	public boolean fromString(String cmd) {
		setProgram(cmd);
		return true;
	}
	
}
