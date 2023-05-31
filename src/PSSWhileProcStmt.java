import java.util.*;

public class PSSWhileProcStmt extends PSSProcStmt {

	public PSSExpression m_cond;
	public PSSProcStmt m_stmt;

	public PSSWhileProcStmt(PSSExpression cond, PSSProcStmt stmt) {
		m_cond = cond;
		m_stmt = stmt;
	}

	public void eval(PSSInst inst) {
            try {
		while (m_cond.eval(inst).toBool()) {
                    try {
			m_stmt.eval(inst);
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
