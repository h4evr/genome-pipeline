package pt.fe.up.diogo.costa.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import pt.fe.up.diogo.costa.utils.StringUtils;

public class StringUtilsTest {

	@Test
	public void testEncodeRLE() {
		String input = "AAAABBBBCCCCDEF";
		String output = StringUtils.encodeRLE(input);
		assertEquals("4A4B4CDEF", output);
	}

	@Test
	public void testDecodeRLE() {
		String input = "10A4B4CDEF";
		String output = StringUtils.decodeRLE(input);
		assertEquals("AAAAAAAAAABBBBCCCCDEF", output);
	}
}
