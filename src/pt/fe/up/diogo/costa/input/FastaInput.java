package pt.fe.up.diogo.costa.input;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pt.fe.up.diogo.costa.config.Configuration;
import pt.fe.up.diogo.costa.data.Chunk;
import pt.fe.up.diogo.costa.data.IRegion;
import pt.fe.up.diogo.costa.data.Sequence;

public class FastaInput implements IInput {
	private String lastLine = null;
	private BufferedReader reader = null;
	
	private static final Pattern headerPattern = Pattern.compile("^>(\\S+)(\\s+(.+))?$");
	private static final Pattern seqPattern = Pattern.compile("[ACTG]+", Pattern.CASE_INSENSITIVE);
	
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
						processHeader(seq, line);
						readHeader = true;
					} else {
						processSequence(seq, sequenceContent.toString());
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
			processSequence(seq, sequenceContent.toString());
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
	
	private void processHeader(Sequence seq, String header) {
		Matcher m = headerPattern.matcher(header);
		
		if(m.matches()) {
			String id = m.group(1);
			String attribsStr = m.group(3);
			
			if(attribsStr != null && attribsStr.length() > 0) {
				String[] attribs = attribsStr.split(";");
				
				for(String attr : attribs) {
					String[] pair = attr.split("[=\\|]");
					seq.getAttributes().put(pair[0].trim(), 
							                pair.length > 0 ? pair[1].trim() : "");
				}
			}
			
			seq.setId(id);
		}
		
	}
	
	private void processSequence(Sequence seq, String content) {
		int chunkSize = (int)Configuration.getInstance().getChunkSize();
		
		seq.setChunks(new LinkedList<IRegion>());
		
		seq.setLength(content.trim().length());
		
		Matcher m = seqPattern.matcher(content);
		
		while(m.find()) {
			String seqStr = m.group();
			int startPosition = m.start();
			int endPosition = m.end();
			int seqLen = endPosition - startPosition;
			
			int offsetChunkPos = 0;
			
			while(offsetChunkPos < seqLen) {
				int len = seqLen - offsetChunkPos;
				
				if(len > chunkSize) {
					len = chunkSize;
				}
				
				String chunkStr = seqStr.substring(offsetChunkPos, offsetChunkPos + len);
				
				Chunk c = new Chunk();
				c.setStartPosition((long)startPosition + offsetChunkPos);
				c.setEndPosition((long)(startPosition + offsetChunkPos + len - 1));
				c.setSequence(chunkStr);
				seq.getChunks().add(c);
				
				offsetChunkPos += chunkSize;
			}
		}
	}
}
