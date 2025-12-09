import java.math.*;

/**
 * A {@code PSSBoolVal} can be either {@link #TRUE} or {@link #FALSE}.
 */
public class PSSBoolVal extends PSSVal {

	private boolean m_val;

	/** the Boolean true */
	public static final PSSBoolVal TRUE = new PSSBoolVal(true);

	/** the Boolean false */
	public static final PSSBoolVal FALSE = new PSSBoolVal(false);

	private PSSBoolVal(boolean val) {
		super(PSSBoolModel.getInstance());
		m_val = val;
	}

	/**
	 * Returns {@code #True} if the input boolean is {@code true}; otherwise returns
	 * {@code #False}.
	 *
	 * @param val a Boolean value
	 * @return {@code #True} if the input boolean is {@code true}; otherwise
	 *         {@code #False}
	 */
	public static PSSBoolVal valueOf(boolean val) {
		return val ? TRUE : FALSE;
	}

	/**
	 * Returns {@code #True} if the input string is not null and is equal, ignoring
	 * case, to the string "true".
	 *
	 * @param text a string
	 * @return {@code #True} if {@code text} is not null and is equal, ignoring
	 *         case, to the string "true"
	 */
	public static PSSBoolVal valueOf(String text) {
		return Boolean.parseBoolean(text) ? TRUE : FALSE;
	}

	@Override
	public String getText() {
		return String.valueOf(m_val);
	}

	@Override
	public PSSVal join(PSSVal val) {
		if (val.isRangeVal()) {
			if (GreaterEqual(val.getMin()).toBool() && LessEqual(val.getMax()).toBool()) {
				return new PSSIntVal(toBigInteger());
			} else {
				return null;
			}
		} else {
			if (Equal(val).toBool()) {
				return new PSSIntVal(toBigInteger());
			} else {
				return null;
			}
		}
	}

	@Override
	public BigInteger toBigInteger() {
		return (m_val) ? BigInteger.ONE : BigInteger.ZERO;
	}

	@Override
	public int toInt() {
		PSSMessage.Error("INTVAL", "BoolVal cannot be converted to int");
		return 0;
	}

	@Override
	public String toStr() {
		PSSMessage.Error("INTVAL", "IntVal cannot be converted to string");
		return "";
	}

	@Override
	public boolean toBool() {
		return m_val;
	}

	@Override
	public PSSVal extract (int msb, int lsb) {
		if (msb < lsb) PSSMessage.Fatal(getClass().getSimpleName() + "::extract(msb,lsb): msb should not small than lsb");
		if (msb < 0 || lsb < 0) PSSMessage.Fatal(getClass().getSimpleName() + "::extract(msb,lsb): msb/lsb should be non-neg value");
		if (msb != lsb) PSSMessage.Warning("Extract more than 1 bit from boolean-type is meanless");
		if (lsb > 0) PSSMessage.Warning("Extract other than index 0 from boolean-type is meanless");

		return (lsb == 0 && m_val) ? TRUE : FALSE;
	}

	@Override
	public PSSBoolVal Equal (PSSVal rhs) {
		BigInteger lhs_int = toBigInteger();
		BigInteger rhs_int = rhs.toBigInteger();
		return (lhs_int.compareTo(rhs_int) == 0) ? TRUE : FALSE;
	}

	@Override
	public PSSBoolVal NotEqual (PSSVal rhs) {
		return Equal(rhs).LogicalNot();
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
		BigInteger lhs_int = toBigInteger();
		return (lhs_int.compareTo(rhs) > 0) ? TRUE : FALSE;
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
		BigInteger lhs_int = toBigInteger();
		return (lhs_int.compareTo(rhs) >= 0) ? TRUE : FALSE;
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
		BigInteger lhs_int = toBigInteger();
		return (lhs_int.compareTo(rhs) <= 0) ? TRUE : FALSE;
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
		BigInteger lhs_int = toBigInteger();
		return (lhs_int.compareTo(rhs) < 0) ? TRUE : FALSE;
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
		BigInteger lhs_int = toBigInteger();
		BigInteger result  = lhs_int.add(rhs);
		if (result.testBit(0)) {
			return new PSSIntVal(BigInteger.ONE);
		} else {
			return new PSSIntVal(BigInteger.ZERO);
		}
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
		BigInteger lhs_int = toBigInteger();
		BigInteger result  = lhs_int.subtract(rhs);
		if (result.testBit(0)) {
			return new PSSIntVal(BigInteger.ONE);
		} else {
			return new PSSIntVal(BigInteger.ZERO);
		}
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
		BigInteger lhs_int = toBigInteger();
		BigInteger result  = lhs_int.multiply(rhs);
		if (result.testBit(0)) {
			return new PSSIntVal(BigInteger.ONE);
		} else {
			return new PSSIntVal(BigInteger.ZERO);
		}
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
		BigInteger lhs_int = toBigInteger();
		BigInteger result  = lhs_int.divide(rhs);
		if (result.testBit(0)) {
			return new PSSIntVal(BigInteger.ONE);
		} else {
			return new PSSIntVal(BigInteger.ZERO);
		}
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
		BigInteger lhs_int = toBigInteger();
		BigInteger result  = lhs_int.mod(rhs);
		if (result.testBit(0)) {
			return new PSSIntVal(BigInteger.ONE);
		} else {
			return new PSSIntVal(BigInteger.ZERO);
		}
	}

	@Override
	public PSSVal LeftShift (PSSVal rhs) {
		int rhs_int = rhs.toInt();
		return LeftShift(rhs_int);
	}
	@Override
	public PSSVal LeftShift (int rhs) {
		BigInteger lhs_int = toBigInteger();
		BigInteger result  = lhs_int.shiftLeft(rhs);
		if (result.testBit(0)) {
			return new PSSIntVal(BigInteger.ONE);
		} else {
			return new PSSIntVal(BigInteger.ZERO);
		}
	}

	@Override
	public PSSVal RightShift (PSSVal rhs) {
		int rhs_int = rhs.toInt();
		return RightShift(rhs_int);
	}
	@Override
	public PSSVal RightShift (int rhs) {
		BigInteger lhs_int = toBigInteger();
		BigInteger result  = lhs_int.shiftLeft(rhs);
		if (result.testBit(0)) {
			return new PSSIntVal(BigInteger.ONE);
		} else {
			return new PSSIntVal(BigInteger.ZERO);
		}
	}

	@Override
	public PSSBoolVal LogicalAnd (PSSVal rhs) {
		boolean rhs_val = rhs.toBool();
		return new PSSBoolVal(m_val && rhs_val);
	}
	@Override
	public PSSBoolVal LogicalAnd (int rhs) {
		return new PSSBoolVal(m_val && (rhs != 0));
	}

	@Override
	public PSSBoolVal LogicalOr (PSSVal rhs) {
		boolean rhs_val = rhs.toBool();
		return new PSSBoolVal(m_val || rhs_val);
	}
	@Override
	public PSSBoolVal LogicalOr (int rhs) {
		return new PSSBoolVal(m_val || (rhs != 0));
	}

	@Override
	public PSSBoolVal InRange(PSSVal lhs) {
		return Equal(lhs);
	}

	@Override
	public PSSBoolVal LogicalNot() {
		return new PSSBoolVal(!m_val);
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
		BigInteger lhs_int = toBigInteger();
		BigInteger result = lhs_int.and(rhs);
		if (result.testBit(0)) {
			return new PSSIntVal(BigInteger.ONE);
		} else {
			return new PSSIntVal(BigInteger.ZERO);
		}
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
		BigInteger lhs_int = toBigInteger();
		BigInteger result = lhs_int.or(rhs);
		if (result.testBit(0)) {
			return new PSSIntVal(BigInteger.ONE);
		} else {
			return new PSSIntVal(BigInteger.ZERO);
		}
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
		BigInteger lhs_int = toBigInteger();
		BigInteger result = lhs_int.xor(rhs);
		if (result.testBit(0)) {
			return new PSSIntVal(BigInteger.ONE);
		} else {
			return new PSSIntVal(BigInteger.ZERO);
		}
	}

	@Override
	public PSSVal BitwiseNot() {
		return (m_val) ? new PSSIntVal(BigInteger.ZERO) : new PSSIntVal(BigInteger.ONE);
	}

	@Override
	public int compareTo(PSSVal o) {
		if (o instanceof PSSBoolVal) {
			PSSBoolVal b = (PSSBoolVal) o;
			return Boolean.valueOf(m_val).compareTo(Boolean.valueOf(b.m_val));
		}
		return super.compareTo(o);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof PSSBoolVal) {
			PSSBoolVal b = (PSSBoolVal) o;
			return m_val == b.m_val;
		}
		return false;
	}

}
