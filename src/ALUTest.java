import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ALUTest {
	
	ALU alu = new ALU();

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testIntegerRepresentation() {
		String string1 = alu.integerRepresentation("9", 8);
		assertEquals(string1, "00001001");
		String string2 = alu.integerRepresentation("-9", 8);
		assertEquals(string2, "11110111");
		String string3 = alu.integerRepresentation("-9", 16);
		assertEquals(string3, "1111111111110111");
		String string4 = alu.integerRepresentation("0", 16);
		assertEquals(string4, "0000000000000000");
	}

	@Test
	public void testFloatRepresentation() {
		String string1 = alu.floatRepresentation("11.375", 8, 11);
		assertEquals("01000001001101100000", string1);
	}

	@Test
	public void testIeee754() {
		String string1 = alu.ieee754("11.375", 32);
		assertEquals("01000001001101100000000000000000", string1);
		String string2 = alu.ieee754("-22.475", 32);
		assertEquals("11000001101100111100110011001100", string2);
		String string3 = alu.ieee754("-0.475", 32);
		assertEquals("10111110111100110011001100110011", string3);
	}

	@Test
	public void testIntegerTrueValue() {
		String string1 = alu.integerTrueValue("00001001");
		assertEquals("9", string1);
		String string2 = alu.integerTrueValue("1000");
		assertEquals("-8", string2);
		String string3 = alu.integerTrueValue("1111");
		assertEquals("-1", string3);
	}

	@Test
	public void testFloatTrueValue() {
		String string1 = alu.floatTrueValue("01000001001101100000", 8, 11);
		assertEquals("11.375", string1);
		String string2 = alu.floatTrueValue("00111110110000000000000000000000", 8, 23);
		assertEquals("0.375", string2);
	}

	@Test
	public void testNegation() {
		String string1 = alu.negation("00101111");
		assertEquals("11010000", string1);
		String string2 = alu.negation("111111111111");
		assertEquals("000000000000", string2);
	}

	@Test
	public void testLeftShift() {
		String string1 = alu.leftShift("00010011", 2);
		assertEquals("01001100", string1);
		String string2 = alu.leftShift("11111111", 7);
		assertEquals("10000000", string2);
	}

	@Test
	public void testLogRightShift() {
		String string1 = alu.logRightShift("00010011", 2);
		assertEquals("00000100", string1);
		String string2 = alu.logRightShift("11111111", 7);
		assertEquals("00000001", string2);
	}

	@Test
	public void testAriRightShift() {
		String string1 = alu.ariRightShift("00010011", 2);
		assertEquals("00000100", string1);
		String string2 = alu.ariRightShift("11111111", 7);
		assertEquals("11111111", string2);
	}

	@Test
	public void testFullAdder() {
		String string1 = alu.fullAdder('1', '1', '0');
		assertEquals("10", string1);
		assertEquals("00", alu.fullAdder('0', '0', '0'));
		assertEquals("01", alu.fullAdder('1', '0', '0'));
		assertEquals("10", alu.fullAdder('0', '1', '1'));
		assertEquals("11", alu.fullAdder('1', '1', '1'));
	}

	@Test
	public void testClaAdder() {
		String string1 = alu.claAdder("1001", "0001", '1');
		assertEquals("01011", string1);
		String string2 = alu.claAdder("1000", "1000", '1');
		assertEquals("10001", string2);
		String string3 = alu.claAdder("0000", "0000", '0');
		assertEquals("00000", string3);
		assertEquals("01011", alu.claAdder("1001", "0001", '1'));
		assertEquals("10010", alu.claAdder("1001", "1001", '0'));
		assertEquals("10011", alu.claAdder("1001", "1001", '1'));
		assertEquals("01011", alu.claAdder("1001", "0001", '1'));
		assertEquals("01111", alu.claAdder("1100", "0011", '0'));
	}

	@Test
	public void testOneAdder() {
		String string1 = alu.oneAdder("10000000");
		assertEquals("010000001", string1);
		String string2 = alu.oneAdder("011111");
		assertEquals("1100000", string2);
		String string3 = alu.oneAdder("100111");
		assertEquals("0101000", string3);
		
	}

	@Test
	public void testAdder() {
		String string1 = alu.adder("0100", "0011", '0', 8);
		assertEquals("000000111", string1);
		String string2 = alu.adder("0100", "0011", '0', 4);
		assertEquals("00111", string2);
		String string3 = alu.adder("0100", "0011", '1', 4);
		assertEquals("11000", string3);
		String string4 = alu.adder("11111100", "00000011", '0', 8);
		assertEquals("011111111", string4);
		assertEquals("000000111", alu.adder("0100", "0011", '0', 8));
		assertEquals("011111111", alu.adder("00001111", "11110000", '0', 8));
		assertEquals("00000000000000000", alu.adder("11111111", "00000001", '0', 16));
	}

	@Test
	public void testIntegerAddition() {
		String string1 = alu.integerAddition("0100", "0011", 8);
		assertEquals("000000111", string1);
	}

	@Test
	public void testIntegerSubtraction() {
		String string1 = alu.integerSubtraction("0100", "0011", 8);
		assertEquals("000000001", string1);
	}

	@Test
	public void testIntegerMultiplication() {
		String string1 = alu.integerMultiplication("0100", "0011", 8);
		assertEquals("000001100", string1);
		String string2 = alu.integerMultiplication("0001", "0001", 4);
		assertEquals("00001", string2);
	}

	@Test
	public void testIntegerDivision() {
		String string1 = alu.integerDivision("0100", "0011", 4);
		assertEquals("000010001", string1);
		String string2 = alu.integerDivision("0111", "0010", 4);
		assertEquals("000110001", string2);
		String string3 = alu.integerDivision("0111", "0001", 4);
		assertEquals("001110000", string3);
		String string4 = alu.integerDivision("0100", "0011", 8);
		assertEquals("00000000100000001", string4);
	}

	@Test
	public void testSignedAddition() {
		String string1 = alu.signedAddition("1100", "1011", 8);
		assertEquals("0100000111", string1);
	}

	@Test
	public void testFloatAddition() {
		String string1 = alu.floatAddition("00111111010100000", "00111111001000000", 8, 8, 4);
		assertEquals("000111111101110000", string1);
		assertEquals("000111111101110000", alu.floatAddition("00111111010100000", "00111111001000000", 8, 8, 8));
		assertEquals("000111111001000000", alu.floatAddition("00000000000000000", "00111111001000000", 8, 8, 8));
		assertEquals("000110110000100000", alu.floatAddition("00110110000100000", "00110000000100000", 8, 8, 8));
		assertEquals("010110010000000000", alu.floatAddition("00110110000100010", "10110110000100011", 8, 8, 8));
		assertEquals("010000000000000000", alu.floatAddition("10110110010111011", "00110110010111011", 8, 8, 0));
		assertEquals("000111111101110000", alu.floatAddition("00111111010100000", "00111111001000000", 8, 8, 0));
	}

	@Test
	public void testFloatSubtraction() {
		String string1 = alu.floatSubtraction("00111111010100000", "00111111001000000", 8, 8, 4);
		assertEquals("000111110010000000", string1);
	}

	@Test
	public void testFloatMultiplication() {
		String string1 = alu.floatMultiplication("00111110111000000", "00111111000000000", 8, 8);
		assertEquals("000111110011000000", string1);
	}

	@Test
	public void testFloatDivision() {
		String string1 = alu.floatDivision("00111110111000000", "00111111000000000", 8, 8);
		assertEquals("000111111011000000", string1);
	}

}
