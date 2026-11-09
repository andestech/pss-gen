package pssgen.util;

import java.util.*;
import java.math.*;

/** The random source of one run: the seed, its java.util.Random and the retry limit. */
public final class PSSRandom {
	private static final String m_alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	private final PSSLog m_log;
	private int i_RetryLimit = 2000;	// Max. iteration of randomization
	private Random m_rand;
	private long m_seed;

	public PSSRandom(PSSLog log) {
		m_log = log;
	}

	public void setRetryLimit (int val) {
		if (val < 1) PSSMessage.Fatal("RetryLimit must greater than 0: value = " + String.valueOf(val));
		m_log.Info("Change retry limit: " + String.valueOf(i_RetryLimit) + " -> " + String.valueOf(val));
		i_RetryLimit = val;
	}

	public void initRandom(long seed) {
		m_log.Info("random seed is " + String.valueOf(seed));
		m_seed = seed;
		m_rand = new Random(seed);
	}
	public long getSeed() {
		return m_seed;
	}
	public Random getRandomObj() {
		return m_rand;
	}
	public String nextString(int len) {
		String str = "";
		for (int i=0; i<len; i++) {
			str = str + m_alphabet.charAt(m_rand.nextInt(m_alphabet.length()));
		}
		return str;
	}
	public String nextString() {
		int len = nextInt(1, 16);
		return nextString(len);
	}
	public ArrayList<Integer> nextSchedule(int size) {
		ArrayList<Integer> schedule = new ArrayList<Integer>();

		for (int i=0; i<size; i++) {
			schedule.add(i);
		}

		for (int i=0; i<size; i++) {
			int idx = nextInt(0, size-1);

			int item = schedule.remove(idx);
			schedule.add(item);
		}
		return schedule;
	}
	public void limitRetry(int retry) {
		if (retry > i_RetryLimit) {
			PSSMessage.Fatal("randomize fail");
		}
	}
	public int nextInt() {
		return m_rand.nextInt();
	}
	public BigInteger nextBigInteger(int numBits, boolean sign) {
		BigInteger rand_val = new BigInteger(numBits, m_rand);	// range 0 to (2^numBits - 1)
		if (sign) {
			BigInteger bias = BigInteger.ONE.shiftLeft(numBits-1);
			rand_val = rand_val.subtract(bias);
		}
		return rand_val;
	}
	public BigInteger nextBigInteger(BigInteger min, BigInteger max) {
		if(max.compareTo(min) < 0) {
			BigInteger tmp = min;
			min = max;
			max = tmp;
		} else if (max.compareTo(min) == 0) {
			return min;
		}
		max = max.add(BigInteger.ONE);

		BigInteger range = max.subtract(min);
		int length = range.bitLength();

		BigInteger result = new BigInteger(length, m_rand);
		while(result.compareTo(range) >= 0) {
			result = new BigInteger(length, m_rand);
		}
		result = result.add(min);
		return result;
	}
	public int nextInt(int min, int max) {
		return m_rand.nextInt(max-min+1) + min;
	}
}
