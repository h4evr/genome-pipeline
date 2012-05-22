package pt.fe.up.diogo.costa.tests;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.fe.up.diogo.costa.data.Chunk;
import pt.fe.up.diogo.costa.data.ChunkDao;

public class ChunkDaoTest {
	private static int newChunkId;
	private final String seq = "TGTGTGCAGATTGTGATTGCGAACGAAAATCATATTTTCTGTTATTGTGGCAAAATGAGA";
	
	@Before
	public void testCreateChunk() {
		ChunkDao dao = ChunkDao.getInstance();
		
		Chunk c1 = new Chunk();
		c1.setSequence(seq);
		c1.setStartPosition(10);
		c1.setEndPosition(c1.getStartPosition() + c1.getSequence().length());
		
		try {
			assertTrue(dao.saveChunk(c1));
			assertTrue(c1.getId() > 0);
			newChunkId = c1.getId();
		} catch (SQLException e) {
			newChunkId = -1;
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetChunk() {
		assertTrue(newChunkId > 0);
		
		ChunkDao dao = ChunkDao.getInstance();
		
		Chunk c1 = null;
		
		try {
			c1 = dao.getChunkById(newChunkId);
		} catch (SQLException e) {
			fail(e.getMessage());
		}
		
		assertNotNull(c1);
		assertTrue(c1.getId() > 0);
		assertEquals(seq, c1.getSequence());
		assertEquals(10, c1.getStartPosition());
		assertEquals(10 + seq.length(), c1.getEndPosition());
	}
	
	@After
	public void testDeleteChunk() {
		assertTrue(newChunkId > 0);
		
		ChunkDao dao = ChunkDao.getInstance();		
		try {
			assertTrue(dao.deleteChunk(newChunkId));
		} catch (SQLException e) {
			fail(e.getMessage());
		}		
		
		Chunk c1 = null;
		
		try {
			c1 = dao.getChunkById(newChunkId);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		assertNull(c1);
	}
}
