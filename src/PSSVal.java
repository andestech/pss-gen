import java.math.*;

/**
 * This class provides a default implementation of values. All operations are
 * not implemented by default except:
 * <ul>
 * <li>{@code isRangeVal}: {@code false} by default</li>
 * <li>{@code isBound}: {@code true} by default</li>
 * </ul>
 */
public abstract class PSSVal implements Comparable<PSSVal> {

	/** the type of this value */
	PSSModel m_type;

	public PSSVal(PSSModel type) {
		m_type = type;
	}

	/**
	 * Returns the type model of this value.
	 *
	 * @return the type model of this value
	 */
	public PSSModel getTypeModel() {
		return m_type;
	}

	/**
	 * Sets the type model of this value.
	 *
	 * @param type the type model of this value
	 */
	public void setTypeModel(PSSModel type) {
		m_type = type;
	}

	public String getText() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::getText is not implemented");
		return "";
	}

	public String toTargetCode() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::toTargetCode is not implemented");
		return "";
	}

	/**
	 * Returns {@code true} if this value is a range.
	 *
	 * @return {@code true} if this value is a range
	 */
	public boolean isRangeVal() {
		return false;
	}

	public boolean isBound() {
		return true;
	}

	public PSSVal join(PSSVal val) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::join is not implemented");
		return null;
	}

	/**
	 * Converts this value to a big integer.
	 *
	 * @return the converted big integer
	 */
	public BigInteger toBigInteger() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::toBigInteger is not implemented");
		return null;
	}

	/**
	 * Converts this value to an integer according PSS documentation.
	 *
	 * @return the converted integer
	 */
	public int toInt() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::toInt is not implemented");
		return 0;
	}

	/**
	 * Converts this value to a string according PSS documentation.
	 *
	 * @return the converted string
	 */
	public String toStr() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::toStr is not implemented");
		return "";
	}

	/**
	 * Converts this value to a boolean according PSS documentation.
	 *
	 * @return the converted boolean
	 */
	public boolean toBool() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::toBool is not implemented");
		return false;
	}

	/**
	 * Returns a random value in this range.
	 *
	 * @return a random value in this range
	 */
	public PSSVal randomIn() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::randomIn is not implemented");
		return null;
	}

	// Binary Operators

	/**
	 * "==" operator
	 *
	 * @param rhs
	 * @return
	 */
	public PSSBoolVal Equal(PSSVal rhs) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::Equal is not implemented");
		return null;
	}

	/**
	 * "!=" operator
	 *
	 * @param rhs
	 * @return
	 */
	public PSSBoolVal NotEqual(PSSVal rhs) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::NotEqual is not implemented");
		return null;
	}

	/**
	 * ">" operator, numeric only
	 *
	 * @param rhs
	 * @return
	 */
	public PSSBoolVal GreaterThan(PSSVal rhs) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::GreaterThan is not implemented");
		return null;
	}

	/**
	 * ">=" operator, numeric only
	 *
	 * @param rhs
	 * @return
	 */
	public PSSBoolVal GreaterEqual(PSSVal rhs) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::GreaterEqual is not implemented");
		return null;
	}

	/**
	 * "<=" operator, numeric only
	 *
	 * @param rhs
	 * @return
	 */
	public PSSBoolVal LessEqual(PSSVal rhs) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::LessEqual is not implemented");
		return null;
	}

	/**
	 * "<" operator, numeric only
	 *
	 * @param rhs
	 * @return
	 */
	public PSSBoolVal LessThan(PSSVal rhs) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::LessThan is not implemented");
		return null;
	}

	/**
	 * "+" operator, numeric only
	 *
	 * @param rhs
	 * @return
	 */
	public PSSVal Add(PSSVal rhs) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::Add is not implemented");
		return null;
	}

	/**
	 * "-" operator, numeric only
	 *
	 * @param rhs
	 * @return
	 */
	public PSSVal Sub(PSSVal rhs) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::Sub is not implemented");
		return null;
	}

	/**
	 * "*" operator, numeric only
	 *
	 * @param rhs
	 * @return
	 */
	public PSSVal Mul(PSSVal rhs) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::Mul is not implemented");
		return null;
	}

	/**
	 * "/" operator, numeric only
	 *
	 * @param rhs
	 * @return
	 */
	public PSSVal Div(PSSVal rhs) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::Div is not implemented");
		return null;
	}

	/**
	 * "%" operator, numeric only
	 *
	 * @param rhs
	 * @return
	 */
	public PSSVal Mod(PSSVal rhs) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::Mod is not implemented");
		return null;
	}

	/**
	 * "<<" operator, numeric only
	 *
	 * @param rhs
	 * @return
	 */
	public PSSVal LeftShift(PSSVal rhs) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::LeftShift is not implemented");
		return null;
	}

	/**
	 * ">>" operator, numeric only
	 *
	 * @param rhs
	 * @return
	 */
	public PSSVal RightShift(PSSVal rhs) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::RightShift is not implemented");
		return null;
	}

	/**
	 * "&&" operator, boolean only
	 *
	 * @param rhs
	 * @return
	 */
	public PSSBoolVal LogicalAnd(PSSVal rhs) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::LogicalAnd is not implemented");
		return null;
	}

	/**
	 * "||" operator, boolean only
	 *
	 * @param rhs
	 * @return
	 */
	public PSSBoolVal LogicalOr(PSSVal rhs) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::LogicalOr is not implemented");
		return null;
	}

	/**
	 * "in" operator
	 *
	 * @param rhs
	 * @return
	 */
	public PSSBoolVal InRange(PSSVal lhs) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::InRange is not implemented");
		return null;
	}

	/**
	 * "!" operator, boolean only
	 *
	 * @param rhs
	 * @return
	 */
	public PSSBoolVal LogicalNot() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::LogicalOr is not implemented");
		return null;
	}

	/**
	 * "&" operator, numeric only
	 *
	 * @param rhs
	 * @return
	 */
	public PSSVal BitwiseAnd(PSSVal rhs) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::BitwiseAnd is not implemented");
		return null;
	}

	/**
	 * "|" operator, numeric only
	 *
	 * @param rhs
	 * @return
	 */
	public PSSVal BitwiseOr(PSSVal rhs) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::BitwiseOr is not implemented");
		return null;
	}

	/**
	 * "^" operator, numeric only
	 *
	 * @param rhs
	 * @return
	 */
	public PSSVal BitwiseXor(PSSVal rhs) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::BitwiseXor is not implemented");
		return null;
	}

	/**
	 * Unary "^" operator, numeric only
	 *
	 * @return
	 */
	public PSSVal BitwiseNot() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::BitwiseNot is not implemented");
		return null;
	}

	/**
	 * Unary "-" operator, numeric only
	 *
	 * @param rhs
	 * @return
	 */
	public PSSVal UnaryMinus() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::UnaryMinus is not implemented");
		return null;
	}

	/**
	 * If this value is a range, returns the minimal value in this range.
	 *
	 * @return the minimal value in this range
	 */
	public PSSVal getMin() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::getMin is not implemented");
		return null;
	}

	/**
	 * If this value is a range, returns the maximal value in this range.
	 *
	 * @return the maximal value in this range
	 */
	public PSSVal getMax() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::getMax is not implemented");
		return null;
	}

	/**
	 * "[]" operator, bit-select
	 *
	 * @param rhs
	 * @return
	 */
	public PSSVal indexOf(PSSVal index) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::indexOf is not implemented");
		return null;
	}

	// Adjust Range
	public PSSVal reduceLessEqual(PSSVal val) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::reduceLessEqual is not implemented");
		return null;
	}

	public PSSVal reduceGreaterEqual(PSSVal val) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::reduceGreaterEqual is not implemented");
		return null;
	}

	@Override
	public int compareTo(PSSVal o) {
		if (o == null) {
			return 1;
		}
		int res = getClass().getCanonicalName().compareTo(o.getClass().getCanonicalName());
		if (res != 0)
			return res;
		return getText().compareTo(o.getText());
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof PSSVal) {
			PSSVal v = (PSSVal) o;
			return getText().equals(v.getText());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getText().hashCode();
	}

}
