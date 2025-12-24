import java.util.*;
import java.util.HashMap;

public class PSSSolver {
	PSSInst m_inst;
	public HashMap<PSSInst, PSSConstraintList> m_inst_list;
	public HashMap<PSSInst, PSSConstraintList> m_default_constraint_list;

	PSSSolver(PSSInst inst) {
		m_inst_list = new HashMap<PSSInst, PSSConstraintList>();
		m_default_constraint_list = new HashMap<PSSInst, PSSConstraintList>();
		m_inst = inst;
	}

	void addDefaultConstraint (PSSInst inst, PSSConstraint constraint) {
		if (!(constraint instanceof PSSExpressionConstraint)) {
			PSSMessage.Fatal("default constraint should be PSSExpressionConstraint class: < " + constraint.getClass().getSimpleName() + " >");
		}

		if (m_default_constraint_list.containsKey(inst)) {
			PSSConstraintList c_list = m_default_constraint_list.get(inst);
			c_list.add(constraint);
		} else {
			PSSConstraintList c_list = new PSSConstraintList();
			c_list.add(constraint);
			m_default_constraint_list.put(inst, c_list);
		}
	}

	void addDefaultConstraint (PSSInst inst, PSSConstraintList list) {
		for (int i = 0; i < list.size(); i++) {
			PSSConstraint c = list.get(i);
			addDefaultConstraint(inst, c);
		}
	}

	void add(PSSInst inst, PSSConstraint constraint) {
		if (m_inst_list.containsKey(inst)) {
			PSSConstraintList c_list = m_inst_list.get(inst);
			c_list.add(constraint);
		} else {
			PSSConstraintList c_list = new PSSConstraintList();
			c_list.add(constraint);
			m_inst_list.put(inst, c_list);
		}
	}

	void add(PSSInst inst, PSSConstraintList list) {
		for (int i = 0; i < list.size(); i++) {
			PSSConstraint c = list.get(i);
			add(inst, c);
		}
	}

	void add(PSSInst inst) {
		if (inst instanceof PSSStructInst) {
			PSSConstraintList c_list = inst.getTypeModel().getConstraintList();
			add(inst, c_list);
		} else if (inst instanceof PSSFlowRefInst) {
			PSSConstraintList c_list = inst.getTypeModel().getConstraintList();
			add(inst, c_list);
		}
	}

	void reduceDefaultDomain () {
		for (PSSInst item : m_default_constraint_list.keySet()) {
			PSSConstraintList def_c_list = m_default_constraint_list.get(item);
			PSSConstraintList c_list     = m_inst_list.get(item);

			// Reduce each default constraint
			for (var i = 0; i < def_c_list.size(); i++) {
				// Get random variable from syntax: (default hierarchical_id '==' constant_expression ';')
				PSSExpressionConstraint def_c = (PSSExpressionConstraint)def_c_list.get(i);
				PSSEqualExpression def_exp    = (PSSEqualExpression)def_c.getExpression();
				PSSInst def_inst              = def_exp.getInsts(item).get(0);
				
				// Assign domain of the random variable as default constraint
				PSSDomainMap def_map = def_c.deduceDomain(item);
				PSSDomain def_domain = def_map.getMap().get(def_inst);
				def_inst.assignDomain(def_domain);
			}
		}
	}

	void reduceDomain() {
		PSSDomainMap result = new PSSDomainMap();

		// Deduce domain by constraint
		for (PSSInst inst : m_inst_list.keySet()) {
			PSSConstraintList c_list = m_inst_list.get(inst);

			for (int i = 0; i < c_list.size(); i++) {
				PSSConstraint c = c_list.get(i);
				PSSDomainMap d_map = c.deduceDomain(inst);
				if (d_map != null) {
					result = result.join(d_map);
				}
			}

		}

		HashMap<PSSInst, PSSDomain> map = result.getMap();
		for (PSSInst var : map.keySet()) {
			PSSDomain domain = map.get(var);
			var.assignDomain(domain);
			PSSMessage.Info(var.m_id + " domain: " + domain.getText());
		}
	}

	public boolean validate () {
		// Validate default constraint
		for (PSSInst item : m_default_constraint_list.keySet()) {
			PSSConstraintList def_c_list = m_default_constraint_list.get(item);
			PSSConstraintList c_list     = m_inst_list.get(item);

			// Validate each default constraint
			for (var i = 0; i < def_c_list.size(); i++) {
				// Get random variable from syntax: (default hierarchical_id '==' constant_expression ';')
				PSSExpressionConstraint def_c = (PSSExpressionConstraint)def_c_list.get(i);
				PSSEqualExpression def_exp    = (PSSEqualExpression)def_c.getExpression();
				PSSInst def_inst              = def_exp.getInsts(item).get(0);
				
				// Skip when hard constraint overwrites default constraint
				if (c_list != null && c_list.containsInst(item, def_inst)) continue;

				if (def_c.validate(item) == false) return false;
			}
		}

		// Validate constraint
		for (PSSInst item : m_inst_list.keySet()) {
			PSSConstraintList c_list = m_inst_list.get(item);
			if (c_list.validate(item) == false) return false;
		}
		return true;
	}

	int solve() {
		int retry = 0;

		reduceDefaultDomain();

		do {
			m_inst.randomize();
			reduceDomain();	// reduce domain with constraints
			PSSRandom.limitRetry(++retry);
		} while (validate() == false);

		return retry;
	}

}
