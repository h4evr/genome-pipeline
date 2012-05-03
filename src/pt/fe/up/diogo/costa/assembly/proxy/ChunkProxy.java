package pt.fe.up.diogo.costa.assembly.proxy;

import pt.fe.up.diogo.costa.data.Chunk;

public class ChunkProxy extends Chunk {
	private long startPosition = 0;
	private long endPosition = 0;
	
	private Chunk chunk = null;
	
	public ChunkProxy() {
		
	}
	
	public ChunkProxy(Chunk c) {
		this.chunk = c;
		startPosition = c.getStartPosition();
		endPosition = c.getEndPosition();
	}
	
	public ChunkProxy(Chunk c, long startPosition, long endPosition) {
		this.chunk = c;
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
		
		if(chunk != null) {
			if(startPosition < chunk.getStartPosition()) {
				startPosition = chunk.getStartPosition();
			}
			
			if(startPosition > chunk.getEndPosition()) {
				startPosition = chunk.getEndPosition();
			}

			if(startPosition > endPosition) {
				startPosition = endPosition;
			}
		}
	}

	@Override
	public long getEndPosition() {
		return endPosition;
	}

	@Override
	public void setEndPosition(long position) {
		endPosition = position;
		
		if(chunk != null) {
			if(endPosition > chunk.getEndPosition()) {
				endPosition = chunk.getEndPosition();
			}
			
			if(endPosition < chunk.getStartPosition()) {
				endPosition = chunk.getStartPosition();
			}
			
			if(endPosition < startPosition) {
				endPosition = startPosition;
			}
		}
	}
	
	@Override
	public String getSequence() {
		if(chunk == null)
			return null;
		
		return chunk.getSequence().substring((int)(startPosition - chunk.getStartPosition()), 
										     (int)(endPosition - chunk.getStartPosition()));
	}

	public Chunk getChunk() {
		return chunk;
	}

	public void setChunk(Chunk chunk) {
		this.chunk = chunk;
	}
}
