package pssgen.proc;

import java.util.*;

import pssgen.expr.PSSExpression;
import pssgen.inst.PSSInst;
import pssgen.inst.PSSNamespaceInst;

public class PSSWhileProcStmt extends PSSProcStmt {

	private PSSExpression m_cond;
	private PSSProcStmt m_stmt;

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

	public final PSSExpression getCond() {
		return m_cond;
	}

	public final PSSProcStmt getStmt() {
		return m_stmt;
	}
}
