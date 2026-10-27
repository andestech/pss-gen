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
