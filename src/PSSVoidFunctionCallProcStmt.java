
/**
 * This class models (void) function call statements.
 */
public class PSSVoidFunctionCallProcStmt extends PSSProcStmt {

	/** the function call expression */
	PSSFunctionCallExpression func = null;

	/**
	 * Constructs a function call statement.
	 *
	 * @param func a function call expression
	 */
	PSSVoidFunctionCallProcStmt(PSSFunctionCallExpression func) {
		this.func = func;
	}

	@Override
	public void eval(PSSInst var) {
		if (func != null)
			func.eval(var);
	}

}
