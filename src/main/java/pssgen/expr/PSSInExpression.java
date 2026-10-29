package pssgen.expr;

import java.util.List;
import java.util.ArrayList;

import pssgen.inst.PSSInst;
import pssgen.solver.PSSDomain;
import pssgen.solver.PSSDomainMap;
import pssgen.util.PSSMessage;
import pssgen.value.PSSVal;

public class PSSInExpression extends PSSExpression {

	PSSExpression	m_left;
	PSSExpression	m_right;

	/** the left operand when it is written as a bare identifier, else null */
	private final String m_bare_left;

	public PSSInExpression(PSSExpression left, PSSExpression right) {
		this(left, right, null);
	}

	/**
	 * @param bare_left the left operand's identifier when it is written without a
	 *                  qualifier, else null
	 */
	public PSSInExpression(PSSExpression left, PSSExpression right, String bare_left) {
		m_left = left;
		m_right = right;
		m_bare_left = bare_left;
	}

	/**
	 * PSS 2.0 Section 9.5.9: the left-hand side has a self-determined type, so an
	 * unqualified enum item there has no expected type to resolve against.
	 */
	private void checkLeft(PSSInst var) {
		if (m_bare_left == null || var == null)
			return;
		PSSInst item = var.findStaticInst(m_bare_left);
		if (item != null && item == m_left.getInst(var))
			PSSMessage.Error("PSS 2.0 Section 9.5.9",
				"The left-hand side of 'in' shall not be an unqualified enum item: '" + m_bare_left + "'");
	}

	@Override
	public boolean isRandomable (PSSInst var) {
		return m_left.isRandomable(var);
	}

	@Override
	public PSSDomainMap deduceDomain(PSSInst var) {
		checkLeft(var);
		PSSDomainMap map = new PSSDomainMap();
		PSSInst leftInst = m_left.getInst(var);
		if (leftInst != null) {
			List<PSSVal> rightList = m_right.eval(var).getValList();
			PSSDomain left_domain = leftInst.getInitDomain();
			left_domain = left_domain.reduceDomainIn(rightList);
			map.add(leftInst, left_domain);
		}
		return map;
	}

	@Override
	public PSSVal eval(PSSInst var) {
		checkLeft(var);
		PSSVal leftVal = m_left.eval(var);
		PSSVal rightVal = m_right.eval(var);
		return rightVal.InRange(leftVal);
	}

	@Override
	public ArrayList<PSSInst> getInsts(PSSInst var) {
		var ret = new ArrayList<PSSInst>();
		ret.addAll(m_left.getInsts(var));
		ret.addAll(m_right.getInsts(var));
		return ret;
	}

	@Override
	public String getText() {
		return m_left.getText() + " in " + m_right.getText();
	}

	@Override
	public PSSRelation relation() {
		return new PSSRelation(PSSRelation.Op.IN, m_left, m_right);
	}
}
