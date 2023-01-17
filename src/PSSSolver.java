import java.util.*;

public class PSSSolver {
	PSSInst m_inst;
	public HashMap<PSSInst, PSSConstraintList> m_inst_list;

	PSSSolver(PSSInst inst) {
		m_inst_list = new HashMap<PSSInst, PSSConstraintList>();
		m_inst = inst;
	}

	void add(PSSInst inst, PSSConstraint constraint) {
		if (m_inst_list.containsKey(inst)) {
			PSSConstraintList c_list = m_inst_list.get(inst);
			c_list.add(constraint);
		}
		else {
			PSSConstraintList c_list = new PSSConstraintList();
			c_list.add(constraint);
			m_inst_list.put(inst, c_list);
		}
	}
	void add(PSSInst inst, PSSConstraintList list) {
		for (int i=0; i<list.size(); i++) {
			PSSConstraint c = list.get(i);
			add(inst, c);
		}
	}
	void add(PSSInst inst) {
		String ClassName = inst.getClass().getSimpleName();
		if (ClassName.equals("PSSStructInst")) {
			PSSConstraintList c_list = inst.getTypeModel().getConstraintList();
			add(inst, c_list);
		}
		else if (ClassName.equals("PSSRefInst")) {
			PSSConstraintList c_list = inst.getTypeModel().getConstraintList();
			add(inst, c_list);
		}

	}

	void reduceDomain() {
		PSSDomainMap result = new PSSDomainMap();

		for (PSSInst inst: m_inst_list.keySet()) {
			PSSConstraintList c_list = m_inst_list.get(inst);

			for (int i=0; i<c_list.size(); i++) {
				PSSConstraint c = c_list.get(i);
				PSSDomainMap d_map = c.deduceDomain(inst);
				if (d_map != null) {
					result = result.join(d_map);
				}
			}

		}
		
		HashMap<PSSInst, PSSDomain> map = result.getMap();
		for (PSSInst var: map.keySet()) {
			PSSDomain domain = map.get(var);
			var.assignDomain(domain);
			PSSMessage.Info(var.m_id + " domain: " + domain.getText());
		}
	}

	public boolean validate() {
		for (PSSInst inst: m_inst_list.keySet()) {
			PSSConstraintList c_list = m_inst_list.get(inst);
			if (c_list.validate(inst) == false) {
				return false;
			}
		}
		return true;
	}

	void solve() {
		boolean validated = false;
		int retry = 0;

		// reduce domain with constraints
		reduceDomain();

		// randomize kernel
		while (validated == false) {
			PSSRandom.limitRetry(retry);
			m_inst.randomize();

			validated = validate();
			retry++;
		}
	}
}

