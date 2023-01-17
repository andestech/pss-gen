import java.util.*;

public class PSSUnaryMinusExpression extends PSSExpression {

	PSSExpression	m_exp;

	public PSSUnaryMinusExpression(PSSExpression exp) {
		m_exp = exp;
	}

	public PSSVal eval(PSSInst var) {
		PSSVal val = m_exp.eval(var);

		return val.UnaryMinus();
	}

	public String getText() {
		return "-" + m_exp.getText();
	}
}
