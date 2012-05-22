package pt.fe.up.diogo.costa.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import pt.fe.up.diogo.costa.db.Database;
import pt.fe.up.diogo.costa.utils.StringUtils;

public class ChunkDao {
	private static ChunkDao instance = null;
	
	public static ChunkDao getInstance() {
		if(instance == null)
			instance = new ChunkDao();
		return instance;
	}
	
	public Chunk getChunkById(int id) throws SQLException {
		Chunk j = null;
		
		Connection conn = Database.getConnection();
		PreparedStatement ps = 
				conn.prepareStatement("SELECT sequence_id, sequence, start, end " +
									  "FROM sequence WHERE sequence_id = ?");
		ps.setInt(1, id);
		
	 	ResultSet rs = ps.executeQuery();
	 	
	 	if(rs.next()) {
	 		j = new Chunk();
	 		j.setId(rs.getInt(1));
	 		j.setSequence(StringUtils.decodeRLE(rs.getString(2)));
	 		j.setStartPosition(rs.getLong(3));
	 		j.setEndPosition(rs.getLong(4));
	 	}
	 	
	 	rs.close();
		return j;
	}
	
	public boolean saveChunk(Chunk j) throws SQLException {
		Connection conn = Database.getConnection();
		PreparedStatement ps;
		
		if(j.getId() == 0) {
			ps = conn.prepareStatement("INSERT INTO sequence (sequence, start, end) " +
				 					   "VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		} else {
			ps = conn.prepareStatement("UPDATE sequence SET sequence = ?, start = ?, end = ? " +
					   					"WHERE sequence_id = ?");
			ps.setInt(4, j.getId());
		}
		
		ps.setString(1, StringUtils.encodeRLE(j.getSequence()));
		ps.setLong(2, j.getStartPosition());
		ps.setLong(3, j.getEndPosition());
		
	 	if(ps.executeUpdate() > 0) {
	 		if(j.getId() == 0) {
		 		ResultSet rsGk = ps.getGeneratedKeys();
				if(rsGk.next()) {
					j.setId(rsGk.getInt(1));
				}
	 		}
	 		ps.close();
	 	} else {
	 		ps.close();
	 		return false;
	 	}
	 	
		return true;
	}
	
	public boolean deleteChunk(Chunk j) throws SQLException {
		return deleteChunk(j.getId());
	}
	
	public boolean deleteChunk(int id) throws SQLException {
		if(id == 0)
			return false;
		
		Connection conn = Database.getConnection();
		PreparedStatement ps;
		
		ps = conn.prepareStatement("DELETE FROM sequence WHERE sequence_id = ?");
		ps.setInt(1, id);
				
		return ps.executeUpdate() > 0;
	}
	
}
