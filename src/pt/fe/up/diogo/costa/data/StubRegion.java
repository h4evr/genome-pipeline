package pt.fe.up.diogo.costa.data;

import java.util.List;

public class StubRegion implements IRegion {
	private long startPosition;
	private long endPosition;
	
	public StubRegion() {
		
	}
	
	public StubRegion(long startPosition, long endPosition) {
		setStartPosition(startPosition);
		setEndPosition(endPosition);
	}
	
	@Override
	public long getStartPosition() {
		return startPosition;
	}

	@Override
	public void setStartPosition(long position) {
		startPosition = position;
	}

	@Override
	public long getEndPosition() {
		return endPosition;
	}

	@Override
	public void setEndPosition(long position) {
		endPosition = position;
	}

	@Override
	public String getSequence() {
		return "";
	}

	@Override
	public List<IRegion> getChunks() {
		return null;
	}
}
