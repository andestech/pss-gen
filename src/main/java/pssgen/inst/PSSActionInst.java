package pssgen.inst;

import java.util.*;

import pssgen.activity.PSSInferredActionList;
import pssgen.activity.PSSTraversal;
import pssgen.activity.PSSTraversalList;
import pssgen.activity.PSSUnboundRef;
import pssgen.activity.PSSUnboundRefList;
import pssgen.model.PSSExecKind;
import pssgen.model.PSSModel;
import pssgen.model.PSSRefModel;
import pssgen.model.PSSTypeCategory;
import pssgen.solver.PSSConstraint;
import pssgen.solver.PSSConstraintList;
import pssgen.solver.PSSSolver;
import pssgen.value.PSSRefVal;

public class PSSActionInst extends PSSInst {

	PSSTraversalList m_sub_list;
	ArrayList<PSSActionInst> m_dependency;

	// inline constraints
	PSSConstraintList m_c_list;

	public PSSActionInst(String id, String type, PSSModel type_decl) {
		super(id, type, type_decl, false);
		m_sub_list = new PSSTraversalList(this);
		m_c_list = new PSSConstraintList();

		// Declare the built-in field "comp" associated with an action. The field comp
		// cannot be assigned by the user. The target of comp shall be decided when this
		// action is randomized.
		PSSComponentRefInst comp = new PSSComponentRefInst("comp");
		comp.setReadOnly(true);
		addInst(comp);
	}

	public void addTraversal(PSSTraversal t) {
		m_sub_list.add(t);
	}

	@Override
	public void addSequentialTraversal(PSSActionInst inst) {
		m_sub_list.addSequence(inst);
	}

	@Override
	public void traverseSubAction() {
		m_sub_list.traverse();
	}

	@Override
	public void inferSubAction() {
		m_sub_list.inferSubAction(this);

	}

	public PSSInferredActionList getInferredAction(boolean output, String flow_data_type) {
		return getTypeDecl().getInferredAction(output, flow_data_type);
	}

	public PSSUnboundRefList getUnboundRefList() {
		PSSUnboundRefList list = new PSSUnboundRefList();

		for (int i = 0; i < getInsts().size(); i++) {
			PSSInst inst = getInsts().get(i);
			if (inst instanceof PSSFlowRefInst) {
				PSSFlowRefInst ref = (PSSFlowRefInst) inst;
				if (!ref.isBound()) {
					PSSUnboundRef uref = new PSSUnboundRef(ref);
					list.add(uref);
				}
			}
		}

		return list;
	}

	@Override
	public void collectConstraint(PSSSolver solver) {
		// Child
		for (int i = 0; i < getInsts().size(); i++) {
			PSSInst inst = getInsts().get(i);
			solver.add(inst);
		}
	}

	public void addConstraint(PSSConstraintList c_list) {
		for (int i = 0; i < c_list.size(); i++) {
			PSSConstraint c = c_list.get(i);
			m_c_list.add(c);
		}
	}

	@Override
	public PSSConstraintList getConstraintList() {
		return m_c_list;
	}

	@Override
	public void execute_kind(PSSExecKind kind) {
		getTypeDecl().getTargetCode(this, kind);
		m_sub_list.execute_kind(kind);
	}

	@Override
	public void execute() {
		execute_kind(PSSExecKind.header);
		execute_kind(PSSExecKind.declaration);
		execute_kind(PSSExecKind.body);
	}

	@Override
	public void randomize() {
		for (int i = 0; i < getInsts().size(); i++) {
			PSSInst var = getInsts().get(i);

			if (var instanceof PSSActionInst) {
				; // Sub-action is randomized when it is to be traversed
			} else {
				var.randomize();
			}
		}
	}

	@Override
	public PSSRefVal toVal() {
		return new PSSRefVal(new PSSRefModel(PSSTypeCategory.ACTION), this);
	}

	/**
	 * Traverses the sub-actions this action's activity has recorded, in order, up to
	 * and including sub (see {@link PSSTraversalList#traverseThrough}).
	 *
	 * @param sub a sub-action handle of this action
	 * @return {@code true} if sub has been traversed
	 */
	public boolean traverseRecorded(PSSActionInst sub) {
		return m_sub_list.traverseThrough(sub);
	}

	/** the default constraints of the in-line constraints of this action's traversals */
	private final PSSConstraintList m_def_c_list = new PSSConstraintList();

	/**
	 * Adds the default constraints of an in-line {@code with} block of a traversal of
	 * this action (PSS 2.0 Section 17.1.3).
	 *
	 * @param c_list the default constraints
	 */
	public void addDefaultConstraint(PSSConstraintList c_list) {
		for (int i = 0; i < c_list.size(); i++)
			m_def_c_list.add(c_list.get(i));
	}

	public PSSConstraintList getDefaultConstraintList() {
		return m_def_c_list;
	}

	/** the handle field instance this instance traverses, which is this unless retraversed */
	private PSSActionInst m_handle = this;
	/** for a handle field instance: the instance of its traversals now in the field */
	private PSSActionInst m_current = this;
	/** whether an activity has recorded a traversal of this instance */
	private boolean m_recorded;

	/**
	 * The instance a traversal of this action handle records. PSS 2.0 Section 13.3.1 a):
	 * the handle is randomized and evaluated where each traversal occurs, and Section
	 * 17.1.3: the in-line constraints of a traversal apply to that traversal only. The
	 * first traversal records this instance; a later one records a new instance of the
	 * same type, which takes the place of this one in the parent.
	 *
	 * @return the instance to record
	 */
	public PSSActionInst recordTraversal() {
		if (!m_recorded) {
			m_recorded = true;
			return this;
		}
		PSSActionInst fresh = (PSSActionInst) getTypeDecl().declInst(getId(), false);
		fresh.m_recorded = true;
		fresh.m_handle = m_handle;
		getParent().replaceInst(this, fresh);
		m_handle.m_current = fresh;
		return fresh;
	}

	/**
	 * Puts this traversal instance of a handle in the handle's place before it is
	 * traversed, so that a reference to the handle reads the most recently selected
	 * values (PSS 2.0 Section 17.1.3).
	 */
	public void takeHandlePlace() {
		PSSActionInst cur = m_handle.m_current;
		if (cur != this) {
			getParent().replaceInst(cur, this);
			m_handle.m_current = this;
		}
	}
}
