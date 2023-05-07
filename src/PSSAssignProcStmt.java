import java.util.*;

public class PSSAssignProcStmt extends PSSProcStmt {

	PSSRefPathExpression m_ref;
	String m_op;
	PSSExpression m_expression;

	public PSSAssignProcStmt(PSSRefPathExpression ref, String op, PSSExpression expression) {
		m_ref = ref;
		m_op = op;
		m_expression = expression;
	}

	public void eval(PSSInst inst) {
		PSSInst leftInst = m_ref.getInst(inst);
		PSSVal rightVal = m_expression.eval(inst);

		/* Special treatment of empty aggregate literal */
		if (m_expression instanceof PSSAggregateExpression
				&& ((PSSAggregateExpression) m_expression).isEmpty()) {
			if (leftInst instanceof PSSListInst) {
				rightVal = new PSSListVal();
			} else if (leftInst instanceof PSSSetInst) {
				rightVal = new PSSSetVal();
			} else if (leftInst instanceof PSSMapInst) {
				rightVal = new PSSMapVal();
			}
		}

		leftInst.assign(rightVal);

	}

	public void dump(String indent) {
		System.out.println(indent + m_ref + m_op + m_expression.getText());
	}
}
