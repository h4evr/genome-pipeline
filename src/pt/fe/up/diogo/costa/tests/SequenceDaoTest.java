package pt.fe.up.diogo.costa.tests;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.fe.up.diogo.costa.data.Sequence;
import pt.fe.up.diogo.costa.data.SequenceDao;
import pt.fe.up.diogo.costa.utils.SequenceUtils;

public class SequenceDaoTest {
	private static int newSequenceId;
	private final String seq = "TGTGTGCAGATTGTGATTGCGAACGAAAATCATATTTTCTGTTATTGTGGCAAAATGAGA";
	
	@Before
	public void testCreateSequence() {
		SequenceDao dao = SequenceDao.getInstance();
		
		Sequence c1 = SequenceUtils.processSequence(seq);
		c1.setStartPosition(0);
		c1.setEndPosition(c1.getSequence().length());
		
		try {
			assertTrue(dao.saveSequence(c1));
			assertTrue(c1.getId() > 0);
			newSequenceId = c1.getId();
		} catch (SQLException e) {
			newSequenceId = -1;
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetSequence() {
		assertTrue(newSequenceId > 0);
		
		SequenceDao dao = SequenceDao.getInstance();
		
		Sequence c1 = null;
		
		try {
			c1 = dao.getSequenceById(newSequenceId);
		} catch (SQLException e) {
			fail(e.getMessage());
		}
		
		assertNotNull(c1);
		assertTrue(c1.getId() > 0);
		assertEquals(seq, c1.getSequence());
		assertEquals(0, c1.getStartPosition());
		assertEquals(seq.length(), c1.getEndPosition());
	}
	
	@After
	public void testDeleteSequence() {
		assertTrue(newSequenceId > 0);
		
		SequenceDao dao = SequenceDao.getInstance();		
		try {
			assertTrue(dao.deleteSequence(newSequenceId));
		} catch (SQLException e) {
			fail(e.getMessage());
		}		
		
		Sequence c1 = null;
		
		try {
			c1 = dao.getSequenceById(newSequenceId);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		assertNull(c1);
	}
}
