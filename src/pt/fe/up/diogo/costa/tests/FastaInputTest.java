package pt.fe.up.diogo.costa.tests;

import org.junit.Assert;
import org.junit.Test;

import pt.fe.up.diogo.costa.data.Sequence;
import pt.fe.up.diogo.costa.input.FastaInput;
import pt.fe.up.diogo.costa.output.FastaOutput;

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
}
