package pssgen.expr;

import java.util.List;
import java.util.ArrayList;

import pssgen.inst.PSSInst;
import pssgen.inst.PSSScope;
import pssgen.model.PSSModel;
import pssgen.util.PSSMessage;
import pssgen.value.PSSVal;

/**
 * This class models function call expressions.
 */
public class PSSFunctionCallExpression extends PSSExpression {

	/** a function call expression */
	PSSMemberPathElemExpression m_path;

	/** the qualifying package, or null */
	private String m_package;

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


	/**
	 * Sets the package that qualifies the call, as in {@code p::f()}.
	 *
	 * @param pkg the qualifying type identifiers joined by {@code ::}
	 */
	public void setPackage(String pkg) {
		m_package = pkg;
	}

	/** The package model of the qualifier. */
	private PSSModel findPackage(PSSInst var) {
		PSSModel m = var.getTypeModel().findDeclaration(m_package);
		if (m == null)
			PSSMessage.Error("", "Function " + getText() + " is not defined.");
		return m;
	}

	@Override
	public PSSVal eval(PSSInst var) {
		if (m_package == null)
			return m_path.eval(var);
		return m_path.eval(findPackage(var), var);
	}

	@Override
	public ArrayList<PSSInst> getInsts(PSSInst var) {
		var ret = new ArrayList<PSSInst>();
		if (m_package == null)
			ret.addAll(m_path.getInsts(var));
		else
			ret.add(m_path.getInst(findPackage(var), var));
		return ret;
	}

	@Override
	public String getText() {
		return (m_package == null ? "" : m_package + "::") + m_path.getText();
	}

	@Override
	public String toString() {
		return getText();
	}

}
