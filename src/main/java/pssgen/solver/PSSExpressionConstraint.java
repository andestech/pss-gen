package pssgen.solver;

import java.util.*;

import pssgen.expr.PSSExpression;
import pssgen.expr.PSSRelation;
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
		PSSRelation relation = m_expression.relation();
		if (relation != null && m_expression.isRandomable(var) == false) {
			String use = relation.op == PSSRelation.Op.IN
				? "Use 'in' expression to non-random variable"
				: "Use '" + relation.op.token + "' expression without random variable";
			log.Warning(use + " in constraint block is meaningless: " + m_expression.getText());
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
