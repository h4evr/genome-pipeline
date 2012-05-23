package pt.fe.up.diogo.costa.tests;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import pt.fe.up.diogo.costa.Result;
import pt.fe.up.diogo.costa.runnable.IRunnable;
import pt.fe.up.diogo.costa.runnable.RunnableForInputId;
import pt.fe.up.diogo.costa.runnable.ScriptingRunnable;
import pt.fe.up.diogo.costa.runnable.SimpleRunnable;


public class RunnableTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	
	}

	@Test
	public void testRunnable() {
		IRunnable<String> runnable = new SimpleRunnable();
		
		assertTrue(runnable.fromString("ping -n 1 127.0.0.1"));
		
		Result<String> res = runnable.run();
		
		assertFalse(res.hasError());
		
		System.out.println(res.getValue());
	}
	
	@Test
	public void testScriptingRunnable() {
		RunnableForInputId<Object> runnable = new ScriptingRunnable();
				
		runnable.setProgram("runnables/ping.js");
		runnable.setInputId(100L);

		Result<Object> res = runnable.run();
		
		if(res.hasError()) {
			res.getError().printStackTrace();
			fail();
		}

		assertNotNull(res.getValue());
		System.out.println(res.getValue().toString());
	}
}
