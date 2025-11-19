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
	public PSSBoolVal Equal(PSSVal rhs) {
		BigInteger lhs_int = toBigInteger();
		BigInteger rhs_int = rhs.toBigInteger();
		return (lhs_int.compareTo(rhs_int) == 0) ? TRUE : FALSE;
	}

	@Override
	public PSSBoolVal NotEqual(PSSVal rhs) {
		return Equal(rhs).LogicalNot();
	}

    @Override
	public PSSBoolVal GreaterThan(PSSVal rhs) {
		BigInteger lhs_int = toBigInteger();
		BigInteger rhs_int = rhs.toBigInteger();
		return (lhs_int.compareTo(rhs_int) > 0) ? TRUE : FALSE;
	}

    @Override
	public PSSBoolVal GreaterEqual(PSSVal rhs) {
		BigInteger lhs_int = toBigInteger();
		BigInteger rhs_int = rhs.toBigInteger();
		return (lhs_int.compareTo(rhs_int) >= 0) ? TRUE : FALSE;
	}

    @Override
	public PSSBoolVal LessThan(PSSVal rhs) {
		BigInteger lhs_int = toBigInteger();
		BigInteger rhs_int = rhs.toBigInteger();
		return (lhs_int.compareTo(rhs_int) < 0) ? TRUE : FALSE;
	}

    @Override
	public PSSBoolVal LessEqual(PSSVal rhs) {
		BigInteger lhs_int = toBigInteger();
		BigInteger rhs_int = rhs.toBigInteger();
		return (lhs_int.compareTo(rhs_int) <= 0) ? TRUE : FALSE;
	}

	@Override
	public PSSBoolVal LogicalAnd(PSSVal rhs) {
		boolean rhs_val = rhs.toBool();
		return new PSSBoolVal(m_val && rhs_val);
	}

	@Override
	public PSSBoolVal LogicalOr(PSSVal rhs) {
		boolean rhs_val = rhs.toBool();
		return new PSSBoolVal(m_val || rhs_val);
	}

	@Override
	public PSSBoolVal LogicalNot() {
		return new PSSBoolVal(!m_val);
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
