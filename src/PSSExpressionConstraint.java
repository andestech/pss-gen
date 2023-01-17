import java.util.*;

public class PSSExpressionConstraint extends PSSConstraint {
	PSSExpression m_expression;

	PSSExpressionConstraint(PSSExpression expression) {
		m_expression = expression;
	}

	public PSSDomainMap deduceDomain(PSSInst var) {
		return m_expression.deduceDomain(var);
	}
	public boolean validate(PSSInst var) {
		return m_expression.eval(var).toBool();
	}

	public void dump(String indent) {
		System.out.println(indent + m_expression.getText());
	}
}
