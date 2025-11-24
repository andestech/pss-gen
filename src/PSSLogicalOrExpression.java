import java.util.*;

public class PSSLogicalOrExpression extends PSSExpression {

	PSSExpression	m_left;
	PSSExpression	m_right;

	public PSSLogicalOrExpression(PSSExpression left, PSSExpression right) {
		m_left = left;
		m_right = right;
	}

	@Override
	public boolean isRandomable (PSSInst var) {
		boolean left_isRandomable = m_left.isRandomable(var);
		boolean right_isRandomable = m_right.isRandomable(var);
		return (left_isRandomable || right_isRandomable);
	}

	public PSSVal eval(PSSInst var) {
		PSSVal result;
		PSSVal leftVal = m_left.eval(var);
		PSSVal rightVal = m_right.eval(var);

		return leftVal.LogicalOr(rightVal);
	}

	public PSSDomainMap deduceDomain(PSSInst var) {
		PSSMessage.Warning("Domain deduction is not not implementation for '" + getText() + "'");
		return null;
	}

	public String getText() {
		return m_left.getText() + "||" + m_right.getText();
	}
}
