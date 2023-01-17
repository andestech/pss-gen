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

	public String getText() {
		return m_left.getText() + m_op + m_right.getText();
	}
}
