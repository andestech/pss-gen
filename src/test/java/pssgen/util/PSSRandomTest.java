package pssgen.util;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Characterization of PSSRandom, including two known bugs that are kept on purpose. */
class PSSRandomTest {

	private static List<Integer> ints(long seed, int n) {
		PSSRandom.initRandom(seed);
		List<Integer> l = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			l.add(PSSRandom.nextInt(0, 1000));
		}
		return l;
	}

	@Test
	void sameSeedSameSequence() {
		assertEquals(ints(42, 20), ints(42, 20));
		assertNotEquals(ints(42, 20), ints(43, 20));
		PSSRandom.initRandom(7);
		assertEquals(7, PSSRandom.getSeed());
	}

	@Test
	void streamIsJavaUtilRandom() {
		PSSRandom.initRandom(1234);
		java.util.Random ref = new java.util.Random(1234);
		assertEquals(ref.nextInt(11) + 5, PSSRandom.nextInt(5, 15));
		assertEquals(ref.nextInt(), PSSRandom.nextInt());
	}

	@Test
	void nextIntIsInclusive() {
		PSSRandom.initRandom(1);
		boolean lo = false, hi = false;
		for (int i = 0; i < 2000; i++) {
			int x = PSSRandom.nextInt(3, 5);
			assertTrue(x >= 3 && x <= 5);
			lo |= x == 3;
			hi |= x == 5;
		}
		assertTrue(lo && hi);
	}

	@Test
	void nextBigIntegerRange() {
		PSSRandom.initRandom(1);
		BigInteger a = BigInteger.valueOf(10), b = BigInteger.valueOf(20);
		for (int i = 0; i < 500; i++) {
			BigInteger x = PSSRandom.nextBigInteger(b, a);
			assertTrue(x.compareTo(a) >= 0 && x.compareTo(b) <= 0, "reversed bounds are swapped");
		}
		assertEquals(a, PSSRandom.nextBigInteger(a, a));
	}

	@Test
	void nextBigIntegerByWidth() {
		PSSRandom.initRandom(1);
		for (int i = 0; i < 500; i++) {
			BigInteger u = PSSRandom.nextBigInteger(4, false);
			BigInteger s = PSSRandom.nextBigInteger(4, true);
			assertTrue(u.signum() >= 0 && u.intValue() <= 15);
			assertTrue(s.intValue() >= -8 && s.intValue() <= 7);
		}
	}

	@Test
	void scheduleIsAPermutation() {
		PSSRandom.initRandom(5);
		List<Integer> s = PSSRandom.nextSchedule(6);
		List<Integer> sorted = new ArrayList<>(s);
		Collections.sort(sorted);
		assertEquals(List.of(0, 1, 2, 3, 4, 5), sorted);
	}

	@Test
	void stringLengthAndDefaultLength() {
		PSSRandom.initRandom(9);
		assertEquals(6, PSSRandom.nextString(6).length());
		for (int i = 0; i < 200; i++) {
			int n = PSSRandom.nextString().length();
			assertTrue(n >= 1 && n <= 16);
		}
	}

	@Test
	void retryLimit() {
		PSSRandom.limitRetry(2000);
		RuntimeException e = assertThrows(RuntimeException.class, () -> PSSRandom.limitRetry(2001));
		assertEquals("randomize fail", e.getMessage());
		assertThrows(RuntimeException.class, () -> PSSRandom.setRetryLimit(0));
	}

	/**
	 * Known bug: the alphabet of nextString has 61 characters and no lowercase 'z'.
	 * Not fixed: a 62-character alphabet changes every random string, and the stream
	 * after it, of every pattern that draws a string.
	 */
	@Test
	void knownBugAlphabetLacksLowercaseZ() {
		PSSRandom.initRandom(3);
		String s = PSSRandom.nextString(20000);
		assertEquals(-1, s.indexOf('z'));
		assertEquals(61, s.chars().distinct().count());
		assertTrue(s.indexOf('y') >= 0 && s.indexOf('Z') >= 0 && s.indexOf('9') >= 0);
	}

	/**
	 * Known bug: nextBigInteger(numBits, sign, min, max) ignores min and max and
	 * returns what nextBigInteger(numBits, sign) returns. Not fixed: honouring the
	 * bounds changes the random stream for callers of the four-argument form.
	 */
	@Test
	void knownBugFourArgNextBigIntegerIgnoresMinMax() {
		BigInteger min = BigInteger.valueOf(100), max = BigInteger.valueOf(101);
		PSSRandom.initRandom(11);
		List<BigInteger> four = new ArrayList<>();
		for (int i = 0; i < 50; i++) {
			four.add(PSSRandom.nextBigInteger(16, false, min, max));
		}
		PSSRandom.initRandom(11);
		List<BigInteger> two = new ArrayList<>();
		for (int i = 0; i < 50; i++) {
			two.add(PSSRandom.nextBigInteger(16, false));
		}
		assertEquals(two, four);
		assertTrue(four.stream().anyMatch(x -> x.compareTo(min) < 0 || x.compareTo(max) > 0));
	}
}
