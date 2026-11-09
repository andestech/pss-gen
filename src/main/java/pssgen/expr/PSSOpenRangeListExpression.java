package pssgen.expr;

import java.util.*;

import pssgen.inst.PSSInst;
import pssgen.value.PSSListVal;
import pssgen.value.PSSVal;

public class PSSOpenRangeListExpression extends PSSExpression {

	private ArrayList<PSSOpenRangeValueExpression> m_list;


	public PSSOpenRangeListExpression() {
		m_list = new ArrayList<PSSOpenRangeValueExpression>();
	}

	public void addOpenRangeValueExpression(PSSOpenRangeValueExpression item) {
		m_list.add(item);
	}

	@Override
	public PSSVal eval(PSSInst var) {
		PSSListVal result = new PSSListVal(null);

		for (int i=0; i<m_list.size(); i++) {
			PSSOpenRangeValueExpression item = m_list.get(i);

			PSSVal item_val = item.eval(var);
			result.add(item_val);
		}

		return result;
	}

	/**
	 * Evaluates the list where its expected type is the type of expected (PSS 2.0 Section
	 * 21.3 a)): the left side of 'in', or the expression of a match.
	 *
	 * @param var      the instance the list is evaluated in
	 * @param expected the field whose type is expected, or null
	 * @return the values and ranges of the list
	 */
	public PSSVal eval(PSSInst var, PSSInst expected) {
		PSSListVal result = new PSSListVal(null);
		for (PSSOpenRangeValueExpression item : m_list)
			result.add(item.eval(var, expected));
		return result;
	}

	@Override
	public String getText() {
		String text = "";

		for (int i=0; i<m_list.size(); i++) {
			PSSOpenRangeValueExpression item = m_list.get(i);

			if (text.length() == 0) {
				text = text.concat(item.getText());
			}
			else {
				text = text.concat(", " + item.getText());
			}
		}
		return "[ " + text + " ]";
	}

	public final ArrayList<PSSOpenRangeValueExpression> getList() {
		return m_list;
	}
}
