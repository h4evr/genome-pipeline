package pt.fe.up.diogo.costa.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import pt.fe.up.diogo.costa.db.Database;
import pt.fe.up.diogo.costa.job.Job;
import pt.fe.up.diogo.costa.job.JobStatus;

public class JobDao {
	private static JobDao instance = null;
	
	public Job createOrUpdate(Job j) throws SQLException {		
		Connection conn = Database.getConnection();
		PreparedStatement ps = 
				conn.prepareStatement("SELECT job_id, input_id, runnable_id, status, last_update_date " +
									  "FROM job WHERE input_id = ? AND runnable_id = ?");
		ps.setLong(1, j.getInputId());
		ps.setLong(2, j.getRunnableId());
		
	 	ResultSet rs = ps.executeQuery();
	 	
	 	if(rs.next()) {
	 		j.setId(rs.getInt(1));
	 		j.setInputId(rs.getLong(2));
	 		j.setRunnableId(rs.getInt(3));
	 		j.setStatus(JobStatus.valueOf(rs.getString(4)));
	 		j.setLastRun(rs.getDate(5));
	 	} else {
	 		j.setId(0L);
	 		saveJob(j);
	 	}
	 	
	 	rs.close();
		
		return j;
	}
	
	public Job getJobById(long jobId) throws SQLException {		
		Job j = null;
		
		Connection conn = Database.getConnection();
		PreparedStatement ps = 
				conn.prepareStatement("SELECT job_id, input_id, runnable_id, status, last_update_date " +
									  "FROM job WHERE job_id = ?");
		ps.setLong(1, jobId);
		
	 	ResultSet rs = ps.executeQuery();
	 	
	 	if(rs.next()) {
	 		j = new Job();
	 		j.setId(rs.getInt(1));
	 		j.setInputId(rs.getLong(2));
	 		j.setRunnableId(rs.getInt(3));
	 		j.setStatus(JobStatus.valueOf(rs.getString(4)));
	 		j.setLastRun(rs.getDate(5));
	 	}
	 	
	 	rs.close();
		return j;
	}
	
	public List<Job> getJobsByInputId(int inputId) throws SQLException {		
		List<Job> jobs = new ArrayList<Job>();

		Connection conn = Database.getConnection();
		PreparedStatement ps = 
				conn.prepareStatement("SELECT job_id, input_id, runnable_id, status, last_update_date " +
									  "FROM job WHERE input_id = ?");
		ps.setInt(1, inputId);
		
	 	ResultSet rs = ps.executeQuery();
	 	
	 	if(rs.next()) {
	 		Job j = new Job();
	 		j.setId(rs.getInt(1));
	 		j.setInputId(rs.getLong(2));
	 		j.setRunnableId(rs.getInt(3));
	 		j.setStatus(JobStatus.valueOf(rs.getString(4)));
	 		j.setLastRun(rs.getDate(5));
	 		jobs.add(j);
	 	}
	 	
	 	rs.close();
		
		return jobs;
	}
	
	public List<Job> getJobsByRunnableId(int runnableId) throws SQLException {
		List<Job> jobs = new ArrayList<Job>();
		
		Connection conn = Database.getConnection();
		PreparedStatement ps = 
				conn.prepareStatement("SELECT job_id, input_id, runnable_id, status, last_update_date " +
									  "FROM job WHERE runnable_id = ?");
		ps.setInt(1, runnableId);
		
	 	ResultSet rs = ps.executeQuery();
	 	
	 	if(rs.next()) {
	 		Job j = new Job();
	 		j.setId(rs.getInt(1));
	 		j.setInputId(rs.getLong(2));
	 		j.setRunnableId(rs.getInt(3));
	 		j.setStatus(JobStatus.valueOf(rs.getString(4)));
	 		j.setLastRun(rs.getDate(5));
	 		jobs.add(j);
	 	}
	 	
	 	rs.close();
		
		return jobs;
	}
	
	@SuppressWarnings("deprecation")
	public boolean saveJob(Job j) throws SQLException {
		int res = 0;
		
		Connection conn = Database.getConnection();
		PreparedStatement ps;
		
		if(j.getId() > 0) {
			ps = conn.prepareStatement("UPDATE job SET input_id = ?, runnable_id = ?, status = ?, last_update_date = ? " +
  					   					"WHERE job_id = ?");

			ps.setLong(1, j.getInputId());
			ps.setInt(2, j.getRunnableId());
			ps.setString(3, j.getStatus().name());
			ps.setDate(4, new java.sql.Date(j.getLastRun().getDate()));
			ps.setLong(5, j.getId());
			res = ps.executeUpdate();
		} else {
			ps = conn.prepareStatement("INSERT INTO job (input_id, runnable_id, status, last_update_date) " +
				  					   "VALUES (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			ps.setLong(1, j.getInputId());
			ps.setInt(2, j.getRunnableId());
			ps.setString(3, j.getStatus().name());
			if(j.getLastRun() == null) {
				ps.setDate(4, null);
			} else {
				ps.setDate(4, new java.sql.Date(j.getLastRun().getDate()));
			}
			res = ps.executeUpdate();
			
			ResultSet rsGk = ps.getGeneratedKeys();
			if(rsGk.next()) {
				j.setId(rsGk.getLong(1));
			}
		}			

		return res > 0;
	}
	
	public void deleteAllJobs() throws SQLException {
		Connection conn = Database.getConnection();
		PreparedStatement ps;
		ps = conn.prepareStatement("DELETE FROM job");
		ps.executeUpdate();
	}
	
	public static JobDao getInstance() {
		if(instance == null)
			instance = new JobDao();
		return instance;
	}
}
