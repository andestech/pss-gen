package pssgen.expr;

import java.util.*;
import java.util.ArrayList;

import pssgen.inst.PSSInst;
import pssgen.value.PSSRangeVal;
import pssgen.value.PSSVal;

public class PSSOpenRangeValueExpression extends PSSExpression {

	PSSExpression m_begin;
	PSSExpression m_end;

	public PSSOpenRangeValueExpression(PSSExpression begin, PSSExpression end) {
		m_begin = begin;
		m_end = end;
	}
	public PSSOpenRangeValueExpression(PSSExpression begin) {
		m_begin = begin;
		m_end = null;
	}

	public PSSVal eval(PSSInst var) {
		return eval(var, null);
	}

	/**
	 * Evaluates the value or range where its expected type is the type of expected (PSS
	 * 2.0 Section 21.3 a)).
	 *
	 * @param var      the instance the item is evaluated in
	 * @param expected the field whose type is expected, or null
	 * @return the value or range
	 */
	public PSSVal eval(PSSInst var, PSSInst expected) {
		PSSVal begin_val = expecting(m_begin, expected).eval(var);
		if (m_end != null) {
			PSSVal end_val = expecting(m_end, expected).eval(var);
			return new PSSRangeVal(begin_val.toBigInteger(), end_val.toBigInteger());
		}
		else {
			return begin_val;
		}
	}

	@Override
	public ArrayList<PSSInst> getInsts(PSSInst var) {
		var ret = new ArrayList<PSSInst>();
		ret.addAll(m_begin.getInsts(var));
		ret.addAll(m_end.getInsts(var));
		return ret;
	}

	public String getText() {
		if (m_end != null) {
			return m_begin.getText() + ".." + m_end.getText();
		}
		else {
			return m_begin.getText();
		}
	}
}
