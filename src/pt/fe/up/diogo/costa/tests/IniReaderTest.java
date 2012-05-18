package pt.fe.up.diogo.costa.tests;

import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import pt.fe.up.diogo.costa.config.IniReader;

public class IniReaderTest {
	@Test
	public void testReadFile() {
		String fileName = "job1.ini";
		
		IniReader res = IniReader.readFile(fileName);
		
		Map<String, String> sectionGeneral = res.get("general");
		Assert.assertTrue(sectionGeneral.get("Name") != null);
		Assert.assertTrue(sectionGeneral.get("Name").equals("Job 1"));
		
		Map<String, String> sectionRunnables = res.get("runnables");
		
		Assert.assertTrue(sectionRunnables.get("Job1") != null);
		Assert.assertTrue(sectionRunnables.get("Job1").equals("RepeatMasker"));
		
		Assert.assertTrue(sectionRunnables.get("Job2") != null);
		Assert.assertTrue(sectionRunnables.get("Job2").equals("GenScan"));
		
		Assert.assertTrue(res.get("RepeatMasker") != null);
		
		Assert.assertTrue(res.get("GenScan") != null);
	}
}
