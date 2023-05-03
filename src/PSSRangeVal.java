import java.util.*;
import java.math.*;

public class PSSRangeVal extends PSSVal {

	BigInteger m_min;
	BigInteger m_max;

	public PSSRangeVal(BigInteger begin, BigInteger end) {
		m_min = begin;
		m_max = end;
	}

	public boolean isRangeVal() {
		return true;
	}

	public PSSVal reduceLessEqual(PSSVal val) {
		// reduceLessEqual(7)
		// [8..9] -> null
		// [7..9] -> 7
		// [5..6] -> [5..6]
		// [5..9] -> [5..7]
		BigInteger int_val = val.toBigInteger();

		if (m_min.compareTo(int_val) > 0) {
			return null;
		} else if (m_min.compareTo(int_val) == 0) {
			return new PSSIntVal(m_min);
		} else if (m_max.compareTo(int_val) < 0) {
			return new PSSRangeVal(m_min, m_max);
		} else {
			return new PSSRangeVal(m_min, int_val);
		}
	}

	public PSSVal reduceGreaterEqual(PSSVal val) {
		// reduceGreaterEqual(5)
		// [1..4] -> null
		// [1..5] -> 5
		// [4..7] -> [5..7]
		// [6..7] -> [6..7]
		BigInteger int_val = val.toBigInteger();
		if (m_max.compareTo(int_val) < 0) {
			return null;
		} else if (m_max.compareTo(int_val) == 0) {
			return new PSSIntVal(m_max);
		} else if (m_min.compareTo(int_val) > 0) {
			return new PSSRangeVal(m_min, m_max);
		} else {
			return new PSSRangeVal(int_val, m_max);
		}
	}

	public PSSVal getMin() {
		return new PSSIntVal(m_min);
	}

	public PSSVal getMax() {
		return new PSSIntVal(m_max);
	}

	static public PSSVal getIntRangeVal(int width, boolean sign) {
		BigInteger min, max;
		if (sign) {
			min = BigInteger.ONE.shiftLeft(width - 1).negate();
			max = BigInteger.ONE.shiftLeft(width - 1).subtract(BigInteger.ONE);
		} else {
			min = BigInteger.ZERO;
			max = BigInteger.ONE.shiftLeft(width).subtract(BigInteger.ONE);
		}

		return new PSSRangeVal(min, max);
	}

	public PSSVal join(PSSVal val) {
		if (val.isRangeVal()) {
			BigInteger min = val.getMin().toBigInteger();
			BigInteger max = val.getMax().toBigInteger();

			min = min.max(m_min); // 2
			max = max.min(m_max); // 6

			if (min.compareTo(max) > 0) {
				return null;
			} else if (min.compareTo(max) == 0) {
				return new PSSIntVal(min);
			} else {
				return new PSSRangeVal(min, max);
			}
		} else {
			BigInteger int_val = val.toBigInteger();
			if ((int_val.compareTo(m_min) >= 0) && (int_val.compareTo(m_max) <= 0)) {
				return new PSSIntVal(int_val);
			} else {
				return null;
			}
		}
	}

	public PSSBoolVal InRange(PSSVal val) {
		BigInteger int_val = val.toBigInteger();
		return new PSSBoolVal((m_min.compareTo(int_val) <= 0) && (m_max.compareTo(int_val) >= 0));
	}

	public String getText() {
		return m_min.toString(10) + ".." + m_max.toString(10);
	}

	public int toInt() {
		PSSMessage.Error("RANGEVAL", "IntVal cannot be converted to int");
		return 0;
	}

	public String toString() {
		PSSMessage.Error("RANGEVAL", "IntVal cannot be converted to string");
		return "";
	}

	public boolean toBool() {
		PSSMessage.Error("RANGEVAL", "IntVal cannot be converted to bool");
		return false;
	}

	public PSSVal randomIn() {
		BigInteger rand_val = PSSRandom.nextBigInteger(m_min, m_max);
		return new PSSIntVal(rand_val);
	}
	// public PSSVal randomString() {
	// if (m_max != null) {
	// PSSMessage.Error("OPENRANGE", "Open range string value is not supported");
	// }
	// return m_min;
	// }

	@Override
	public int compareTo(PSSVal o) {
		if (o instanceof PSSRangeVal) {
			PSSRangeVal r = (PSSRangeVal) o;
			int cmin = m_min.compareTo(r.m_min);
			if (cmin != 0)
				return cmin;
			return m_max.compareTo(r.m_max);
		}
		return super.compareTo(o);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof PSSRangeVal) {
			PSSRangeVal r = (PSSRangeVal) o;
			return m_min.equals(r.m_min) && m_max.equals(r.m_max);
		}
		return false;
	}

}
