import java.util.*;

public class PSSIfElseActivity extends PSSActivity {

	public PSSExpression m_cond;
	public PSSActivity m_true_stmt;
	public PSSActivity m_false_stmt;

	public PSSIfElseActivity(PSSExpression cond) {
		m_cond = cond;
		m_true_stmt = null;
		m_false_stmt = null;
	}

	public void addTrueStmt(PSSActivity stmt) {
		m_true_stmt = stmt;
		m_true_stmt.m_parent = this;
	}
	public void addFalseStmt(PSSActivity stmt) {
		m_false_stmt = stmt;
		m_false_stmt.m_parent = this;
	}

	public void evalActivity(PSSInst inst) {
		PSSVal cond = m_cond.eval(inst);

		if (cond.toBool()) {
			m_true_stmt.evalActivity(inst);
		}
		else {
			if (m_false_stmt != null) {
				m_false_stmt.evalActivity(inst);
			}
		}
	}

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
