
public class PSSExpression {

	public PSSExpression() {
	}

	/**
	 * Override left expression.
	 * CAUTION: this API carries potential risks. Ensure you understand the implications before use.
	 * 
	 * @param left
	 * @return void
	 */
	public void setLeftExpression (PSSExpression left) {
		PSSMessage.Fatal(getClass().getSimpleName() + ".setLeftExpression() is not implemented");
	}

	/**
	 * Infers the domains of variables that satisfy this expression.
	 *
	 * @param var
	 * @return domains successfully inferred
	 */
	public PSSDomainMap deduceDomain(PSSInst var) {
		PSSMessage.Fatal(getClass().getSimpleName() + ".deduceDomain() is not implemented");
		return null;
	}

	/**
	 * Evaluates this expression.
	 *
	 * @param var
	 * @return the evaluation result
	 */
	public PSSVal eval(PSSInst var) {
		PSSMessage.Fatal(getClass().getSimpleName() + ".eval() is not implemented");
		return null;
	}

	/**
	 * Returns the instance of this expression.
	 *
	 * @param var
	 * @return the instance of this expression, or {@code null} if this expression
	 *         is not a variable
	 */
	public PSSInst getInst(PSSInst var) {
		return null;
	}

	/**
	 * Returns the text representation of this expression.
	 *
	 * @return the text representation of this expression
	 */
	public String getText() {
		return "";
	}

	@Override
	public int hashCode() {
		return getText().hashCode();
	}

}
