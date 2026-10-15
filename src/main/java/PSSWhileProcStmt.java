import java.util.*;

public class PSSWhileProcStmt extends PSSProcStmt {

	public PSSExpression m_cond;
	public PSSProcStmt m_stmt;

	public PSSWhileProcStmt(PSSExpression cond, PSSProcStmt stmt) {
		m_cond = cond;
		m_stmt = stmt;
	}

	public void eval(PSSInst inst) {
		PSSNamespaceInst body_inst = new PSSNamespaceInst("");
		inst.addInst(body_inst);
		try {
			while (m_cond.eval(inst).toBool()) {
				try {
					m_stmt.eval(body_inst);
				} catch (PSSContinueException dontcare) {
					// continue
				}
			}
		} catch (PSSBreakException dontcare) {
			// break
		}
	}

	public void dump(String indent) {
		String str_cond = m_cond.getText();

		System.out.println(indent + "while (" + str_cond + ") {");
		m_stmt.dump(indent + "\t");
		System.out.println(indent + "}");

	}
}
