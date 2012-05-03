package pt.fe.up.diogo.costa.assembly;

import java.util.List;

import pt.fe.up.diogo.costa.data.IRegion;

public interface ICoordSystem {
	public String getId();
	public List<IRegion> getSequence(Mapping m, long startPosition, long endPosition);
	public boolean isSequenceLevel();
}
