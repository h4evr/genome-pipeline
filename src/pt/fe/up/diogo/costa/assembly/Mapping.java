package pt.fe.up.diogo.costa.assembly;

import pt.fe.up.diogo.costa.data.IRegion;

public class Mapping {
	public enum TYPE {
		GAP,
		SCAFFOLD
	}
	
	private String coordSystemId;
	private IRegion source;
	private IRegion destination;
	private TYPE type;

	public String getCoordSystemId() {
		return coordSystemId;
	}

	public void setCoordSystemId(String coordSystemId) {
		this.coordSystemId = coordSystemId;
	}

	public IRegion getDestination() {
		return destination;
	}

	public void setDestination(IRegion destination) {
		this.destination = destination;
	}

	public IRegion getSource() {
		return source;
	}

	public void setSource(IRegion source) {
		this.source = source;
	}

	public TYPE getType() {
		return type;
	}

	public void setType(TYPE type) {
		this.type = type;
	}
}
