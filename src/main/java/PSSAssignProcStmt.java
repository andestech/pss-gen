
public class PSSAssignProcStmt extends PSSProcStmt {

	PSSRefPathExpression m_ref;
	String m_op;
	PSSExpression m_expression;

	public PSSAssignProcStmt(PSSRefPathExpression ref, String op, PSSExpression expression) {
		m_ref = ref;
		m_op = op;
		m_expression = expression;
	}

	@Override
	public void eval(PSSInst inst, PSSExecKind exec_kind) {
		// Report warning when assign value to a random variable in pre_solve block
		if (exec_kind == PSSExecKind.pre_solve) {
			PSSInst leftInst = m_ref.getInst(inst);
			if (leftInst.isRandomable()) {
				PSSMessage.Warning("Assign to random variable in pre_solve block is meaningless: "
						+ m_ref.getText() + m_op + m_expression.getText());
			}
		}

		eval(inst);
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


		/** Get LHS value for self assignment */
		PSSVal leftVal = null;
		if (m_op.matches("[+-<>|&]+=")) {
			if (leftInst instanceof PSSIntInst) {
				PSSIntVal index_backup = ((PSSIntInst)leftInst).getBitSelect();
				leftVal = m_ref.eval(inst);
				leftInst = ((PSSIntInst)leftInst).indexOf(index_backup);
			} else {
				leftVal = m_ref.eval(inst);
			}
		}

		switch(m_op) {
			case "=":
				m_ref.assign(inst, rightVal);
				break;

			case "+=":
				m_ref.assign(inst, leftVal.Add(rightVal));
				break;

			case "-=":
				m_ref.assign(inst, leftVal.Sub(rightVal));
				break;

			case "<<=":
				m_ref.assign(inst, leftVal.LeftShift(rightVal));
				break;

			case ">>=":
				m_ref.assign(inst, leftVal.RightShift(rightVal));
				break;

			case "|=":
				m_ref.assign(inst, leftVal.BitwiseOr(rightVal));
				break;

			case "&=":
				m_ref.assign(inst, leftVal.BitwiseAnd(rightVal));
				break;

			default:
				PSSMessage.Error("", m_op + " is not an assign_op.");
		}

	}

	public void dump(String indent) {
		System.out.println(indent + m_ref + m_op + m_expression.getText());
	}
}
