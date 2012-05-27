package pt.fe.up.diogo.costa.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sequence implements IRegion {
	private Integer id = 0;
	private String name;
	private Map<String, String> attributes;
	private List<IRegion> chunks;
	private long length;
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Map<String, String> getAttributes() {
		if(attributes == null)
			attributes = new HashMap<String, String>();
		
		return attributes;
	}
	
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
	
	public List<IRegion> getChunks() {
		return chunks;
	}
	
	public void setChunks(List<IRegion> chunks) {
		this.chunks = chunks;
	}
	
	public long getLength() {
		return length;
	}
	
	public void setLength(long length) {
		this.length = length;
	}

	@Override
	public long getStartPosition() {
		return 0;
	}

	@Override
	public void setStartPosition(long position) {
	}

	@Override
	public long getEndPosition() {
		return this.getLength();
	}

	@Override
	public void setEndPosition(long position) {
	}
	
	public String getSequence() {
		if(getChunks() == null || getChunks().size() == 0)
			return "";
		
		long lastPos = getChunks().get(0).getStartPosition();
		
		StringBuilder b = new StringBuilder();
		
		for(IRegion c : getChunks()) {
			if(c.getStartPosition() - lastPos > 1) {
				for(long i = c.getStartPosition() - lastPos - 1; i > 0; --i) {
					b.append("-");
				}
			}
			
			b.append(c.getSequence());
			
			lastPos = c.getEndPosition();
		}
		
		return b.toString();
	}
}
