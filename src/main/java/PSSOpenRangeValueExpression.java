import java.util.*;
import java.util.ArrayList;

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
		PSSVal begin_val = m_begin.eval(var);
		if (m_end != null) {
			PSSVal end_val = m_end.eval(var);
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
