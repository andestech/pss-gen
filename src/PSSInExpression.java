import java.util.*;

public class PSSInExpression extends PSSExpression {

	PSSExpression	m_left;
	PSSExpression	m_right;

	public PSSInExpression(PSSExpression left, PSSExpression right) {
		m_left = left;
		m_right = right;
	}

	public PSSDomainMap deduceDomain(PSSInst var) {
		PSSDomainMap map = new PSSDomainMap();
		PSSInst leftInst = m_left.getInst(var);
		if (leftInst != null) {
			PSSListVal rightVal = (PSSListVal) m_right.eval(var);
			PSSDomain left_domain = leftInst.getInitDomain();
			left_domain = left_domain.reduceDomainIn(rightVal);
			map.add(leftInst, left_domain);
		}
		return map;
	}

	public PSSVal eval(PSSInst var) {
		PSSVal result;
		PSSVal leftVal = m_left.eval(var);
		PSSVal rightVal = m_right.eval(var);

		return rightVal.InRange(leftVal);
	}

	public String getText() {
		return m_left.getText() + " in " + m_right.getText();
	}
}
