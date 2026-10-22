package pssgen.util;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Characterization of PSSRandom, including a known bug that is kept on purpose. */
class PSSRandomTest {

	private final PSSRandom random = new PSSRandom(new PSSLog(System.out));

	private List<Integer> ints(long seed, int n) {
		random.initRandom(seed);
		List<Integer> l = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			l.add(random.nextInt(0, 1000));
		}
		return l;
	}

	@Test
	void sameSeedSameSequence() {
		assertEquals(ints(42, 20), ints(42, 20));
		assertNotEquals(ints(42, 20), ints(43, 20));
		random.initRandom(7);
		assertEquals(7, random.getSeed());
	}

	@Test
	void streamIsJavaUtilRandom() {
		random.initRandom(1234);
		java.util.Random ref = new java.util.Random(1234);
		assertEquals(ref.nextInt(11) + 5, random.nextInt(5, 15));
		assertEquals(ref.nextInt(), random.nextInt());
	}

	@Test
	void nextIntIsInclusive() {
		random.initRandom(1);
		boolean lo = false, hi = false;
		for (int i = 0; i < 2000; i++) {
			int x = random.nextInt(3, 5);
			assertTrue(x >= 3 && x <= 5);
			lo |= x == 3;
			hi |= x == 5;
		}
		assertTrue(lo && hi);
	}

	@Test
	void nextBigIntegerRange() {
		random.initRandom(1);
		BigInteger a = BigInteger.valueOf(10), b = BigInteger.valueOf(20);
		for (int i = 0; i < 500; i++) {
			BigInteger x = random.nextBigInteger(b, a);
			assertTrue(x.compareTo(a) >= 0 && x.compareTo(b) <= 0, "reversed bounds are swapped");
		}
		assertEquals(a, random.nextBigInteger(a, a));
	}

	@Test
	void nextBigIntegerByWidth() {
		random.initRandom(1);
		for (int i = 0; i < 500; i++) {
			BigInteger u = random.nextBigInteger(4, false);
			BigInteger s = random.nextBigInteger(4, true);
			assertTrue(u.signum() >= 0 && u.intValue() <= 15);
			assertTrue(s.intValue() >= -8 && s.intValue() <= 7);
		}
	}

	@Test
	void scheduleIsAPermutation() {
		random.initRandom(5);
		List<Integer> s = random.nextSchedule(6);
		List<Integer> sorted = new ArrayList<>(s);
		Collections.sort(sorted);
		assertEquals(List.of(0, 1, 2, 3, 4, 5), sorted);
	}

	@Test
	void stringLengthAndDefaultLength() {
		random.initRandom(9);
		assertEquals(6, random.nextString(6).length());
		for (int i = 0; i < 200; i++) {
			int n = random.nextString().length();
			assertTrue(n >= 1 && n <= 16);
		}
	}

	@Test
	void retryLimit() {
		random.limitRetry(2000);
		RuntimeException e = assertThrows(RuntimeException.class, () -> random.limitRetry(2001));
		assertEquals("randomize fail", e.getMessage());
		assertThrows(RuntimeException.class, () -> random.setRetryLimit(0));
	}

	/**
	 * Known bug: the alphabet of nextString has 61 characters and no lowercase 'z'.
	 * Not fixed: a 62-character alphabet changes every random string, and the stream
	 * after it, of every pattern that draws a string.
	 */
	@Test
	void knownBugAlphabetLacksLowercaseZ() {
		random.initRandom(3);
		String s = random.nextString(20000);
		assertEquals(-1, s.indexOf('z'));
		assertEquals(61, s.chars().distinct().count());
		assertTrue(s.indexOf('y') >= 0 && s.indexOf('Z') >= 0 && s.indexOf('9') >= 0);
	}
}
