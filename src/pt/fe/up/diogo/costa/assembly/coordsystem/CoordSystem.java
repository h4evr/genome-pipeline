package pt.fe.up.diogo.costa.assembly.coordsystem;

import java.util.ArrayList;
import java.util.List;

import pt.fe.up.diogo.costa.assembly.Assembly;
import pt.fe.up.diogo.costa.assembly.CoordSystemHandler;
import pt.fe.up.diogo.costa.assembly.ICoordSystem;
import pt.fe.up.diogo.costa.assembly.Mapping;
import pt.fe.up.diogo.costa.data.IRegion;

public class CoordSystem implements ICoordSystem {
	private String id;
	private boolean seqLevel;
	
	public CoordSystem() {
		seqLevel = false;
		id = "";
	}
	
	public CoordSystem(String id) {
		setId(id);
		seqLevel = false;
	}
	
	public CoordSystem(String id, boolean seqLevel) {
		setId(id);
		this.seqLevel = seqLevel;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public List<IRegion> getSequence(Mapping m, long startPosition, long endPosition) {
		if(!this.seqLevel) {
			List<Mapping> mappings = Assembly.getInstance().getMappingsByCoordSystem(id, startPosition, endPosition);
			List<IRegion> chunks = new ArrayList<IRegion>(mappings.size());
			
			for(Mapping m2 : mappings) {
				IRegion proxy = CoordSystemHandler.getInstance().createProxy(m2.getSource(), m2.getDestination(), startPosition, endPosition);
				if(proxy != null) {
					chunks.add(proxy);
				}
			}
			
			if(chunks.size() == 0)
				return null;
			
			return chunks;
		} else {
			IRegion p = CoordSystemHandler.getInstance().createProxy(m.getSource(), m.getDestination(), startPosition, endPosition);
			
			if(p == null)
				return null;
			
			List<IRegion> regs = new ArrayList<IRegion>();
			regs.add(p);
			
			return regs;
		}
	}
	
	public boolean isSequenceLevel() {
		return seqLevel;
	}
	
	public void setSequenceLevel(boolean seqLevel) {
		this.seqLevel = seqLevel;
	}
}
