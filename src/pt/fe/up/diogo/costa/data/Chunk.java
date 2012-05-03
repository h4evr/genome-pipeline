package pt.fe.up.diogo.costa.data;

import java.util.ArrayList;
import java.util.List;

public class Chunk implements IRegion {
	private long startPosition;
	private long endPosition;
	private String seq;

	public long getStartPosition() {
		return startPosition;
	}
	
	public void setStartPosition(long startPosition) {
		this.startPosition = startPosition;
	}
	
	public long getEndPosition() {
		return endPosition;
	}
	
	public void setEndPosition(long endPosition) {
		this.endPosition = endPosition;
	}

	public void setSequence(String seq) {
		this.seq = seq;
	}
	
	@Override
	public String getSequence() {
		return this.seq;
	}

	@Override
	public List<IRegion> getChunks() {
		List<IRegion> me = new ArrayList<IRegion>(1);
		me.add(this);
		return me;
	}
}
