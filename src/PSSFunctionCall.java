import java.util.*;


public class PSSFunctionCall extends PSSExpression {
	String m_path;
	String m_id;
	List<PSSExpression> args = null;

	PSSFunctionCall(String path, String id, List<PSSExpression> args) {
		m_path = path;
		m_id = id;
		this.args = args;
	}

	public PSSVal eval(PSSInst var) {
		PSSMessage.Debug("[PSSFunctionCall] calling function " + m_id + " of " + m_path);
		/* Evaluate arguments */
		List<PSSVal> vals = new ArrayList<PSSVal>();
		for (PSSExpression arg : args) {
			vals.add(arg.eval(var));
		}
		PSSInst inst = var;
		for (String p : m_path.split("\\.")) {
			if (p != "") {
				System.out.println("Find instance: " + p);
				inst = inst.findInstance(p);
			}
		}
		return inst.evalMethod(m_id, vals);
	}

}

