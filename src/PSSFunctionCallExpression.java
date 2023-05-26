import java.util.List;

/**
 * This class models function call expressions.
 */
public class PSSFunctionCallExpression extends PSSExpression {

	/** a function call expression */
	PSSMemberPathElemExpression m_path;

	/**
	 * Constructs a function call expression.
	 *
	 * @param path a path to an instance
	 * @param id   a method name of the instance
	 * @param args the arguments for the method call
	 */
	PSSFunctionCallExpression(PSSMemberPathElemExpression path, String id, List<PSSExpression> args) {
		m_path = new PSSMemberPathElemExpression(id, args, null);
		if (path != null) {
			path.appendLeaf(m_path);
			m_path = path;
		}
	}

	/**
	 * Constructs a function call expression.
	 *
	 * @param id   a method name
	 * @param args the arguments for the method call
	 */
	PSSFunctionCallExpression(String id, List<PSSExpression> args) {
		this(null, id, args);
	}

	@Override
	public PSSVal eval(PSSInst var) {
		return m_path.eval(var);
	}

	@Override
	public String getText() {
		return m_path.getText();
	}

	@Override
	public String toString() {
		return getText();
	}

}
