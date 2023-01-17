import java.util.*;

public class PSSActionInst extends PSSInst {
	
	PSSTraversalList		m_sub_list;
	ArrayList<PSSActionInst>	m_dependency;

	// inleine constraints
	PSSConstraintList m_c_list;

	public PSSActionInst(String id, String type, PSSModel type_decl) {
		super(id, type, type_decl, false);
		m_sub_list = new PSSTraversalList();
		m_c_list = new PSSConstraintList();
	}

	public void addTraversal(PSSTraversal t) {
		m_sub_list.add(t);
	}
	public void addSequentialTraversal(PSSActionInst inst) {
		m_sub_list.addSequence(inst);
	}


	public void traverseSubAction() {
		m_sub_list.traverse();
	}

	public void inferSubAction() {
		m_sub_list.inferSubAction(this);

	}

	public PSSInferredActionList getInferredAction(boolean output, String flow_data_type) {
		return m_type_decl.getInferredAction(output, flow_data_type);
	}



	public PSSUnboundRefList getUnboundRefList() {
		PSSUnboundRefList list = new PSSUnboundRefList();

		for (int i=0; i<m_insts.size(); i++) {
			PSSInst inst = m_insts.get(i);
			if (inst.getClass().getSimpleName().equals("PSSRefInst")) {
				PSSRefInst ref = (PSSRefInst)inst;
				if (!ref.isBound()) {
					PSSUnboundRef uref = new PSSUnboundRef(ref);
					list.add(uref);
				}
			}
		}

		return list;
	}


	public void collectConstraint(PSSSolver solver) {
		// Child
		for (int i=0; i<m_insts.size(); i++) {
			PSSInst inst = m_insts.get(i);
			solver.add(inst);
		}
	}

	public void addConstraint(PSSConstraintList c_list) {
		for (int i=0; i<c_list.size(); i++) {
			PSSConstraint c = c_list.get(i);
			m_c_list.add(c);
		}
	}
	public PSSConstraintList getConstraintList() {
		return m_c_list;
	}

	public void execute_kind(String kind) {
		m_type_decl.getTargetCode(this, kind);
		m_sub_list.execute_kind(kind);
	}

	public void execute() {
		execute_kind("header");
		execute_kind("declaration");
		execute_kind("body");
	}

	public void randomize() {

		for (int i=0; i<m_insts.size(); i++) {
			PSSInst var = m_insts.get(i);

			String id = var.m_id;

			String ClassName = var.getClass().getSimpleName();
			if (ClassName.equals("PSSActionInst")) {
				;	// Sbu-Action
			}
			else {
				var.randomize();
			}
		}
	}
}
