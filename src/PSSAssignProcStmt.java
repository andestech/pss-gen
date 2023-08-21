
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
		PSSVal rightVal = m_expression.eval(inst);
		PSSInst leftInst = m_ref.getInst(inst);

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

		/** Backup Bit-Select value for prevert lost after leftInst.toVal() */
		PSSIntVal backupBitSelect = null;
		if (leftInst instanceof PSSIntInst && ((PSSIntInst)leftInst).isAccessBitSelect()) {
			backupBitSelect = ((PSSIntInst)leftInst).getBitSelect();
		}

		/** Get LHS value for later use */
		PSSVal leftVal = null;
		if (m_op.matches("[+-<>|&]+=")) {
			leftVal = leftInst.toVal();

			/** Restore back Bit-Selects value after clear by leftInst.toVal() */
			if (null != backupBitSelect) {
				leftInst = leftInst.indexOf(backupBitSelect);
			}
		}

		switch(m_op) {
			case "=":
				leftInst.assign(rightVal);
				break;

			case "+=":
				leftInst.assign(leftVal.Add(rightVal));
				break;

			case "-=":
				leftInst.assign(leftVal.Sub(rightVal));
				break;

			case "<<=":
				leftInst.assign(leftVal.LeftShift(rightVal));
				break;

			case ">>=":
				leftInst.assign(leftVal.RightShift(rightVal));
				break;

			case "|=":
				leftInst.assign(leftVal.BitwiseOr(rightVal));
				break;

			case "&=":
				leftInst.assign(leftVal.BitwiseAnd(rightVal));
				break;

			default:
				PSSMessage.Error("", m_op + " is not an assign_op.");
		}

	}

	public void dump(String indent) {
		System.out.println(indent + m_ref + m_op + m_expression.getText());
	}
}
