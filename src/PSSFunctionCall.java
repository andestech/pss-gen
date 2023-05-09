import java.util.*;

public class PSSFunctionCall extends PSSExpression {
	PSSHierarchicalIDExpression m_path;
	String m_id;
	List<PSSExpression> m_args = null;

	PSSFunctionCall(PSSHierarchicalIDExpression path, String id, List<PSSExpression> args) {
		m_path = path;
		m_id = id;
		m_args = args;
	}

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
