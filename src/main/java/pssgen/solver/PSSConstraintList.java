package pssgen.solver;

import java.util.*;

import pssgen.expr.PSSExpression;
import pssgen.inst.PSSInst;

public class PSSConstraintList {
	ArrayList<PSSConstraint> m_list;

	public PSSConstraintList() {
		m_list = new ArrayList<PSSConstraint>();
	}

	public void add(PSSConstraint constraint) {
		m_list.add( constraint);
	}

	public int size() {
		return m_list.size();
	}

	public PSSConstraint get(int i) {
		return m_list.get(i);
	}

	public boolean containsInst (PSSInst parent_inst, PSSInst search_inst) {
		for (PSSConstraint item : m_list) {
			if (item instanceof PSSExpressionConstraint) {
				// Search each expression
				PSSExpression exp = ((PSSExpressionConstraint)item).getExpression();
				ArrayList<PSSInst> exp_inst_list = exp.getInsts(parent_inst);
				if (exp_inst_list.contains(search_inst)) return true;
			} else if (item instanceof PSSIfConstraint) {
				// TODO: if (true) search true expression
				// TODO: if (false) search false expression if exist
			} else if (item instanceof PSSForeachConstraint) {
				// TODO: search each element
			}
		}
		return false;
	}

	public boolean validate(PSSInst inst) {
		for (int i=0; i<m_list.size(); i++) {
			PSSConstraint c = m_list.get(i);
			if (!c.validate(inst)) {
				return false;
			}
		}
		return true;
	}

	public void dump (String indent) {
		for (int i=0; i<m_list.size(); i++) {
			PSSConstraint c = m_list.get(i);
			c.dump(indent);
		}
	}
}

