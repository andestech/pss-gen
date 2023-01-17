import java.util.*;

public class PSSLogicalNotExpression extends PSSExpression {

	PSSExpression	m_exp;

	public PSSLogicalNotExpression(PSSExpression exp) {
		m_exp = exp;
	}

	public PSSVal eval(PSSInst var) {
		PSSVal val = m_exp.eval(var);

		return val.LogicalNot();
	}

	public String getText() {
		return "!" + m_exp.getText();
	}
}
