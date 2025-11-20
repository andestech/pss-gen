import java.util.List;

/**
 * A {@code PSSDomain} represents a domain of values.
 */
public abstract class PSSDomain {

	/**
	 * Constructs this domain.
	 */
	public PSSDomain() {
	}

	/**
	 * Returns {@code true} is this domain is empty.
	 *
	 * @return {@code true} is this domain is empty
	 */
	public abstract boolean isEmpty();

	/**
	 * Returns a random element in this domain.
	 *
	 * @return a random element in this domain, or {@code null} if this domain is
	 *         empty
	 */
	public abstract PSSVal random();

	/**
	 * Returns {@code true} if a specified value is in this domain.
	 *
	 * @param lhs a value
	 * @return {@code true} if {@code lhs} is in this domain
	 */
	public abstract PSSBoolVal InRange(PSSVal lhs);

	/**
	 * Returns the join of this domain and a specified domain.
	 *
	 * @param dom a domain
	 * @return the join of this domain and {@code dom}
	 */
	public abstract PSSDomain join(PSSDomain dom);

	/**
	 * Returns the union of this domain and a specified domain.
	 *
	 * @param dom a domain
	 * @return the union of this domain and {@code dom}
	 */
	public abstract PSSDomain union(PSSDomain dom);

	/**
	 * Returns a domain reduced from this one by keeping only elements equal to a
	 * specified value.
	 *
	 * @param val a value
	 * @return a domain reduced from this one by keeping only elements equal to
	 *         {@code val}
	 */
	public PSSDomain reduceDomainEqual(PSSVal val) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::reduceDomainEqual is not impelemented");
		return null;
	}

	/**
	 * Returns a domain reduced from this one by keeping only elements not equal to
	 * a
	 * specified value.
	 *
	 * @param val a value
	 * @return a domain reduced from this one by keeping only elements not equal to
	 *         {@code val}
	 */
	public PSSDomain reduceDomainNotEqual(PSSVal val) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::reduceDomainNotEqual is not impelemented");
		return null;
	}

	/**
	 * Returns a domain reduced from this one by keeping only elements greater than
	 * a
	 * specified value.
	 *
	 * @param val a value
	 * @return a domain reduced from this one by keeping only elements greater than
	 *         {@code val}
	 */
	public PSSDomain reduceDomainGreaterThan(PSSVal val) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::reduceDomainGreaterThan is not impelemented");
		return null;
	}

	/**
	 * Returns a domain reduced from this one by keeping only elements greater than
	 * or equal to a
	 * specified value.
	 *
	 * @param val a value
	 * @return a domain reduced from this one by keeping only elements greater than
	 *         or equal to {@code val}
	 */
	public PSSDomain reduceDomainGreaterEqual(PSSVal val) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::reduceDomainGreaterEqual is not impelemented");
		return null;
	}

	/**
	 * Returns a domain reduced from this one by keeping only elements less than a
	 * specified value.
	 *
	 * @param val a value
	 * @return a domain reduced from this one by keeping only elements less than
	 *         {@code val}
	 */
	public PSSDomain reduceDomainLessThan(PSSVal val) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::reduceDomainLessThan is not impelemented");
		return null;
	}

	/**
	 * Returns a domain reduced from this one by keeping only elements less than or
	 * equal to a
	 * specified value.
	 *
	 * @param val a value
	 * @return a domain reduced from this one by keeping only elements less than or
	 *         equal to {@code val}
	 */
	public PSSDomain reduceDomainLessEqual(PSSVal val) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::reduceDomainLessEqual is not impelemented");
		return null;
	}

	/**
	 * Returns a domain reduced from this one by keeping only elements in a
	 * specified list.
	 *
	 * @param list of PSSVal
	 * @return a domain reduced from this one by keeping only elements in the set
	 *         {@code val}
	 */
	public PSSDomain reduceDomainIn(List<PSSVal> list) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::reduceDomainIn is not impelemented");
		return null;
	}

	/**
	 * Returns the text representation of this domain.
	 *
	 * @return the text representation of this domain
	 */
	public String getText() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::getText is not impelemented");
		return "";
	}

}
