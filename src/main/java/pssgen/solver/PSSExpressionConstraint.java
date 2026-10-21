package pssgen.solver;

import java.util.*;

import pssgen.expr.PSSEqualExpression;
import pssgen.expr.PSSExpression;
import pssgen.expr.PSSGreaterEqualExpression;
import pssgen.expr.PSSGreaterThanExpression;
import pssgen.expr.PSSInExpression;
import pssgen.expr.PSSLessEqualExpression;
import pssgen.expr.PSSLessThanExpression;
import pssgen.expr.PSSNotEqualExpression;
import pssgen.inst.PSSInst;
import pssgen.util.PSSLog;

public class PSSExpressionConstraint extends PSSConstraint {
	PSSExpression m_expression;

	public PSSExpressionConstraint(PSSExpression expression) {
		m_expression = expression;
	}

	public PSSExpression getExpression() {
		return m_expression;
	}

	public PSSDomainMap deduceDomain(PSSInst var) {
		return m_expression.deduceDomain(var);
	}
	public boolean validate(PSSInst var) {
		PSSLog log = var.run().log();
		// Report warning if a constraint is unrelated to any random variable
		if        (m_expression instanceof PSSInExpression           && m_expression.isRandomable(var) == false) {
			log.Warning("Use 'in' expression to non-random variable in constraint block is meaningless: " + m_expression.getText());
		} else if (m_expression instanceof PSSEqualExpression        && m_expression.isRandomable(var) == false) {
			log.Warning("Use '==' expression without random variable in constraint block is meaningless: " + m_expression.getText());
		} else if (m_expression instanceof PSSNotEqualExpression     && m_expression.isRandomable(var) == false) {
			log.Warning("Use '!=' expression without random variable in constraint block is meaningless: " + m_expression.getText());
		} else if (m_expression instanceof PSSGreaterThanExpression  && m_expression.isRandomable(var) == false) {
			log.Warning("Use '>' expression without random variable in constraint block is meaningless: " + m_expression.getText());
		} else if (m_expression instanceof PSSGreaterEqualExpression && m_expression.isRandomable(var) == false) {
			log.Warning("Use '>=' expression without random variable in constraint block is meaningless: " + m_expression.getText());
		} else if (m_expression instanceof PSSLessThanExpression     && m_expression.isRandomable(var) == false) {
			log.Warning("Use '<' expression without random variable in constraint block is meaningless: " + m_expression.getText());
		} else if (m_expression instanceof PSSLessEqualExpression    && m_expression.isRandomable(var) == false) {
			log.Warning("Use '<=' expression without random variable in constraint block is meaningless: " + m_expression.getText());
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
