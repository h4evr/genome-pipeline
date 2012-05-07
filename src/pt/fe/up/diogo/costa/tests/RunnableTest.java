package pt.fe.up.diogo.costa.tests;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import pt.fe.up.diogo.costa.Result;
import pt.fe.up.diogo.costa.runnable.IRunnable;
import pt.fe.up.diogo.costa.runnable.SimpleRunnable;


public class RunnableTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	
	}

	@Test
	public void testRunnable() {
		IRunnable<String> runnable = new SimpleRunnable();
		
		Assert.assertTrue(runnable.fromString("ping -n 1 127.0.0.1"));
		
		Result<String> res = runnable.run();
		
		Assert.assertFalse(res.hasError());
		
		System.out.println(res.getValue());
	}
}
