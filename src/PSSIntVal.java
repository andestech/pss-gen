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
	public PSSVal extract (int msb, int lsb) {
		if (msb < lsb) PSSMessage.Fatal(getClass().getSimpleName() + "::extract(msb,lsb): msb should not small than lsb");
		if (msb < 0 || lsb < 0) PSSMessage.Fatal(getClass().getSimpleName() + "::extract(msb,lsb): msb/lsb should be non-neg value");
		BigInteger mask = BigInteger.valueOf((1 << (msb - lsb + 1)) - 1);
		BigInteger ret = m_val.shiftRight(lsb);
		ret = ret.and(mask);
		return new PSSIntVal(ret);
	}

	@Override
	public PSSBoolVal Equal (PSSVal rhs) {
		BigInteger rhs_int = rhs.toBigInteger();
		return Equal(rhs_int);
	}
	@Override
	public PSSBoolVal Equal (int rhs) {
		BigInteger rhs_int = BigInteger.valueOf(rhs);
		return Equal(rhs_int);
	}
	protected PSSBoolVal Equal (BigInteger rhs) {
		boolean result = m_val.equals(rhs);
		return PSSBoolVal.valueOf(result);
	}

	@Override
	public PSSBoolVal NotEqual (PSSVal rhs) {
		BigInteger rhs_int = rhs.toBigInteger();
		return NotEqual(rhs_int);
	}
	@Override
	public PSSBoolVal NotEqual (int rhs) {
		BigInteger rhs_int = BigInteger.valueOf(rhs);
		return NotEqual(rhs_int);
	}
	protected PSSBoolVal NotEqual (BigInteger rhs) {
		boolean result = m_val.equals(rhs);
		return PSSBoolVal.valueOf(!result);
	}


	@Override
	public PSSBoolVal GreaterThan (PSSVal rhs) {
		BigInteger rhs_int = rhs.toBigInteger();
		return GreaterThan(rhs_int);
	}
	@Override
	public PSSBoolVal GreaterThan (int rhs) {
		BigInteger rhs_int = BigInteger.valueOf(rhs);
		return GreaterThan(rhs_int);
	}
	protected PSSBoolVal GreaterThan (BigInteger rhs) {
		boolean result = m_val.compareTo(rhs) == 1;
		return PSSBoolVal.valueOf(result);
	}

	@Override
	public PSSBoolVal GreaterEqual (PSSVal rhs) {
		BigInteger rhs_int = rhs.toBigInteger();
		return GreaterEqual(rhs_int);
	}
	@Override
	public PSSBoolVal GreaterEqual (int rhs) {
		BigInteger rhs_int = BigInteger.valueOf(rhs);
		return GreaterEqual(rhs_int);
	}
	protected PSSBoolVal GreaterEqual (BigInteger rhs) {
		boolean result = m_val.compareTo(rhs) >= 0;
		return PSSBoolVal.valueOf(result);
	}

	@Override
	public PSSBoolVal LessEqual (PSSVal rhs) {
		BigInteger rhs_int = rhs.toBigInteger();
		return LessEqual(rhs_int);
	}
	@Override
	public PSSBoolVal LessEqual (int rhs) {
		BigInteger rhs_int = BigInteger.valueOf(rhs);
		return LessEqual(rhs_int);
	}
	protected PSSBoolVal LessEqual (BigInteger rhs) {
		boolean result = m_val.compareTo(rhs) <= 0;
		return PSSBoolVal.valueOf(result);
	}

	@Override
	public PSSBoolVal LessThan (PSSVal rhs) {
		BigInteger rhs_int = rhs.toBigInteger();
		return LessThan(rhs_int);
	}
	@Override
	public PSSBoolVal LessThan (int rhs) {
		BigInteger rhs_int = BigInteger.valueOf(rhs);
		return LessThan(rhs_int);
	}
	protected PSSBoolVal LessThan (BigInteger rhs) {
		boolean result = m_val.compareTo(rhs) < 0;
		return PSSBoolVal.valueOf(result);
	}

	@Override
	public PSSVal Add (PSSVal rhs) {
		BigInteger rhs_int = rhs.toBigInteger();
		return Add(rhs_int);
	}
	@Override
	public PSSVal Add (int rhs) {
		BigInteger rhs_int = BigInteger.valueOf(rhs);
		return Add(rhs_int);
	}
	protected PSSVal Add (BigInteger rhs) {
		BigInteger result = m_val.add(rhs);
		return new PSSIntVal(result);
	}

	@Override
	public PSSVal Sub (PSSVal rhs) {
		BigInteger rhs_int = rhs.toBigInteger();
		return Sub(rhs_int);
	}
	@Override
	public PSSVal Sub (int rhs) {
		BigInteger rhs_int = BigInteger.valueOf(rhs);
		return Sub(rhs_int);
	}
	protected PSSVal Sub (BigInteger rhs) {
		BigInteger result = m_val.subtract(rhs);
		return new PSSIntVal(result);
	}

	@Override
	public PSSVal Mul (PSSVal rhs) {
		BigInteger rhs_int = rhs.toBigInteger();
		return Mul(rhs_int);
	}
	@Override
	public PSSVal Mul (int rhs) {
		BigInteger rhs_int = BigInteger.valueOf(rhs);
		return Mul(rhs_int);
	}
	protected PSSVal Mul (BigInteger rhs) {
		BigInteger result = m_val.multiply(rhs);
		return new PSSIntVal(result);
	}

	@Override
	public PSSVal Div (PSSVal rhs) {
		BigInteger rhs_int = rhs.toBigInteger();
		return Div(rhs_int);
	}
	@Override
	public PSSVal Div (int rhs) {
		BigInteger rhs_int = BigInteger.valueOf(rhs);
		return Div(rhs_int);
	}
	protected PSSVal Div (BigInteger rhs) {
		BigInteger result = m_val.divide(rhs);
		return new PSSIntVal(result);
	}

	@Override
	public PSSVal Mod (PSSVal rhs) {
		BigInteger rhs_int = rhs.toBigInteger();
		return Mod(rhs_int);
	}
	@Override
	public PSSVal Mod (int rhs) {
		BigInteger rhs_int = BigInteger.valueOf(rhs);
		return Mod(rhs_int);
	}
	protected PSSVal Mod (BigInteger rhs) {
		BigInteger result = m_val.mod(rhs);
		return new PSSIntVal(result);
	}

	@Override
	public PSSVal LeftShift (PSSVal rhs) {
		int rhs_int = rhs.toInt();
		return LeftShift(rhs_int);
	}
	@Override
	public PSSVal LeftShift (int rhs) {
		BigInteger result = m_val.shiftLeft(rhs);
		return new PSSIntVal(result);
	}

	@Override
	public PSSVal RightShift (PSSVal rhs) {
		int rhs_int = rhs.toInt();
		return RightShift(rhs_int);
	}
	@Override
	public PSSVal RightShift (int rhs) {
		BigInteger result = m_val.shiftRight(rhs);
		return new PSSIntVal(result);
	}

	@Override
	public PSSBoolVal InRange(PSSVal lhs) {
		return Equal(lhs);
	}

	@Override
	public PSSVal BitwiseAnd (PSSVal rhs) {
		BigInteger rhs_int = rhs.toBigInteger();
		return BitwiseAnd(rhs_int);
	}
	@Override
	public PSSVal BitwiseAnd (int rhs) {
		BigInteger rhs_int = BigInteger.valueOf(rhs);
		return BitwiseAnd(rhs_int);
	}
	protected PSSVal BitwiseAnd (BigInteger rhs) {
		BigInteger result = m_val.and(rhs);
		return new PSSIntVal(result);
	}

	@Override
	public PSSVal BitwiseOr (PSSVal rhs) {
		BigInteger rhs_int = rhs.toBigInteger();
		return BitwiseOr(rhs_int);
	}
	@Override
	public PSSVal BitwiseOr (int rhs) {
		BigInteger rhs_int = BigInteger.valueOf(rhs);
		return BitwiseOr(rhs_int);
	}
	protected PSSVal BitwiseOr (BigInteger rhs) {
		BigInteger result = m_val.or(rhs);
		return new PSSIntVal(result);
	}

	@Override
	public PSSVal BitwiseXor (PSSVal rhs) {
		BigInteger rhs_int = rhs.toBigInteger();
		return BitwiseXor(rhs_int);
	}
	@Override
	public PSSVal BitwiseXor (int rhs) {
		BigInteger rhs_int = BigInteger.valueOf(rhs);
		return BitwiseXor(rhs_int);
	}
	protected PSSVal BitwiseXor (BigInteger rhs) {
		BigInteger result = m_val.xor(rhs);
		return new PSSIntVal(result);
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
