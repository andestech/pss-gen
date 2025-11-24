import java.util.*;

public class PSSAddExpression extends PSSExpression {

	PSSExpression	m_left;
	PSSExpression	m_right;

	public PSSAddExpression(PSSExpression left, PSSExpression right) {
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

		return rightVal.Add(leftVal);
	}

	public String getText() {
		return m_left.getText() + "+" + m_right.getText();
	}
}
