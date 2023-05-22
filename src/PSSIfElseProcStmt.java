import java.util.*;

public class PSSIfElseProcStmt extends PSSProcStmt {

	public PSSExpression m_cond;
	public PSSProcStmt m_true_stmt;
	public PSSProcStmt m_false_stmt;

	public PSSIfElseProcStmt(PSSExpression cond, PSSProcStmt true_stmt, PSSProcStmt false_stmt) {
		m_cond = cond;
		m_true_stmt = true_stmt;
		m_false_stmt = false_stmt;
	}

	@Override
	public void eval(PSSInst inst) {
		PSSVal cond = m_cond.eval(inst);

		if (cond.toBool()) {
			m_true_stmt.eval(inst);
		} else {
			if (m_false_stmt != null) {
				m_false_stmt.eval(inst);
			}
		}
	}

	@Override
	public String getText() {
		return "if (" + m_cond.getText() + ")" + "{ " + m_true_stmt.getText() + " }"
				+ (m_false_stmt == null ? "" : " else {" + m_false_stmt.getText() + " }");
	}

	@Override
	public void dump(String indent) {
		String str_cond = m_cond.getText();

		System.out.println(indent + "if (" + str_cond + ") {");
		m_true_stmt.dump(indent + "\t");
		System.out.println(indent + "}");

		if (m_false_stmt != null) {
			System.out.println(indent + "else {");
			m_false_stmt.dump(indent + "\t");
			System.out.println(indent + "}");
		}
	}

}
