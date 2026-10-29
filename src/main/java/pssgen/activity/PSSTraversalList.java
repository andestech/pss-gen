package pssgen.activity;

import java.util.*;

import pssgen.inst.PSSActionInst;
import pssgen.inst.PSSFlowRefInst;
import pssgen.inst.PSSInst;
import pssgen.inst.PSSScope;
import pssgen.model.PSSActionModel;
import pssgen.model.PSSExecKind;
import pssgen.model.PSSModel;
import pssgen.util.PSSMessage;

public class PSSTraversalList {
	ArrayList <PSSTraversal> m_list;
	boolean contain_flow_action;
	/** the action instance whose sub-actions these are */
	private final PSSActionInst m_owner;

	public PSSTraversalList(PSSActionInst owner) {
		m_list = new ArrayList <PSSTraversal>();
		m_owner = owner;
	}

	public void add(PSSTraversal t) {
		m_list.add(t);
	}

	public void addSequence(PSSActionInst inst) {
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
			int idx = m_owner.run().random().nextInt(0, candidate.size()-1);
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

	public void traverse() {
		if (contain_flow_action) {
			schedule();	// TODO: Fix performance issue
		}
		for (int i=0; i<m_list.size(); i++) {
			PSSTraversal t = m_list.get(i);
			PSSActionInst sub = t.getInst();
			PSSActionModel sub_action_decl = (PSSActionModel) sub.getTypeDecl();
			sub_action_decl.traverse(sub);
		}
	}

	public void inferSubAction(PSSActionInst parent_inst) {
		for (int i=0; i<m_list.size(); i++) {
			PSSTraversal t = m_list.get(i);
			PSSActionInst sub_ah = t.getInst();
			PSSUnboundRefList list = sub_ah.getUnboundRefList();

			if (0 < list.size()) {
				contain_flow_action = true;
			}

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
		parent_inst.run().log().Info("Infer action: " + action_model.getId());

		PSSModel type_model = uref.getTypeModel();

		PSSInst flowobj = type_model.declInst("", true);

		parent_inst.addInst(flowobj);

		uref.bind(flowobj);

		PSSActionInst inferred_inst = action_model.declInst(true);
		// An inferred action is traversed without being a child of parent_inst.
		inferred_inst.setRun(parent_inst.run());
		PSSTraversal inferred_t = new PSSTraversal(inferred_inst);
		m_list.add(inferred_t);

		PSSFlowRefInst uref2 = (PSSFlowRefInst) inferred_inst.findInstance(item.getRefId(), PSSScope.Context.FLOW_REF);
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
			int idx = parent_inst.run().random().nextInt(0, list.size()-1);
			PSSInferredAction item = list.get(idx);
			declInferredAction(parent_inst, t, uref, item);
		}
		else {
			PSSMessage.Error("INFER", "cannot infer action for " + uref.getTypeId());
		}
	}


	public void execute_kind(PSSExecKind kind) {
		for (int i=0; i<m_list.size(); i++) {
			PSSTraversal t = m_list.get(i);
			PSSActionInst sub_ah = t.getInst();
			sub_ah.execute_kind(kind);
		}
	}

	/**
	 * PSS 2.0 Section 17.4.6: an activity expression may read the fields of a
	 * sub-action handle that the activity has already traversed. Traverses, in their
	 * order, the sub-actions recorded so far up to and including handle, so that its
	 * fields exist and hold their solved values. A sub-action that is traversed here
	 * is not traversed again by {@link #traverse()}.
	 *
	 * @param handle a sub-action handle
	 * @return {@code false} when handle is not recorded, or when a sub-action up to it
	 *         still has an unbound flow object, which a later bind or inference fills
	 */
	public boolean traverseThrough(PSSActionInst handle) {
		int end = -1;
		for (int i = 0; i < m_list.size() && end < 0; i++) {
			if (m_list.get(i).getInst() == handle)
				end = i;
		}
		if (end < 0)
			return false;
		for (int i = 0; i <= end; i++) {
			if (m_list.get(i).getInst().getUnboundRefList().size() > 0)
				return false;
		}
		for (int i = 0; i <= end; i++) {
			PSSActionInst sub = m_list.get(i).getInst();
			((PSSActionModel) sub.getTypeDecl()).traverse(sub);
		}
		return true;
	}
}
