package pt.fe.up.diogo.costa.assembly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.fe.up.diogo.costa.data.IRegion;
import pt.fe.up.diogo.costa.data.Sequence;
import pt.fe.up.diogo.costa.data.StubRegion;

public class Assembly {
	private static Assembly instance;
	private Map<String, List<Mapping>> mappings;
	private Map<String, List<Mapping>> mappingsByCoordSystem;

	public void addMapping(String name, Mapping m) {
		if(!getMappings().containsKey(name)) {
			mappings.put(name, new ArrayList<Mapping>());
		}
		
		if(!getMappingsByCoordSystem().containsKey(m.getCoordSystemId())) {
			mappingsByCoordSystem.put(m.getCoordSystemId(), new ArrayList<Mapping>());
		}
		
		mappings.get(name).add(m);
		mappingsByCoordSystem.get(m.getCoordSystemId()).add(m);
	}
	
	public Map<String, List<Mapping>> getMappings() {
		if(mappings == null)
			mappings = new HashMap<String, List<Mapping>>();
		return mappings;
	}

	public void setMappings(Map<String, List<Mapping>> mappings) {
		this.mappings = mappings;
	}

	public static Assembly getInstance() {
		if(instance == null)
			instance = new Assembly();
		return instance;
	}
	
	public void setMappingsByCoordSystem(Map<String, List<Mapping>> mappingsByCoordSystem) {
		this.mappingsByCoordSystem = mappingsByCoordSystem;
	}
	
	public Map<String, List<Mapping>> getMappingsByCoordSystem() {
		if(mappingsByCoordSystem == null)
			mappingsByCoordSystem = new HashMap<String, List<Mapping>>();
		return mappingsByCoordSystem;
	}
	
	public List<Mapping> getMappingsByCoordSystem(String coordSystem) {
		if(getMappingsByCoordSystem().containsKey(coordSystem))
			return mappingsByCoordSystem.get(coordSystem);
		else
			return null;
	}

	public List<Mapping> getMappingsByCoordSystem(String coordSystem, long startPosition, long endPosition) {
		List<Mapping> ms = getMappingsByCoordSystem(coordSystem);
		List<Mapping> res = new ArrayList<Mapping>(ms.size());
		
		for(Mapping m : ms) {
			if((m.getSource().getStartPosition() >= startPosition && m.getSource().getStartPosition() < endPosition) ||
			   (m.getSource().getEndPosition() > startPosition && m.getSource().getEndPosition() <= endPosition)) {
				res.add(m);
			}
		}
		
		return res;
	}
	
	public void createMappings(List<Sequence> sequences, String srcCoordSystemId, String destCoordSystemId) {
		long destId = 0;
		long srcId = 0;
		
		for(Sequence s : sequences) {
			for(IRegion c : s.getChunks()) {
				IRegion destRegion = new StubRegion(c.getStartPosition(), c.getEndPosition());
				Mapping dest = new Mapping();
				dest.setSource(destRegion);
				dest.setDestination(c);
				dest.setCoordSystemId(destCoordSystemId);
				
				addMapping(destCoordSystemId + Long.toString(++destId), dest);
				
				Mapping src = new Mapping();
				src.setCoordSystemId(srcCoordSystemId);
				src.setSource(new StubRegion(c.getStartPosition(), c.getEndPosition()));
				src.setDestination(destRegion);
				
				addMapping(destCoordSystemId + Long.toString(++srcId), src);
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		for(String cs : mappingsByCoordSystem.keySet()) {
			for(Mapping m : mappingsByCoordSystem.get(cs)) {
				sb.append(m.getCoordSystemId()).append(":")
				  .append(m.getSource().getStartPosition()).append(":")
				  .append(m.getSource().getEndPosition())
				  .append(" -> ");
				
				sb.append(m.getDestination().getStartPosition()).append(":")
				  .append(m.getDestination().getEndPosition());
				
				sb.append("\n");
			}
		}
		
		return sb.toString();
	}
}
