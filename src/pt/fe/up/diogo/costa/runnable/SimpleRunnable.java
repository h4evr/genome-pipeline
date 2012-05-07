package pt.fe.up.diogo.costa.runnable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import pt.fe.up.diogo.costa.Result;

public class SimpleRunnable extends RunnableForInputId<String> {
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
		StringBuilder sbArgs = new StringBuilder();
		
		if(arguments != null) {
			for(String a : arguments) {
				sbArgs.append(a).append(" ");
			}
		}
		
		System.out.println("Running the following command: " + program + " " + sbArgs.toString());
		
		try {
			List<String> args = new ArrayList<String>();
			
			args.add(getProgram());
			args.addAll(getArguments());
			
			Process p = (new ProcessBuilder(args)).start();
			InputStream stream = p.getInputStream();
			StringBuilder sb = new StringBuilder();
			
			byte[] buf = new byte[1000];
			int readBytes = 0;
			
			while((readBytes = stream.read(buf)) > 0) {
				sb.append(new String(buf, 0, readBytes));
			}
			
			p.destroy();
			
			return new Result<String>(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
			return new Result<String>(e);
		}
	}
	
	@Override
	public boolean fromString(String cmd) {
		List<String> buffer = new ArrayList<String>();
		
		String buffering = "";
		
		StringBuilder tmp = new StringBuilder();
		
		String[] splitted = cmd.split(" ");
		
		for(String s : splitted) {
			if(buffering.length() > 0 && s.endsWith(buffering)) {
				tmp.append(s);
				buffer.add(tmp.toString());
				buffering = "";
				tmp = new StringBuilder();
			} else if(buffering.length() == 0 && (s.startsWith("\"") || s.startsWith("'"))) {
				buffering = s.substring(0, 1);
				tmp.append(s);
			} else {
				buffer.add(s);
			}
		}
		
		if(buffer.size() == 1) {
			this.setProgram(buffer.get(0));
			this.setArguments(null);
			return true;
		} else if(buffer.size() > 1) {
			this.setProgram(buffer.get(0));
			this.setArguments(buffer.subList(1, buffer.size()));
			return true;
		} else {
			return false;
		}
	}
}
