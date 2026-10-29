package pssgen.solver;

import java.util.*;

import pssgen.expr.PSSExpression;
import pssgen.expr.PSSRelation;
import pssgen.inst.PSSInst;
import pssgen.util.PSSLog;

public class PSSExpressionConstraint extends PSSConstraint {
	PSSExpression m_expression;

	/** the conditions of the if constraints a default constraint is written under; null when none */
	private List<PSSExpression> m_guards;
	/** per condition, the value it must have: false in an else branch */
	private List<Boolean> m_guard_values;

	public PSSExpressionConstraint(PSSExpression expression) {
		m_expression = expression;
	}

	/**
	 * Adds a condition under which this default constraint applies.
	 *
	 * @param guard the condition of an enclosing if constraint
	 * @param value true in the if branch, false in the else branch
	 */
	public void addGuard(PSSExpression guard, boolean value) {
		if (m_guards == null) {
			m_guards = new ArrayList<PSSExpression>();
			m_guard_values = new ArrayList<Boolean>();
		}
		m_guards.add(guard);
		m_guard_values.add(value);
	}

	/**
	 * PSS 2.0 Section 17.1.10 f): a default constraint may be conditioned on a constant
	 * expression; it applies only where all its conditions hold.
	 *
	 * @param var the instance the constraint belongs to
	 * @return {@code true} if every condition holds
	 */
	public boolean isActive(PSSInst var) {
		if (m_guards == null)
			return true;
		for (int i = 0; i < m_guards.size(); i++) {
			if (m_guards.get(i).eval(var).toBool() != m_guard_values.get(i))
				return false;
		}
		return true;
	}

	public PSSExpression getExpression() {
		return m_expression;
	}

	@Override
	boolean refersTo(PSSInst parent_inst, PSSInst search_inst) {
		return m_expression.getInsts(parent_inst).contains(search_inst);
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
				: "Use '" + relation.op.getToken() + "' expression without random variable";
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
