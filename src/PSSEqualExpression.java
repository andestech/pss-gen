import java.util.ArrayList;

public class PSSEqualExpression extends PSSExpression {

	PSSExpression	m_left;
	PSSExpression	m_right;

	public PSSEqualExpression(PSSExpression left, PSSExpression right) {
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
		PSSVal leftVal = m_left.eval(var);
		PSSVal rightVal = m_right.eval(var);

		return leftVal.Equal(rightVal);
	}

	public PSSDomainMap deduceDomain(PSSInst var) {
		PSSDomainMap map = new PSSDomainMap();

		PSSInst leftInst = m_left.getInst(var);
		if (leftInst != null) {
			PSSVal rightVal = m_right.eval(var);
			PSSDomain left_domain = leftInst.getInitDomain();
			left_domain = left_domain.reduceDomainEqual(rightVal);
			map.add(leftInst, left_domain);
		}
		return map;
	}

	@Override
	public ArrayList<PSSInst> getInsts(PSSInst var) {
		var ret = new ArrayList<PSSInst>();
		ret.addAll(m_left.getInsts(var));
		ret.addAll(m_right.getInsts(var));
		return ret;
	}

	public String getText() {
		return m_left.getText() + "==" + m_right.getText();
	}
}
