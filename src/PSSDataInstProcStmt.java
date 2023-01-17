import java.util.*;

public class PSSDataInstProcStmt extends PSSProcStmt {

	public String m_id;
	public PSSExpression m_expression;

	public PSSDataInstProcStmt(String id, PSSExpression expression) {
		m_id = id;
		m_expression = expression;
	}

	public String getText() {
		if (m_expression != null) {
			return m_id + "=" + m_expression.getText();
		}
		else {
			return m_id;
		}
	}
}
