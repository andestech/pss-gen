package pssgen.value;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

/** Characterization of PSSRangeVal, the internal [min..max] element of integer domains. */
class PSSRangeValTest {

	private static PSSRangeVal range(long min, long max) {
		return new PSSRangeVal(BigInteger.valueOf(min), BigInteger.valueOf(max));
	}

	@Test
	void intRangeOfWidthAndSign() {
		assertEquals("0..15", PSSRangeVal.getIntRangeVal(4, false).getText());
		assertEquals("-8..7", PSSRangeVal.getIntRangeVal(4, true).getText());
		assertEquals("0..1", PSSRangeVal.getIntRangeVal(1, false).getText());
	}

	@Test
	void joinIntersects() {
		assertEquals(range(2, 5), range(0, 5).join(range(2, 9)));
		assertEquals(new PSSIntVal(5), range(0, 5).join(range(5, 9)));
		assertNull(range(0, 4).join(range(5, 9)));
		assertEquals(new PSSIntVal(3), range(0, 4).join(new PSSIntVal(3)));
		assertNull(range(0, 4).join(new PSSIntVal(9)));
	}

	@Test
	void reduceLessEqual() {
		assertNull(range(8, 9).reduceLessEqual(new PSSIntVal(7)));
		assertEquals(new PSSIntVal(7), range(7, 9).reduceLessEqual(new PSSIntVal(7)));
		assertEquals(range(5, 6), range(5, 6).reduceLessEqual(new PSSIntVal(7)));
		assertEquals(range(5, 7), range(5, 9).reduceLessEqual(new PSSIntVal(7)));
	}

	@Test
	void reduceGreaterEqual() {
		assertNull(range(1, 4).reduceGreaterEqual(new PSSIntVal(5)));
		assertEquals(new PSSIntVal(5), range(1, 5).reduceGreaterEqual(new PSSIntVal(5)));
		assertEquals(range(5, 7), range(4, 7).reduceGreaterEqual(new PSSIntVal(5)));
		assertEquals(range(6, 7), range(6, 7).reduceGreaterEqual(new PSSIntVal(5)));
	}

	@Test
	void inRangeIsInclusive() {
		assertTrue(range(1, 3).InRange(new PSSIntVal(1)).toBool());
		assertTrue(range(1, 3).InRange(new PSSIntVal(3)).toBool());
		assertFalse(range(1, 3).InRange(new PSSIntVal(4)).toBool());
	}

	@Test
	void conversionsAreErrors() {
		RuntimeException e = assertThrows(RuntimeException.class, () -> range(1, 3).toInt());
		assertEquals("IntVal cannot be converted to int", e.getMessage());
	}
}
