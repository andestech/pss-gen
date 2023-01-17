import java.util.*;

public class PSSWhileProcStmt extends PSSProcStmt {

	public PSSExpression m_cond;
	public PSSProcStmt m_stmt;

	public PSSWhileProcStmt(PSSExpression cond, PSSProcStmt stmt) {
		m_cond = cond;
		m_stmt = stmt;
	}

	public void eval(PSSInst inst) {
		while (m_cond.eval(inst).toBool()) {
			m_stmt.eval(inst);
		}
	}

	public void dump(String indent) {
		String str_cond = m_cond.getText();

		System.out.println(indent + "while (" + str_cond + ") {");
		m_stmt.dump(indent + "\t");
		System.out.println(indent + "}");

	}
}
