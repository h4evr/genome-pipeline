package pt.fe.up.diogo.costa.output;

import pt.fe.up.diogo.costa.data.Sequence;

public class FastaOutput implements IOutput {

	@Override
	public String toString(Sequence s) {
		if(s == null)
			return "";
		
		StringBuilder b = new StringBuilder();
		
		
		b.append(">").append(s.getName());
		
		if(s.getAttributes().size() > 0) {
			b.append(" ");
			
			for(String key : s.getAttributes().keySet()) {
				b.append(key).append("=").append(s.getAttributes().get(key)).append("; ");
			}
		}
		
		b.append("\n").append(s.getSequence()).append("\n");
		
		return b.toString();
	}

}
