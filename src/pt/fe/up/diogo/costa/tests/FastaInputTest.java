package pt.fe.up.diogo.costa.tests;

import org.junit.Assert;
import org.junit.Test;

import pt.fe.up.diogo.costa.data.Sequence;
import pt.fe.up.diogo.costa.input.FastaInput;
import pt.fe.up.diogo.costa.output.FastaOutput;
import pt.fe.up.diogo.costa.utils.SequenceUtils;

public class FastaInputTest {

	@Test
	public void testReadNextSequence() {

		FastaInput in = new FastaInput();
		Sequence seq = null;
		long numSeqs = 0;
		
		Assert.assertTrue(in.open("A.fasta"));
		
		try {
			while((seq = in.readNextSequence()) != null) {
				//System.out.println(seq.getId());
				
				/*for(String key : seq.getAttributes().keySet()) {
					System.out.println("key: " + key + ", value = " + seq.getAttributes().get(key));
				}*/
				
				Assert.assertTrue(seq.getAttributes().size() > 0);
				Assert.assertTrue(seq.getChunks() != null);
				Assert.assertTrue(seq.getChunks().size() > 0);
				
				++numSeqs;
			}
		} finally {
			in.close();
		}
		
		Assert.assertTrue(numSeqs == 6);		
	}

	@Test
	public void assertChunks() {
		FastaInput in = new FastaInput();
		Sequence seq = null;
		
		Assert.assertTrue(in.open("test.fa"));
		
		try {
			while((seq = in.readNextSequence()) != null) {
				Assert.assertEquals(">seq1\nTGT---GTG---CAG---ATT---GTG--ATT---GCG---AAC---GAA---AAT---CAT---ATT---TTC---TGT---TAT---TGT---GGC---AAA---ATG---AGA\n", 
						            (new FastaOutput()).toString(seq));
			}
		} finally {
			in.close();
		}
	}	
	
	@Test
	public void assertEncodeSequence() {
		FastaInput in = new FastaInput();
		Sequence seq = null;
		long numSeqs = 0;
		
		Assert.assertTrue(in.open("A.fasta"));
		
		try {
			while((seq = in.readNextSequence()) != null) {				
				String sequence = seq.getSequence();
				int seqOldLen = sequence.length();
				byte[] comSequence = SequenceUtils.compressSequence(sequence);
				int seqNewLen = comSequence.length;
				System.out.print(seqOldLen + " : ");
				System.out.print(seqNewLen + " = " + ((double)seqNewLen / (double)seqOldLen));
				System.out.print(" -> ");
				
				String decomp = SequenceUtils.decompressSequence(comSequence);
				System.out.println(decomp.length());
				
				Assert.assertTrue(seq.getAttributes().size() > 0);
				Assert.assertTrue(seq.getChunks() != null);
				Assert.assertTrue(seq.getChunks().size() > 0);
				
				Assert.assertEquals(sequence, decomp.substring(0, seqOldLen));
				
				++numSeqs;
			}
		} finally {
			in.close();
		}
		
		Assert.assertTrue(numSeqs == 6);	
	}
	
	@Test
	public void assertEncodeAndDecodeSequence() {
		final String sequence = "TGTGTGCAGATTGTGATTGCGAACGAAAATCATATTTTCTGTTATTGTGGCAAAATGAGA";
		int seqOldLen = sequence.length();
		byte[] comSequence = SequenceUtils.compressSequence(sequence);		
		String decomp = SequenceUtils.decompressSequence(comSequence);
		
		System.out.println(sequence);
		System.out.println(decomp.substring(0, seqOldLen));
		
		Assert.assertEquals(sequence, decomp.substring(0, seqOldLen));	
	}
}
