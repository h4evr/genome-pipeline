package pt.fe.up.diogo.costa.runnable; 

import java.util.List;

import pt.fe.up.diogo.costa.Result;

public class DummyRunnable implements IRunnable<String> {
	private String program = "";
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
	public Result<String> run() {
		StringBuilder sb = new StringBuilder();
		
		if(arguments != null) {
			for(String a : arguments) {
				sb.append(a).append(" ");
			}
		}
		
		System.out.println("Running the following command: " + program + " " + sb.toString());
		
		return new Result<String>("OK");
	}

	@Override
	public boolean fromString(String cmd) {
		return false;
	}
}
