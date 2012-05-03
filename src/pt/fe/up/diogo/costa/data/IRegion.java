package pt.fe.up.diogo.costa.data;

import java.util.List;

public interface IRegion {
	public long getStartPosition();
	public void setStartPosition(long position);
	public long getEndPosition();
	public void setEndPosition(long position);
	public String getSequence();
	public List<IRegion> getChunks();
}
