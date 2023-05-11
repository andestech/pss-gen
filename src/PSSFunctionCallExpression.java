import java.util.*;

/**
 * This class models function call expressions.
 */
public class PSSFunctionCallExpression extends PSSExpression {

	/** a path to an instance */
	PSSHierarchicalIDExpression m_path;

	/** a method name of the instance */
	String m_id;

	/** the arguments for the method call */
	List<PSSExpression> m_args = null;

	/**
	 * Constructs a function call expression.
	 *
	 * @param path a path to an instance
	 * @param id   a method name of the instance
	 * @param args the arguments for the method call
	 */
	PSSFunctionCallExpression(PSSHierarchicalIDExpression path, String id, List<PSSExpression> args) {
		m_path = path;
		m_id = id;
		m_args = args;
	}

	@Override
	public PSSVal eval(PSSInst var) {
		PSSMessage.Debug("[PSSFunctionCall] calling function " + m_id + " of " + m_path);
		/* Evaluate arguments */
		List<PSSVal> vals = new ArrayList<PSSVal>();
		for (PSSExpression arg : m_args) {
			vals.add(arg.eval(var));
		}
		PSSInst inst = m_path.getInst(var);
		return inst.evalMethod(m_id, vals);
	}

}
