package pt.fe.up.diogo.costa.data;

import java.util.List;

public class Annotation implements IRegion {
	private int id;
	private int sequenceId;
	private long startPosition;
	private long endPosition;
	private String label;
	private String content;
	
	public int getSequenceId() {
		return sequenceId;
	}

	public void setSequenceId(int sequenceId) {
		this.sequenceId = sequenceId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

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

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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
