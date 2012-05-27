package pt.fe.up.diogo.costa.input;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import pt.fe.up.diogo.costa.data.Sequence;
import pt.fe.up.diogo.costa.utils.SequenceUtils;

public class FastaInput implements IInput {
	private String lastLine = null;
	private BufferedReader reader = null;

	@Override
	public boolean open(String filename) {
		try {
			reader = new BufferedReader(new FileReader(new File(filename)));
		} catch (FileNotFoundException e) {
			return false;
		}
		
		return true;
	}

	@Override
	public Sequence readNextSequence() {
		Sequence seq = null;
		
		if(reader == null)
			return null;
		
		String line = "";
		StringBuilder sequenceContent = new StringBuilder();
		boolean readHeader = false;
		boolean processedSeq = false;
		
		try {
			while((line = (lastLine != null ? lastLine : reader.readLine())) != null) {
				lastLine = null;
				
				if(line.startsWith("#")) {
					continue;
				} else if(line.startsWith(">")) {
					if(!readHeader) {
						seq = new Sequence();
						SequenceUtils.processHeader(seq, line);
						readHeader = true;
					} else {
						SequenceUtils.processSequence(seq, sequenceContent.toString());
						processedSeq = true;
						lastLine = line;
						break;
					}
				} else {
					sequenceContent.append(line);
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
			return null;
		}
		
		if(!processedSeq && sequenceContent.length() > 0) {
			SequenceUtils.processSequence(seq, sequenceContent.toString());
		}
		
		return seq;
	}

	@Override
	public void close() {
		if(reader != null) {
			try {
				reader.close();
				reader = null;
			} catch (IOException e) {
				;
			}
		}
	}
}
