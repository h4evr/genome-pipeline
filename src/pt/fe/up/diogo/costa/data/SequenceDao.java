package pt.fe.up.diogo.costa.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import pt.fe.up.diogo.costa.db.Database;
import pt.fe.up.diogo.costa.utils.SequenceUtils;

public class SequenceDao {
	private static SequenceDao instance = null;
	
	public static SequenceDao getInstance() {
		if(instance == null)
			instance = new SequenceDao();
		return instance;
	}
	
	public Sequence getSequenceById(int id) throws SQLException {
		Sequence j = null;
		
		Connection conn = Database.getConnection();
		PreparedStatement ps = 
				conn.prepareStatement("SELECT sequence_id, sequence, length " +
									  "FROM sequence WHERE sequence_id = ?");
		ps.setInt(1, id);
		
	 	ResultSet rs = ps.executeQuery();
	 	
	 	if(rs.next()) {
	 		String seq = SequenceUtils.decompressSequence(rs.getBytes(2)).substring(0, (int)rs.getLong(3));
	 		j = SequenceUtils.processSequence(seq);
	 		j.setId(rs.getInt(1));
	 		j.setStartPosition(0);
	 		j.setEndPosition(rs.getLong(3));
	 	}
	 	
	 	rs.close();
		return j;
	}
	
	public boolean saveSequence(Sequence s) throws SQLException {
		Connection conn = Database.getConnection();
		PreparedStatement ps;
		
		if(s.getId() == 0) {
			ps = conn.prepareStatement("INSERT INTO sequence (sequence, length) " +
				 					   "VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
		} else {
			ps = conn.prepareStatement("UPDATE sequence SET sequence = ?, length = ? " +
					   					"WHERE sequence_id = ?");
			ps.setInt(3, s.getId());
		}
		
		ps.setBytes(1, SequenceUtils.compressSequence(s.getSequence()));
		ps.setLong(2, s.getLength());
		
	 	if(ps.executeUpdate() > 0) {
	 		if(s.getId() == 0) {
		 		ResultSet rsGk = ps.getGeneratedKeys();
				if(rsGk.next()) {
					s.setId(rsGk.getInt(1));
				}
	 		}
	 		ps.close();
	 	} else {
	 		ps.close();
	 		return false;
	 	}
	 	
		return true;
	}
	
	public boolean deleteSequence(Sequence j) throws SQLException {
		return deleteSequence(j.getId());
	}
	
	public boolean deleteSequence(int id) throws SQLException {
		if(id == 0)
			return false;
		
		Connection conn = Database.getConnection();
		PreparedStatement ps;
		
		ps = conn.prepareStatement("DELETE FROM sequence WHERE sequence_id = ?");
		ps.setInt(1, id);
				
		return ps.executeUpdate() > 0;
	}
	
}
