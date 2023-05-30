import java.util.*;

public class PSSRepeatWhileProcStmt extends PSSProcStmt {

	public PSSExpression m_cond;
	public PSSProcStmt m_stmt;

	public PSSRepeatWhileProcStmt(PSSExpression cond, PSSProcStmt stmt) {
		m_cond = cond;
		m_stmt = stmt;
	}

	public void eval(PSSInst inst) {
            try {
		do {
                    try {
			m_stmt.eval(inst);
                    } catch (PSSContinueException e) {
                        // continue
                    }
		}while (m_cond.eval(inst).toBool());
            } catch (PSSBreakException e) {
                // break
            }
	}

	public void dump(String indent) {
		String str_cond = m_cond.getText();

		System.out.println(indent + "repeat {");
		m_stmt.dump(indent + "\t");
		System.out.println(indent + "} while (" + str_cond + ")");

	}
}
