package pt.fe.up.diogo.costa.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pt.fe.up.diogo.costa.config.Configuration;
import pt.fe.up.diogo.costa.data.Chunk;
import pt.fe.up.diogo.costa.data.IRegion;
import pt.fe.up.diogo.costa.data.Sequence;

public class SequenceUtils {
	private static Map<String, Byte> dictionary = null;
	private static Map<Byte, String> inverse_dictionary = null;
	
	private static final Pattern headerPattern = Pattern.compile("^>(\\S+)(\\s+(.+))?$");
	private static final Pattern seqPattern = Pattern.compile("[ACTG]+", Pattern.CASE_INSENSITIVE);
	
	public static byte[] compressSequence(String in) {
		if(dictionary == null)
			buildDictionary();

		byte[] sb = new byte[in.length() / 3 + 3];
		
		int sbIndex = 0;
		for(int i = 0; i < in.length(); i += 3) {
			int end = i + 3;
			if(end > in.length())
				end = in.length();
			String codon = in.substring(i, end);
			
			if(codon.length() < 3) {
				for(int j = 0; j < 3 - codon.length() + 1; ++j)
					codon += "-";
			}
			
			Byte c = dictionary.get(codon);
			if(c != null) {
				sb[sbIndex++] = c.byteValue();
			} else {
				System.err.println("Not specified: " + codon);
			}
		}
		
		return sb;
	}
	
	public static String decompressSequence(byte[] in) {
		if(inverse_dictionary == null)
			buildDictionary();
		
		StringBuilder sb = new StringBuilder(in.length * 3);
		
		for(int i = 0; i < in.length; ++i) {
			String codon = inverse_dictionary.get(new Byte(in[i]));
			if(codon != null) {
				sb.append(codon);
			} else {
				System.err.println("Not specified: " + codon);
			}
		}
		
		return sb.toString();
	}
	
	public static Sequence processSequence(String content) {
		Sequence seq = new Sequence();
		processSequence(seq, content);
		return seq;
	}
	
	public static void processSequence(Sequence seq, String content) {		
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
	
	public static void processHeader(Sequence seq, String header) {
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
			
			seq.setName(id);
		}
		
	}
	private static void buildDictionary() {
		dictionary = new HashMap<String, Byte>();
		inverse_dictionary = new HashMap<Byte, String>();

		String[] words = { 
			"AAA", "AAT", "AAC", "AAG", "AAN", "AA-", "ATA", "ATT", "ATC", "ATG", "ATN", "AT-", "ACA", "ACT", "ACC", "ACG", "ACN", "AC-", "AGA", "AGT", "AGC", "AGG",
			"AGN", "AG-", "ANA", "ANT", "ANC", "ANG", "ANN", "AN-", "A-A", "A-T", "A-C", "A-G", "A-N", "A--", "TAA", "TAT", "TAC", "TAG", "TAN", "TA-", "TTA", "TTT",
			"TTC", "TTG", "TTN", "TT-", "TCA", "TCT", "TCC", "TCG", "TCN", "TC-", "TGA", "TGT", "TGC", "TGG", "TGN", "TG-", "TNA", "TNT", "TNC", "TNG", "TNN", "TN-", 
			"T-A", "T-T", "T-C", "T-G", "T-N", "T--", "CAA", "CAT", "CAC", "CAG", "CAN", "CA-", "CTA", "CTT", "CTC", "CTG", "CTN", "CT-", "CCA", "CCT", "CCC", "CCG",
			"CCN", "CC-", "CGA", "CGT", "CGC", "CGG", "CGN", "CG-", "CNA", "CNT", "CNC", "CNG", "CNN", "CN-", "C-A", "C-T", "C-C", "C-G", "C-N", "C--", "GAA", "GAT",
			"GAC", "GAG", "GAN", "GA-", "GTA", "GTT", "GTC", "GTG", "GTN", "GT-", "GCA", "GCT", "GCC", "GCG", "GCN", "GC-", "GGA", "GGT", "GGC", "GGG", "GGN", "GG-",
			"GNA", "GNT", "GNC", "GNG", "GNN", "GN-", "G-A", "G-T", "G-C", "G-G", "G-N", "G--", "NAA", "NAT", "NAC", "NAG", "NAN", "NA-", "NTA", "NTT", "NTC", "NTG",
			"NTN", "NT-", "NCA", "NCT", "NCC", "NCG", "NCN", "NC-", "NGA", "NGT", "NGC", "NGG", "NGN", "NG-", "NNA", "NNT", "NNC", "NNG", "NNN", "NN-", "N-A", "N-T", 
			"N-C", "N-G", "N-N", "N--", "-AA", "-AT", "-AC", "-AG", "-AN", "-A-", "-TA", "-TT", "-TC", "-TG", "-TN", "-T-", "-CA", "-CT", "-CC", "-CG", "-CN", "-C-", 
			"-GA", "-GT", "-GC", "-GG", "-GN", "-G-", "-NA", "-NT", "-NC", "-NG", "-NN", "-N-", "--A", "--T", "--C", "--G", "--N", "---"
		};
		
		for(short i = 0; i < words.length; ++i) {
			dictionary.put(words[i], (byte)(i - 127));
			inverse_dictionary.put((byte)(i - 127), words[i]);
		}
	}
}
