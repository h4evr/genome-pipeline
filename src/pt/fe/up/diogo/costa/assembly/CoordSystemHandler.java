package pt.fe.up.diogo.costa.assembly;

import java.util.ArrayList;
import java.util.List;

import pt.fe.up.diogo.costa.assembly.proxy.ChunkProxy;
import pt.fe.up.diogo.costa.data.Chunk;
import pt.fe.up.diogo.costa.data.IRegion;
import pt.fe.up.diogo.costa.data.Sequence;
import pt.fe.up.diogo.costa.exception.InvalidCoordinateException;
import pt.fe.up.diogo.costa.exception.MappingNotFoundException;

public class CoordSystemHandler {
	private static CoordSystemHandler instance = null;
	private List<ICoordSystem> coordSystems;
	
	public static CoordSystemHandler getInstance() {
		if(instance == null)
			instance = new CoordSystemHandler();
		return instance;
	}
	
	public List<ICoordSystem> getCoordSystems() {
		if(coordSystems == null)
			coordSystems = new ArrayList<ICoordSystem>();
		return coordSystems;
	}

	public void setCoordSystems(List<ICoordSystem> coordSystems) {
		this.coordSystems = coordSystems;
	}
	
	public List<IRegion> resolve(String position) throws InvalidCoordinateException, MappingNotFoundException {
		if(position == null)
			throw new InvalidCoordinateException();
		
		String[] pos = position.split(":");
		
		if(pos.length != 3)
			throw new InvalidCoordinateException();
		
		long startPosition = 0;
		long endPosition = 0;
		
		try {
			startPosition = Long.parseLong(pos[1]);
			endPosition = Long.parseLong(pos[2]);
		} catch(NumberFormatException e) {
			throw new InvalidCoordinateException();
		}
		
		if(startPosition > endPosition) {
			long tmp = endPosition;
			endPosition = startPosition;
			startPosition = tmp;
		}
		
		List<Mapping> mappings = null;
		
		if(Assembly.getInstance().getMappings().containsKey(pos[0])) {
			mappings = Assembly.getInstance().getMappings().get(pos[0]);
		}
		
		if(mappings == null) {
			throw new MappingNotFoundException();
		}
		
		List<IRegion> res = new ArrayList<IRegion>(mappings.size());
		
		for(Mapping m : mappings) {
			if((m.getSource().getStartPosition() >= startPosition && m.getSource().getStartPosition() < endPosition) ||
			   (m.getSource().getEndPosition() > startPosition && m.getSource().getEndPosition() <= endPosition)) {
				for(ICoordSystem cs : getCoordSystems()) {
					if(cs.getId().equals(m.getCoordSystemId())) {
						List<IRegion> newRes = cs.getSequence(m, startPosition, endPosition);
						if(newRes != null) {
							res.addAll(newRes);
						}
					}
				}
			}
		}

		return res;
	}
	
	public IRegion createProxy(IRegion source, IRegion destination, long startPosition, long endPosition) {
		if(destination instanceof Chunk) {
			long sP = startPosition - source.getStartPosition() + destination.getStartPosition();
			long len = endPosition - startPosition;
			
			ChunkProxy p = new ChunkProxy((Chunk)destination);
			p.setStartPosition(sP);
			p.setEndPosition(sP + len);
			
			return p;
		} else { 
			return null;
		}
	}
	
	public Sequence buildSequence(List<IRegion> regions) {
		Sequence s = new Sequence();
		
		s.setChunks(new ArrayList<IRegion>());
		
		for(IRegion r : regions) {
			List<IRegion> tmp = r.getChunks();
			
			if(tmp != null) {
				s.getChunks().addAll(tmp);
			}
		}
		
		return s;
	}
}
