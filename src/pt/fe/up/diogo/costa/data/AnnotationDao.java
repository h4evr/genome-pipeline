package pt.fe.up.diogo.costa.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import pt.fe.up.diogo.costa.db.Database;

public class AnnotationDao {
	private static AnnotationDao instance = null;
	
	public AnnotationDao getInstance() {
		if(instance == null)
			instance = new AnnotationDao();
		return instance;
	}
	
	public Annotation[] getAnnotations(Chunk j) throws SQLException {
		List<Annotation> res;
		
		Connection conn = Database.getConnection();
		PreparedStatement ps = 
				conn.prepareStatement("SELECT annotation_id, sequence_id, start, end, label, content " +
									  "FROM annotation WHERE sequence_id = ?");
		ps.setInt(1, j.getId());
		
	 	ResultSet rs = ps.executeQuery();
	 	
	 	res = new ArrayList<Annotation>();
	 	
	 	while(rs.next()) {
	 		Annotation a = new Annotation();
	 		a.setId(rs.getInt(1));
	 		a.setSequenceId(rs.getInt(2));
	 		a.setStartPosition(rs.getLong(3));
	 		a.setEndPosition(rs.getLong(4));
	 		a.setLabel(rs.getString(5));
	 		a.setContent(rs.getString(6));
	 		res.add(a);
	 	}
	 	
	 	rs.close();
	 	
		return (Annotation[])res.toArray();
	}
	
	public boolean saveAnnotation(Annotation j) throws SQLException {
		Connection conn = Database.getConnection();
		PreparedStatement ps;
		
		if(j.getId() == 0) {
			ps = conn.prepareStatement("INSERT INTO annotation (sequence_id, start, end, label, content) " +
				 					   "VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		} else {
			ps = conn.prepareStatement("UPDATE annotation SET sequence_id = ?, start = ?, end = ?, label = ?, content = ? " +
					   					"WHERE annotation_id = ?");
			ps.setInt(6, j.getId());
		}
		
		ps.setInt(1, j.getSequenceId());
		ps.setLong(2, j.getStartPosition());
		ps.setLong(3, j.getEndPosition());
		ps.setString(4, j.getLabel());
		ps.setString(5, j.getContent());
		
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
	
	public boolean deleteAnnotation(Annotation j) throws SQLException {
		return deleteAnnotation(j.getId());
	}
	
	public boolean deleteAnnotation(int id) throws SQLException {
		if(id == 0)
			return false;
		
		Connection conn = Database.getConnection();
		PreparedStatement ps;
		
		ps = conn.prepareStatement("DELETE FROM annotation WHERE annotation_id = ?");
		
		return ps.executeUpdate() > 0;
	}
}
