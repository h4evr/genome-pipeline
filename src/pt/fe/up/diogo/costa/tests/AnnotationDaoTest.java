package pt.fe.up.diogo.costa.tests;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.fe.up.diogo.costa.data.Annotation;
import pt.fe.up.diogo.costa.data.AnnotationDao;

public class AnnotationDaoTest {
	private static int newAnnotationId;

	@Before
	public void testCreateAnnotation() {
		AnnotationDao dao = AnnotationDao.getInstance();
		
		Annotation c1 = new Annotation();
		c1.setSequenceId(-1);
		c1.setStartPosition(10);
		c1.setEndPosition(20);
		c1.setLabel("Label1");
		c1.setContent("Content 1");
		
		try {
			assertTrue(dao.saveAnnotation(c1));
			assertTrue(c1.getId() > 0);
			newAnnotationId = c1.getId();
		} catch (SQLException e) {
			newAnnotationId = -1;
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetAnnotation() {
		AnnotationDao dao = AnnotationDao.getInstance();
		
		Annotation[] c1 = null;
				
		try {
			c1 = dao.getAnnotations(-1);
		} catch (SQLException e) {
			fail(e.getMessage());
		}
		
		assertNotNull(c1);
		assertTrue(c1.length > 0);
		
		assertEquals("Label1", c1[0].getLabel());
		assertEquals("Content 1", c1[0].getContent());
		
		
	}
	
	@After
	public void testDeleteAnnotation() {
		assertTrue(newAnnotationId > 0);
		
		AnnotationDao dao = AnnotationDao.getInstance();		
		try {
			assertTrue(dao.deleteAnnotation(newAnnotationId));
		} catch (SQLException e) {
			fail(e.getMessage());
		}		
		
		Annotation[] c1 = null;
		
		try {
			c1 = dao.getAnnotations(-1);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		assertNotNull(c1);
		assertTrue(c1.length == 0);
	}
}
