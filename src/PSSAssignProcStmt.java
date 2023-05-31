
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

		if (leftInst.isReadOnly())
			PSSMessage.Error("", m_ref.getText() + " is read-only");

		/* Special treatment of empty aggregate literal */
		if (m_expression instanceof PSSAggregateExpression
				&& ((PSSAggregateExpression) m_expression).isEmpty()) {
			if (leftInst instanceof PSSListInst) {
				rightVal = new PSSListVal(null);
			} else if (leftInst instanceof PSSSetInst) {
				rightVal = new PSSSetVal(null);
			} else if (leftInst instanceof PSSMapInst) {
				rightVal = new PSSMapVal(null);
			}
		}

        switch(m_op) {
            case "=":
                leftInst.assign(rightVal);
                break;

            case "+=":
                leftInst.assign(leftInst.toVal().Add(rightVal));
                break;

            case "-=":
                leftInst.assign(leftInst.toVal().Sub(rightVal));
                break;

            case "<<=":
                leftInst.assign(leftInst.toVal().LeftShift(rightVal));
                break;

            case ">>=":
                leftInst.assign(leftInst.toVal().RightShift(rightVal));
                break;

            case "|=":
                leftInst.assign(leftInst.toVal().BitwiseOr(rightVal));
                break;

            case "&=":
                leftInst.assign(leftInst.toVal().BitwiseAnd(rightVal));
                break;

            default:
                PSSMessage.Error("", m_op + " is not an assign_op.");
        }

	}

	public void dump(String indent) {
		System.out.println(indent + m_ref + m_op + m_expression.getText());
	}
}
