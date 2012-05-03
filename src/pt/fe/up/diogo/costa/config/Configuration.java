package pt.fe.up.diogo.costa.config;

public class Configuration implements IConfiguration {
	
	private static IConfiguration config = null;
	
	public static IConfiguration getInstance() {
		if(config == null)
			config = new Configuration();
		return config;
	}
	
	@Override
	public long getChunkSize() {
		return 10000;
	}
}
