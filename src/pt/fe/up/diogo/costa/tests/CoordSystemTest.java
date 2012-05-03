package pt.fe.up.diogo.costa.tests;

import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import pt.fe.up.diogo.costa.assembly.Assembly;
import pt.fe.up.diogo.costa.assembly.CoordSystemHandler;
import pt.fe.up.diogo.costa.assembly.Mapping;
import pt.fe.up.diogo.costa.assembly.coordsystem.CoordSystem;
import pt.fe.up.diogo.costa.assembly.proxy.ChunkProxy;
import pt.fe.up.diogo.costa.data.Chunk;
import pt.fe.up.diogo.costa.data.IRegion;
import pt.fe.up.diogo.costa.data.Sequence;
import pt.fe.up.diogo.costa.data.StubRegion;
import pt.fe.up.diogo.costa.exception.InvalidCoordinateException;
import pt.fe.up.diogo.costa.exception.MappingNotFoundException;
import pt.fe.up.diogo.costa.input.FastaInput;

public class CoordSystemTest {
	@BeforeClass
	public static void setup() {
		CoordSystemHandler.getInstance().getCoordSystems().add(new CoordSystem("chunk", true));
		CoordSystemHandler.getInstance().getCoordSystems().add(new CoordSystem("scaffold"));
	}
	
	@Test
	public void testChunkProxy() {
		
		Chunk c = new Chunk();
		c.setSequence("ATGATGATGATGATG");
		c.setStartPosition(120);
		c.setEndPosition(c.getStartPosition() + c.getSequence().length());
		long oldEndPos = c.getEndPosition();
		
		ChunkProxy p = new ChunkProxy(c);
		
		p.setStartPosition(100);
		Assert.assertTrue(p.getStartPosition() == 120);
		
		p.setStartPosition(p.getEndPosition() + 1);
		Assert.assertTrue(p.getStartPosition() == p.getEndPosition());
		
		p.setStartPosition(121);
		
		p.setEndPosition(p.getStartPosition() - 1);
		Assert.assertTrue(p.getEndPosition() == p.getStartPosition());
		
		p.setEndPosition(oldEndPos + 1);
		Assert.assertTrue(p.getEndPosition() == oldEndPos);
		
		p.setEndPosition(p.getStartPosition() + 6);
				
		Assert.assertEquals("TGATGA", p.getSequence());
	}
	
	@Test
	public void testCoordSystem() {
		Chunk c = new Chunk();
		c.setSequence("ATGATGATGATGATG");
		c.setStartPosition(120);
		c.setEndPosition(c.getStartPosition() + c.getSequence().length());
		
		Mapping m = new Mapping();
		m.setCoordSystemId("chunk");
		m.setSource(new StubRegion(1000, 1000 + c.getSequence().length()));
		m.setDestination(c);
		
		Assembly.getInstance().addMapping("c1", m);
		
		
		Mapping m2 = new Mapping();
		m2.setCoordSystemId("scaffold");
		m2.setSource(new StubRegion(1, 1 + c.getSequence().length()));
		m2.setDestination(new ChunkProxy(c));
		
		Assembly.getInstance().addMapping("s1", m2);

		List<IRegion> regions;
		
		try {
			regions = CoordSystemHandler.getInstance().resolve("c1:1000:1003");
			
			Assert.assertNotNull(regions);
			Assert.assertTrue(regions.size() == 1);
			Assert.assertTrue(regions.get(0).getSequence().equals("ATG"));
		} catch (InvalidCoordinateException e) {
			Assert.fail();
		} catch (MappingNotFoundException e) {
			Assert.fail();
		}
		
		try {
			regions = CoordSystemHandler.getInstance().resolve("s1:1:30");
			
			Assert.assertNotNull(regions);
			Assert.assertTrue(regions.size() == 1);
			
			System.out.println(CoordSystemHandler.getInstance().buildSequence(regions).getSequence());
			
		} catch (InvalidCoordinateException e) {
			Assert.fail();
		} catch (MappingNotFoundException e) {
			Assert.fail();
		}
		
		try {
			regions = CoordSystemHandler.getInstance().resolve("c1:abc:1003");
			Assert.fail();
		} catch (InvalidCoordinateException e) {
		} catch (MappingNotFoundException e) {
			Assert.fail();
		}
		
		try {
			regions = CoordSystemHandler.getInstance().resolve("c100:1000:1003");
			Assert.fail();
		} catch (InvalidCoordinateException e) {
			Assert.fail();
		} catch (MappingNotFoundException e) {
		}
	}
	
	@Test
	public void testCreateMappings() {
		FastaInput in = new FastaInput();
		Sequence seq = null;
		
		Assert.assertTrue(in.open("test.fa"));
		
		List<Sequence> seqs = new LinkedList<Sequence>();
		
		try {
			while((seq = in.readNextSequence()) != null) {
				seqs.add(seq);
			}
		} finally {
			in.close();
		}
		
		Assembly.getInstance().createMappings(seqs, "scaffold", "chunk");
		
		System.out.println(Assembly.getInstance().toString());
	}
}
