import java.util.*;

public class PSSLogicalAndExpression extends PSSExpression {

	PSSExpression	m_left;
	PSSExpression	m_right;

	public PSSLogicalAndExpression(PSSExpression left, PSSExpression right) {
		m_left = left;
		m_right = right;
	}

	public PSSVal eval(PSSInst var) {
		PSSVal result;
		PSSVal leftVal = m_left.eval(var);
		PSSVal rightVal = m_right.eval(var);

		return leftVal.LogicalAnd(rightVal);
	}

	public PSSDomainMap deduceDomain(PSSInst var) {
		m_left.deduceDomain(var);
		m_right.deduceDomain(var);
		return null;
	}


	public String getText() {
		return m_left.getText() + "&&" + m_right.getText();
	}
}
