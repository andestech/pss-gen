

public class PSSPrimaryExpression extends PSSExpression {

	PSSVal		m_val;

	public PSSPrimaryExpression(PSSVal val) {
		m_val = val;
	}

	public PSSVal eval(PSSInst var) {
		return m_val;
	}

	public String getText() {
		return m_val.getText();
	}
}
