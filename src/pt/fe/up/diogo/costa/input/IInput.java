package pt.fe.up.diogo.costa.input;

import pt.fe.up.diogo.costa.data.Sequence;

public interface IInput {
	public boolean open(String filename);
	public Sequence readNextSequence();
	public void close();
}
