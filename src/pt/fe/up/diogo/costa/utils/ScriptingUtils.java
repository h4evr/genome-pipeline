package pt.fe.up.diogo.costa.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import pt.fe.up.diogo.costa.Result;

public class ScriptingUtils {
	public Result<String> executeProgram(String program, List<String> arguments) {
		StringBuilder sbArgs = new StringBuilder();
		
		if(arguments != null) {
			for(String a : arguments) {
				sbArgs.append(a).append(" ");
			}
		}
		
		System.out.println("Running the following command: " + program + " " + sbArgs.toString());
		
		try {
			List<String> args = new ArrayList<String>();
			
			args.add(program);
			args.addAll(arguments);
			
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
			return new Result<String>(e);
		}
	}
}
