package pt.fe.up.diogo.costa.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class IniReader {	
	private Map<String, Map<String, String>> fileValues;
	
	public static IniReader readFile(String fileName) {
		IniReader res = new IniReader();

		File f = new File(fileName);

		String currentSectionName = "unspecified";
		Map<String, String> currentSession = new HashMap<String, String>();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(f));
			String line = "";
			
			while((line = reader.readLine()) != null) {
				if(line.startsWith("#"))
					continue;
				
				// Section
				if(line.startsWith("[")) {
					String sectionName = line.substring(1, line.lastIndexOf("]")).toLowerCase();
					
					if(currentSession.size() > 0)
						res.get(currentSectionName).putAll(currentSession);
					
					currentSectionName = sectionName;
					currentSession = new HashMap<String, String>();
				} else if(line.contains("=")) {
					String[] v = line.split("=");
					currentSession.put(v[0].trim(), v[1].trim());
				}
			}
			
			if(currentSession.size() > 0)
				res.get(currentSectionName).putAll(currentSession);
			
			reader.close();
		} catch (Exception e) {
			return null;
		} 
		
		return res;
	}
	
	public Map<String, String> get(String sectionName) {
		if(fileValues == null) {
			fileValues = new HashMap<String, Map<String, String>>();
		}
		
		if(!fileValues.containsKey(sectionName.toLowerCase())) {
			fileValues.put(sectionName.toLowerCase(), new HashMap<String, String>());
		}
		
		return fileValues.get(sectionName.toLowerCase());
	}
}
