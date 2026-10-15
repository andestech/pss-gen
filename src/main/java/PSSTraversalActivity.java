import java.util.*;

public class PSSTraversalActivity extends PSSActivity {

	public PSSConstraintList m_constraint;
	public PSSConstraintList m_default_constraint;
	String m_handle_id;
	String m_type_id;
	PSSActionInst m_handle_var;

	public PSSTraversalActivity(String handle_id, String type_id) {
		super();
		m_handle_id = handle_id;
		m_type_id = type_id;
		m_constraint = new PSSConstraintList();
		m_default_constraint = new PSSConstraintList();
		m_handle_var = null;
	}
	public PSSConstraint addConstraint(PSSConstraint node) {
		m_constraint.add(node);
		node.m_parent = this;
		return node;
	}
	public PSSConstraint addDefaultConstraint(PSSConstraint node) {
		m_default_constraint.add(node);
		node.m_parent = this;
		return node;
	}
	public void evalActivity(PSSInst inst) {
		if (m_handle_id.length() > 0) {
			evalNamedActivity(inst);
		}
		else {
			evalAnonymousActivity(inst);
		}
	}
	public void evalAnonymousActivity(PSSInst inst) {
		PSSActionModel sub_action_decl = (PSSActionModel) findDeclaration(m_type_id);
		if (sub_action_decl == null) {
			PSSMessage.Error("TRAVERSAL", "Cannot find type declaration '" + m_type_id + "'");
		}

		// Create Inst
		PSSActionInst handle_var = sub_action_decl.declInst(true);
		inst.addInst(handle_var);
		handle_var.addConstraint(m_constraint);

		inst.addSequentialTraversal(handle_var);
	}

	public void evalNamedActivity(PSSInst inst) {
		// Find child action inst
		m_handle_var = (PSSActionInst) inst.findInstance(m_handle_id);
		if (m_handle_var == null) {
			PSSMessage.Error("TRAVERSAL", "Cannot action handle '" + m_handle_id + "'");
		}

		// Add inline constraint
		m_handle_var.addConstraint(m_constraint);

		inst.addSequentialTraversal(m_handle_var);
	}

	//public void executeActivity(PSSActionInst inst) {
	//	// Traverse SubAction
	//	PSSAction action_decl = (PSSAction) m_handle_var.m_type_decl;
	//	action_decl.execute(m_handle_var);
	//}

	public void dump(String indent) {
		if (m_constraint.size() == 0) {
			System.out.println(indent + m_handle_id);
		}
		else {
			System.out.println(indent + m_handle_id + " with {");
			for (int i=0; i<m_constraint.size(); i++) {
				PSSConstraint c= m_constraint.get(i);
				c.dump(indent + "\t");
			}
			System.out.println(indent + "}");
		}
	}
}
