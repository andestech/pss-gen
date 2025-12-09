import java.util.*;

public class PSSBitwiseNotExpression extends PSSExpression {

	PSSExpression	m_exp;

	public PSSBitwiseNotExpression(PSSExpression exp) {
		m_exp = exp;
	}

	@Override
	public boolean isRandomable (PSSInst var) {
		return m_exp.isRandomable(var);
	}

	public PSSVal eval(PSSInst var) {
		PSSVal val = m_exp.eval(var);

		return val.BitwiseNot();
	}

	@Override
	public ArrayList<PSSInst> getInsts(PSSInst var) {
		var ret = new ArrayList<PSSInst>();
		ret.addAll(m_exp.getInsts(var));
		return ret;
	}

	public String getText() {
		return "~" + m_exp.getText();
	}
}
