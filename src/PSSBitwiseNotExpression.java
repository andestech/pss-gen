import java.util.*;

public class PSSBitwiseNotExpression extends PSSExpression {

	PSSExpression	m_exp;

	public PSSBitwiseNotExpression(PSSExpression exp) {
		m_exp = exp;
	}

	public PSSVal eval(PSSInst var) {
		PSSVal val = m_exp.eval(var);

		return val.BitwiseNot();
	}

	public String getText() {
		return "~" + m_exp.getText();
	}
}
