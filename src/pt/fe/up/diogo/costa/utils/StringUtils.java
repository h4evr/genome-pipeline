package pt.fe.up.diogo.costa.utils;

public class StringUtils {
	
	public static String encodeRLE(String str) {
		StringBuilder out = new StringBuilder(str.length());
		
		char c, cn;
		int cnt = 0, i, j;
		
		for(i = 0; i < str.length(); ++i) {
			c = str.charAt(i);
			cnt = 1;
			
			for(j = i + 1; j < str.length(); ++j) {
				cn = str.charAt(j);
				if(cn == c) {
					++cnt;
				} else {
					break;
				}
			}
			
			i = j - 1;
			
			if(cnt > 1)
				out.append(Integer.toString(cnt));
			out.append(c);
		}
		
		return out.toString();
	}
	
	public static String decodeRLE(String str) {
		StringBuilder out = new StringBuilder(str.length());
		
		int i = 0, j = 0, k = 0, cnt = 0;
		char c = 0, cn = 0;
		
		for(i = 0; i < str.length(); ++i) {
			c = str.charAt(i);
			cnt = 1;
			
			if(c > '0' && c <= '9') {
				cnt = (int)(c - '0');
				for(j = i + 1; j < str.length(); ++j) {
					cn = str.charAt(j);
					if(cn >= '0' && cn <= '9') {
						cnt = cnt * 10 + (int)(cn - '0');
					} else {
						for(k = 0; k < cnt; ++k) {
							out.append(cn);
						}
						
						i = j;
						break;
					}
				}
			} else {
				out.append(c);
			}
		}
		
		return out.toString();
	}
}
