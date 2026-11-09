package pssgen.solver;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import pssgen.value.PSSIntVal;
import pssgen.value.PSSRangeVal;
import pssgen.value.PSSVal;

/**
 * Characterization of PSSIntDomain. A domain is a list of values and ranges in
 * insertion order; getText() shows that list as it is.
 */
class PSSIntDomainTest {

	private static PSSVal r(long min, long max) {
		return new PSSRangeVal(BigInteger.valueOf(min), BigInteger.valueOf(max));
	}

	private static PSSVal v(long x) {
		return new PSSIntVal(BigInteger.valueOf(x));
	}

	private static PSSIntDomain dom(PSSVal... items) {
		PSSIntDomain d = new PSSIntDomain();
		for (PSSVal item : items) {
			d.addVal(item);
		}
		return d;
	}

	@Test
	void fullDomainOfWidthAndSign() {
		assertEquals("{0..255}", new PSSIntDomain(8, false).getText());
		assertEquals("{-128..127}", new PSSIntDomain(8, true).getText());
		assertEquals("{}", new PSSIntDomain().getText());
		assertTrue(new PSSIntDomain().isEmpty());
	}

	@Test
	void joinIsThePairwiseIntersection() {
		PSSIntDomain d = dom(r(0, 10), r(20, 30)).join(dom(r(5, 25), v(28)));
		assertEquals("{5..10, 20..25, 28}", d.getText());
	}

	@Test
	void joinKeepsDuplicates() {
		assertEquals("{3, 3}", dom(r(0, 5), r(3, 9)).join(dom(v(3))).getText());
	}

	@Test
	void unionConcatenatesWithoutMerging() {
		assertEquals("{7, 0..5, 3..9}", dom(r(0, 5), r(3, 9)).union(dom(v(7))).getText());
	}

	@Test
	void notEqualSplitsARange() {
		assertEquals("{0..4, 6..9}", dom(r(0, 9)).reduceDomainNotEqual(v(5)).getText());
		assertEquals("{1..9}", dom(r(0, 9)).reduceDomainNotEqual(v(0)).getText());
		assertEquals("{0..8}", dom(r(0, 9)).reduceDomainNotEqual(v(9)).getText());
		assertEquals("{0..9}", dom(r(0, 9)).reduceDomainNotEqual(v(10)).getText());
		assertEquals("{4}", dom(v(3), v(4)).reduceDomainNotEqual(v(3)).getText());
	}

	@Test
	void notEqualOnATwoValueRangeLeavesSingleValueRanges() {
		assertEquals("{1..1}", dom(r(0, 1)).reduceDomainNotEqual(v(0)).getText());
	}

	@Test
	void equalKeepsTheValueWhenInRange() {
		assertEquals("{5}", dom(r(0, 9)).reduceDomainEqual(v(5)).getText());
		assertTrue(dom(r(0, 9)).reduceDomainEqual(v(10)).isEmpty());
	}

	@Test
	void inIsJoinWithTheList() {
		PSSIntDomain d = dom(r(0, 9)).reduceDomainIn(Arrays.asList(v(3), r(7, 12)));
		assertEquals("{3, 7..9}", d.getText());
	}

	@Test
	void lessAndGreater() {
		assertEquals("{0..4}", dom(r(0, 9)).reduceDomainLessThan(v(5)).getText());
		assertEquals("{0..5}", dom(r(0, 9)).reduceDomainLessEqual(v(5)).getText());
		assertEquals("{6..9}", dom(r(0, 9)).reduceDomainGreaterThan(v(5)).getText());
		assertEquals("{5..9}", dom(r(0, 9)).reduceDomainGreaterEqual(v(5)).getText());
		assertEquals("{0}", dom(r(0, 9)).reduceDomainLessEqual(v(0)).getText());
	}

	/**
	 * reduceDomainLessThan carries "TODO: will this subtraction underflow?". Values are
	 * BigIntegers, so val - 1 does not wrap: below the minimum the domain is empty.
	 */
	@Test
	void todoUnderflowLessThanTheMinimumIsEmpty() {
		assertTrue(new PSSIntDomain(8, false).reduceDomainLessThan(v(0)).isEmpty());
		BigInteger min65 = BigInteger.ONE.shiftLeft(64).negate();
		assertTrue(new PSSIntDomain(65, true).reduceDomainLessThan(new PSSIntVal(min65)).isEmpty());
	}

	/**
	 * reduceDomainGreaterThan carries "TODO: will this addition overflow?". val + 1 is
	 * a BigInteger sum, so above the maximum the domain is empty.
	 */
	@Test
	void todoOverflowGreaterThanTheMaximumIsEmpty() {
		assertTrue(new PSSIntDomain(8, false).reduceDomainGreaterThan(v(255)).isEmpty());
		BigInteger max65 = BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE);
		assertTrue(new PSSIntDomain(65, true).reduceDomainGreaterThan(new PSSIntVal(max65)).isEmpty());
	}

	@Test
	void minAndMaxOfTheFirstRange() {
		assertEquals(v(-128), new PSSIntDomain(8, true).getMin());
		assertEquals(v(127), new PSSIntDomain(8, true).getMax());
		assertEquals(v(2), dom(r(2, 9), v(5)).getMin());
		assertEquals(v(9), dom(r(2, 9), v(5)).getMax());
		assertNull(new PSSIntDomain().getMin());
	}

	@Test
	void inRange() {
		assertTrue(dom(r(0, 3), v(7)).InRange(v(7)).toBool());
		assertFalse(dom(r(0, 3), v(7)).InRange(v(5)).toBool());
	}
}
