import java.util.*;

public class PSSTraversalList {
	ArrayList <PSSTraversal> m_list;

	PSSTraversalList() {
		m_list = new ArrayList <PSSTraversal>();
	}

	void add(PSSTraversal t) {
		m_list.add(t);
	}

	void addSequence(PSSActionInst inst) {
		PSSTraversal t = new PSSTraversal(inst);
		if (m_list.size() != 0) {
			PSSTraversal last = m_list.get(m_list.size() - 1);
			t.addDependency(last);
		}
		m_list.add(t);
	}

	int size() {
		return m_list.size();
	}

	private int nextIndex() {
		ArrayList<Integer> candidate = new ArrayList<Integer>();

		for (int i=0; i<m_list.size(); i++) {
			PSSTraversal t = m_list.get(i);
			boolean resolved = true;
			for (int j=0; j<t.getDependencySize(); j++) {
				PSSTraversal dep_t = t.getDependencyTraversal(j);
				if (m_list.contains(dep_t)) {
					resolved = false;
					break;
				}
			}
			if (resolved) {
				candidate.add(i);
			}
		}
		if (candidate.size() == 0) {
			PSSMessage.Error("DEP", "Cannot resolve action dependency");
			return 0;
		}
		else if (candidate.size() == 1) {
			return candidate.get(0);
		}
		else {
			int idx = PSSRandom.nextInt(0, candidate.size()-1);
			return candidate.get(idx);
		}

	}

	void schedule() {
		ArrayList<PSSTraversal> sched_list = new ArrayList<PSSTraversal>();

		int retry = 0;

		while (m_list.size() != 0) {
			int idx = nextIndex();
			PSSTraversal t = m_list.get(idx);;
			m_list.remove(idx);
			sched_list.add(t);

		}
		m_list = sched_list;

	}

	void traverse() {
		schedule();
		for (int i=0; i<m_list.size(); i++) {
			PSSTraversal t = m_list.get(i);
			PSSActionInst sub = t.getInst();
			PSSActionModel sub_action_decl = (PSSActionModel) sub.m_type_decl;
			sub_action_decl.traverse(sub);
		}
	}

	void inferSubAction(PSSActionInst parent_inst) {
		for (int i=0; i<m_list.size(); i++) {
			PSSTraversal t = m_list.get(i);
			PSSActionInst sub_ah = t.getInst();
			PSSUnboundRefList list = sub_ah.getUnboundRefList();

			for (int j=0; j<list.size(); j++) {
				PSSUnboundRef uref = list.get(j);
				inferAction(parent_inst, t, uref);
			}
		}
	}

	private void declInferredAction(
		PSSActionInst parent_inst,
		PSSTraversal t,
		PSSUnboundRef uref,
		PSSInferredAction item
	) {
		PSSActionModel action_model = item.getAction();
		PSSMessage.Info("Infer action: " + action_model.getId());

		PSSModel type_model = uref.getTypeModel();

		PSSInst flowobj = type_model.declInst("", true);

		parent_inst.addInst(flowobj);

		uref.bind(flowobj);

		PSSActionInst inferred_inst = action_model.declInst(true);
		PSSTraversal inferred_t = new PSSTraversal(inferred_inst);
		m_list.add(inferred_t);

		PSSFlowRefInst uref2 = (PSSFlowRefInst) inferred_inst.findInstance(item.getRefId());
		uref2.bind(flowobj);

		if (uref.isOutput()) {
			inferred_t.addDependency(t);
		}
		else {
			t.addDependency(inferred_t);
		}
	}

	public void inferAction(PSSActionInst parent_inst, PSSTraversal t, PSSUnboundRef uref) {
		PSSInferredActionList list = parent_inst.getInferredAction(!uref.isOutput(), uref.getTypeId());
		if (list.size() > 0) {
			int idx = PSSRandom.nextInt(0, list.size()-1);
			PSSInferredAction item = list.get(idx);
			declInferredAction(parent_inst, t, uref, item);
		}
		else {
			PSSMessage.Error("INFER", "cannot infer action for " + uref.getTypeId());
		}
	}


	public void execute_kind(String kind) {
		for (int i=0; i<m_list.size(); i++) {
			PSSTraversal t = m_list.get(i);
			PSSActionInst sub_ah = t.getInst();
			sub_ah.execute_kind(kind);
		}
	}

}

