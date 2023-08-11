import java.math.*;

public class PSSIntVal extends PSSVal {

	BigInteger m_val;

	public PSSIntVal(BigInteger val) {
		super(PSSIntModel.getDefaultDecimalModel());
		m_val = val;
	}

	public PSSIntVal(int val) {
		super(PSSIntModel.getDefaultDecimalModel());
		m_val = BigInteger.valueOf(val);
	}

	static PSSIntVal ONE() {
		return new PSSIntVal(BigInteger.ONE);
	}

    @Override
	public String getText() {
		return m_val.toString(10);
	}

    @Override
	public String toTargetCode() {
		return m_val.toString(10);
	}

    @Override
	public BigInteger toBigInteger() {
		return m_val;
	}

    @Override
	public int toInt() {
		return m_val.intValue();
	}

    @Override
	public String toStr() {
		PSSMessage.Error("INTVAL", "BitVal cannot be converted to string");
		return "";
	}

    @Override
	public boolean toBool() {
		return m_val.compareTo(BigInteger.ZERO) != 0;
	}

    @Override
	public PSSBoolVal Equal(PSSVal rhs) {
		BigInteger rhs_int = rhs.toBigInteger();
		boolean eq = m_val.equals(rhs_int);
		return PSSBoolVal.valueOf(eq);
	}

    @Override
	public PSSBoolVal NotEqual(PSSVal rhs) {
		BigInteger rhs_int = rhs.toBigInteger();
		boolean eq = m_val.equals(rhs_int);
		return PSSBoolVal.valueOf(!eq);
	}

    @Override
	public PSSBoolVal GreaterThan(PSSVal rhs) {
		BigInteger rhs_int = rhs.toBigInteger();
		boolean result = m_val.compareTo(rhs_int) == 1;
		return PSSBoolVal.valueOf(result);
	}

    @Override
	public PSSBoolVal GreaterEqual(PSSVal rhs) {
		BigInteger rhs_int = rhs.toBigInteger();
		boolean result = m_val.compareTo(rhs_int) >= 0;
		return PSSBoolVal.valueOf(result);
	}

    @Override
	public PSSBoolVal LessThan(PSSVal rhs) {
		BigInteger rhs_int = rhs.toBigInteger();
		boolean result = m_val.compareTo(rhs_int) < 0;
		return PSSBoolVal.valueOf(result);
	}

    @Override
	public PSSBoolVal LessEqual(PSSVal rhs) {
		BigInteger rhs_int = rhs.toBigInteger();
		boolean result = m_val.compareTo(rhs_int) <= 0;
		return PSSBoolVal.valueOf(result);
	}

    @Override
	public PSSVal Add(PSSVal rhs) {
		BigInteger rhs_int = rhs.toBigInteger();
		BigInteger result = m_val.add(rhs_int);
		return new PSSIntVal(result);
	}

    @Override
	public PSSVal Sub(PSSVal rhs) {
		BigInteger rhs_int = rhs.toBigInteger();
		BigInteger result = m_val.subtract(rhs_int);
		return new PSSIntVal(result);
	}

    @Override
	public PSSVal Mul(PSSVal rhs) {
		BigInteger rhs_int = rhs.toBigInteger();
		BigInteger result = m_val.multiply(rhs_int);
		return new PSSIntVal(result);
	}

    @Override
	public PSSVal Div(PSSVal rhs) {
		BigInteger rhs_int = rhs.toBigInteger();
		BigInteger result = m_val.divide(rhs_int);
		return new PSSIntVal(result);
	}

    @Override
	public PSSVal Mod(PSSVal rhs) {
		BigInteger rhs_int = rhs.toBigInteger();
		BigInteger result = m_val.mod(rhs_int);
		return new PSSIntVal(result);
	}

    @Override
	public PSSVal LeftShift(PSSVal rhs) {
		int rhs_int = rhs.toInt();
		BigInteger result = m_val.shiftLeft(rhs_int);
		return new PSSIntVal(result);
	}

    @Override
	public PSSVal RightShift(PSSVal rhs) {
		int rhs_int = rhs.toInt();
		BigInteger result = m_val.shiftRight(rhs_int);
		return new PSSIntVal(result);
	}

    @Override
	public PSSVal BitwiseAnd(PSSVal rhs) {
		BigInteger rhs_int = rhs.toBigInteger();
		BigInteger result = m_val.and(rhs_int);
		return new PSSIntVal(result);
	}

    @Override
	public PSSVal BitwiseOr(PSSVal rhs) {
		BigInteger rhs_int = rhs.toBigInteger();
		BigInteger result = m_val.or(rhs_int);
		return new PSSIntVal(result);
	}

    @Override
	public PSSVal BitwiseXor(PSSVal rhs) {
		BigInteger rhs_int = rhs.toBigInteger();
		BigInteger result = m_val.xor(rhs_int);
		return new PSSIntVal(result);
	}

    @Override
	public PSSBoolVal InRange(PSSVal lhs) {
		return Equal(lhs);
	}

    @Override
	public PSSVal BitwiseNot() {
		BigInteger result = m_val.not();
		return new PSSIntVal(result);
	}

    @Override
	public PSSVal UnaryMinus() {
		BigInteger result = m_val.negate();
		return new PSSIntVal(result);
	}

    @Override
	public PSSVal join(PSSVal val) {
		if (val.isRangeVal()) {
			BigInteger min = val.getMin().toBigInteger();
			BigInteger max = val.getMax().toBigInteger();
			if ((m_val.compareTo(min) >= 0) && (m_val.compareTo(max) <= 0)) {
				return new PSSIntVal(m_val);
			} else {
				return null;
			}
		} else {
			if (m_val.compareTo(val.toBigInteger()) == 0) {
				return new PSSIntVal(m_val);
			} else {
				return null;
			}
		}
	}

	// Adjust Range
    @Override
	public PSSVal reduceLessEqual(PSSVal val) {
		BigInteger val_int = val.toBigInteger();
		if (m_val.compareTo(val_int) <= 0) {
			return new PSSIntVal(m_val);
		} else {
			return null;
		}
	}

    @Override
	public PSSVal reduceGreaterEqual(PSSVal val) {
		BigInteger val_int = val.toBigInteger();
		if (m_val.compareTo(val_int) >= 0) {
			return new PSSIntVal(m_val);
		} else {
			return null;
		}
	}

	@Override
	public int compareTo(PSSVal o) {
		if (o instanceof PSSIntVal) {
			PSSIntVal i = (PSSIntVal) o;
			return m_val.compareTo(i.m_val);
		}
		return super.compareTo(o);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof PSSIntVal) {
			PSSIntVal i = (PSSIntVal) o;
			return m_val.equals(i.m_val);
		}
		return false;
	}

}
