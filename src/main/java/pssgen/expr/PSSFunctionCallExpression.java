package pssgen.expr;

import java.util.List;
import java.util.ArrayList;

import pssgen.inst.PSSInst;
import pssgen.inst.PSSScope;
import pssgen.value.PSSVal;

/**
 * This class models function call expressions.
 */
public class PSSFunctionCallExpression extends PSSExpression {

	/** a function call expression */
	PSSMemberPathElemExpression m_path;

	/**
	 * Constructs a function call expression.
	 *
	 * @param path    a path to an instance
	 * @param id      a method name of the instance
	 * @param args    the arguments for the method call
	 * @param context where the call is written
	 */
	public PSSFunctionCallExpression(PSSMemberPathElemExpression path, String id, List<PSSExpression> args,
			PSSScope.Context context) {
		m_path = new PSSMemberPathElemExpression(id, args, null, context);
		if (path != null) {
			path.appendLeaf(m_path);
			m_path = path;
		}
	}


	@Override
	public PSSVal eval(PSSInst var) {
		return m_path.eval(var);
	}

	@Override
	public ArrayList<PSSInst> getInsts(PSSInst var) {
		var ret = new ArrayList<PSSInst>();
		ret.addAll(m_path.getInsts(var));
		return ret;
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
