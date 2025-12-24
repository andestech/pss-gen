import java.util.*;

public class PSSBinaryExpression extends PSSExpression {

	PSSExpression	m_left;
	PSSExpression	m_right;
	String		m_op;

	public PSSBinaryExpression(PSSExpression left, String op, PSSExpression right) {
		m_left = left;
		m_right = right;
		m_op = op;
	}

	@Override
	public boolean isRandomable (PSSInst var) {
		boolean left_isRandomable = m_left.isRandomable(var);
		boolean right_isRandomable = m_right.isRandomable(var);
		return (left_isRandomable || right_isRandomable);
	}

	@Override
	public ArrayList<PSSInst> getInsts(PSSInst var) {
		var ret = new ArrayList<PSSInst>();
		ret.addAll(m_left.getInsts(var));
		ret.addAll(m_right.getInsts(var));
		return ret;
	}

	public String getText() {
		return m_left.getText() + m_op + m_right.getText();
	}
}
