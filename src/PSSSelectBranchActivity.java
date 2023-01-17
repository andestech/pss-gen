import java.util.*;

public class PSSSelectBranchActivity extends PSSActivity {
	PSSActivity m_stmt;
	PSSExpression m_cond;
	PSSExpression m_weight;

	public PSSSelectBranchActivity(PSSActivity stmt, PSSExpression cond, PSSExpression weight) {
		m_cond = cond;
		m_weight = weight;
		m_stmt = stmt;
		m_stmt.m_parent = this;
	}

	public void evalActivity(PSSInst inst) {
		m_stmt.evalActivity(inst);
	}

	public int getWeight(PSSInst inst) {
		int base = 1;
		boolean cond = true;

		if (m_cond != null) {
			cond = m_cond.eval(inst).toBool();
		}

		if (cond) {
			if (m_weight != null) {
				PSSVal w = m_weight.eval(inst);
				return w.toInt();
			}
			else {
				return 1;
			}
		}
		else {
			return 0;
		}
	}

	public void dump(String indent) {
		m_stmt.dump(indent + "\t");
	}
}
