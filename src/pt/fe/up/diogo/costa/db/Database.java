package pt.fe.up.diogo.costa.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Database {
	
	private static Connection conn = null;
	private static Properties connectionProps = null;
	
	public static Properties getProperties() {
		if(connectionProps == null) {
			connectionProps = new Properties();
			connectionProps.put("user", "pipeline");
			connectionProps.put("password", "qwerty123");
			connectionProps.put("host", "127.0.0.1");
			connectionProps.put("port", 3306);
			connectionProps.put("db", "genome_pipeline");
		}
		
		return connectionProps;
	}
	
	public static Connection getConnection() {
		if(conn == null) {
			connectionProps = getProperties();
			
			Properties props = new Properties();
			props.put("user", connectionProps.get("user"));
			props.put("password", connectionProps.get("password"));
			
			try {
				conn = DriverManager.getConnection("jdbc:mysql://" + 
												   connectionProps.get("host") + ":" + 
												   connectionProps.get("port") + "/" +
												   connectionProps.get("db"), 
												   props);
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		return conn;
	}
	
	public static void close() {
		if(conn != null) {
			try {
				conn.close();
				conn = null;
			} catch(SQLException e) {}
		}
	}
}
