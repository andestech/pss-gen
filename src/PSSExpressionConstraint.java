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
		// Report warning if a constraint is unrelated to any random variable
		if        (m_expression instanceof PSSInExpression           && m_expression.isRandomable(var) == false) {
			PSSMessage.Warning("Use 'in' expression to non-random variable in constraint block is meaningless: " + m_expression.getText());
		} else if (m_expression instanceof PSSEqualExpression        && m_expression.isRandomable(var) == false) {
			PSSMessage.Warning("Use '==' expression without random variable in constraint block is meaningless: " + m_expression.getText());
		} else if (m_expression instanceof PSSNotEqualExpression     && m_expression.isRandomable(var) == false) {
			PSSMessage.Warning("Use '!=' expression without random variable in constraint block is meaningless: " + m_expression.getText());
		} else if (m_expression instanceof PSSGreaterThanExpression  && m_expression.isRandomable(var) == false) {
			PSSMessage.Warning("Use '>' expression without random variable in constraint block is meaningless: " + m_expression.getText());
		} else if (m_expression instanceof PSSGreaterEqualExpression && m_expression.isRandomable(var) == false) {
			PSSMessage.Warning("Use '>=' expression without random variable in constraint block is meaningless: " + m_expression.getText());
		} else if (m_expression instanceof PSSLessThanExpression     && m_expression.isRandomable(var) == false) {
			PSSMessage.Warning("Use '<' expression without random variable in constraint block is meaningless: " + m_expression.getText());
		} else if (m_expression instanceof PSSLessEqualExpression    && m_expression.isRandomable(var) == false) {
			PSSMessage.Warning("Use '<=' expression without random variable in constraint block is meaningless: " + m_expression.getText());
		}

		return m_expression.eval(var).toBool();
	}

	@Override
	public String getText() {
		return m_expression.getText();
	}

	@Override
	public void dump(String indent) {
		System.out.println(indent + m_expression.getText());
	}

}
