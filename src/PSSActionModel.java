import java.util.*;

public class PSSActionModel extends PSSModel {

	public ArrayList<PSSFlowRefModel> m_flowref;
	public ArrayList<PSSAttrField> m_attrfield;
	public ArrayList<PSSActivity> m_activity;
	public PSSConstraintList m_constraint;
	public PSSConstraintList m_default_constraint;
	public ArrayList<PSSExecBlock> m_exec_list;
	public PSSTargetCodeList m_target_code;

	public PSSActionModel(String id) {
		super(id);
		m_activity  = new ArrayList<PSSActivity>();
		m_flowref   = new ArrayList<PSSFlowRefModel>();
		m_attrfield = new ArrayList<PSSAttrField>();
		m_constraint = new PSSConstraintList();
		m_default_constraint = new PSSConstraintList();
		m_exec_list = new ArrayList<PSSExecBlock>();
		m_target_code = new PSSTargetCodeList();
	}

	@Override
	public PSSActionInst declInst(boolean rand) {
		return (PSSActionInst) super.declInst(rand);
	}

	public PSSActionInst declInst(String id, boolean rand) {
		PSSActionInst inst = new PSSActionInst(id, m_id, this);
		for (int i=0; i<m_flowref.size(); i++) {
			PSSFlowRefModel node = m_flowref.get(i);

			PSSModel type_model = findDeclaration(node.m_type);
			if (type_model == null) {
				PSSMessage.Error("ACTION", "Cannot find type declaration " + node.m_type);
			}

			// Declare Reference Inst
			PSSFlowRefInst flowref = node.declInst(inst, type_model);
		}

		return inst;
	}
	public void addActivityStmt (PSSActivity node) {
		m_activity.add(node);
		node.m_parent = this;
	}
	public PSSFlowRefModel addFlowRef (PSSFlowRefModel node) {
		m_flowref.add(node);
		node.m_parent = this;
		return node;
	}
	public PSSAttrField addAttrField (PSSAttrField node) {
		m_attrfield.add(node);
		node.m_parent = this;
		return node;
	}
	public PSSConstraint addConstraint(PSSConstraint node) {
		m_constraint.add(node);
		node.m_parent = this;
		return node;
	}

	public PSSConstraint addDefaultConstraint (PSSConstraint node) {
		m_default_constraint.add(node);
		node.m_parent = this;
		return node;
	}

	public PSSTargetCode addTargetCode (PSSTargetCode node) {
		m_target_code.add(node);
		return node;
	}
	public void addExecBlock (PSSExecBlock block) {
		PSSExecKind kind = block.getKind();
		if (kind.equals(PSSExecKind.header) || kind.equals(PSSExecKind.declaration) || kind.equals(PSSExecKind.body)) {
		}
		else if (kind.equals(PSSExecKind.pre_solve) || kind.equals(PSSExecKind.post_solve)) {
			;
		}
		else if (kind.equals(PSSExecKind.init_up) || kind.equals(PSSExecKind.init_down) || kind.equals(PSSExecKind.init)) {
			PSSMessage.Error("ACTION", kind + " is only valid in component");
		}
		else if (kind.equals(PSSExecKind.run_start) || kind.equals(PSSExecKind.run_end)) {
			PSSMessage.Error("ACTION", kind + " is not yet supported");
		}
		m_exec_list.add(block);
		block.m_parent = this;
	}

	private Integer evalAttrField(PSSInst inst) {
		for (int i=0; i<m_attrfield.size(); i++) {
			PSSAttrField attrfield = m_attrfield.get(i);
			attrfield.eval(inst);
		}
		return 0;
	}

	public void evalPreSolve(PSSInst inst) {
		if (m_exec_list.size() > 0) PSSMessage.Info("pre_solve action '" + inst.m_type+ "'");
		for (int i = 0; i < m_exec_list.size(); i++) {
			PSSExecBlock block = m_exec_list.get(i);
			if (block.getKind().equals(PSSExecKind.pre_solve)) {
				block.eval(inst);
			}
		}
	}

	public void evalPostSolve(PSSInst inst) {
		if (m_exec_list.size() > 0) PSSMessage.Info("post_solve action '" + inst.m_type+ "'");
		for (int i = 0; i < m_exec_list.size(); i++) {
			PSSExecBlock block = m_exec_list.get(i);
			if (block.getKind().equals(PSSExecKind.post_solve)) {
				block.eval(inst);
			}
		}
	}

	public void evalActivity(PSSInst inst) {
		for (int i=0; i<m_activity.size(); i++) {
			PSSActivity item = m_activity.get(i);

			item.evalActivity(inst);

		}
	}

	public  PSSSolver createSolver(PSSInst inst)
	{
		PSSSolver solver = new PSSSolver(inst);

		// constraint declaration
		solver.add(inst, m_constraint);
		solver.addDefaultConstraint(inst, m_default_constraint);
		// inline constraint
		solver.add(inst, inst.getConstraintList());

		return solver;
	}


	public Integer traverse(PSSActionInst inst) {
		if (inst.isTraversed()) {
			return 0;
		}
		inst.setTraversed();

		PSSMessage.Info("traverse action '" + inst.m_type+ "'");

		// Create solver
		PSSSolver solver = createSolver(inst);

		evalAttrField(inst);
		inst.m_initialized = true;

		inst.collectConstraint(solver);

		// pre_solve
		evalPreSolve(inst);

		// solve
		PSSMessage.Info("solve action '" + inst.m_type+ "'");
		int interation = solver.solve();
		PSSMessage.Debug("solve iteration: " + String.valueOf(interation));

		// post_solve
		evalPostSolve(inst);

		// activity
		evalActivity(inst);

		// infer action that use flow object
		inst.inferSubAction();

		// traverse sub-actions
		inst.traverseSubAction();

		return 0;
	}

	public void getTargetCode(PSSInst inst, PSSExecKind exec_kind) {
		m_target_code.execute_kind(inst, exec_kind);
	}

	public PSSFlowRefModel getFlowRef(boolean output, String flow_data_type) {
		for (int i=0; i<m_flowref.size(); i++) {
			PSSFlowRefModel node = m_flowref.get(i);
			if (node.hasFlowRef(output, flow_data_type)) {
				return node;
			}
		}
		return null;
	}

	public void dump_activity (String indent) {
		if (m_activity.size() == 0)
			return;
		System.out.println(indent + "activity {");
		for (int i=0; i<m_activity.size(); i++) {
			PSSActivity item = m_activity.get(i);
			item.dump(indent + "\t");
		}
		System.out.println(indent + "}");
	}
	public void dump_attrfield (String indent) {
		for (int i=0; i<m_attrfield.size(); i++) {
			PSSModel node = m_attrfield.get(i);
			node.dump(indent);
		}
	}
	public void dump_constraint(String indent) {
		for (int i=0; i<m_constraint.size(); i++) {
			System.out.println(indent + "constraint ");
			PSSConstraint c = m_constraint.get(i);
			c.dump(indent + "\t");
		}
	}
	public void dump (String indent) {

		System.out.println(indent + "action " +  m_id + " {");

		for (int i=0; i<m_flowref.size(); i++) {
			PSSModel node = m_flowref.get(i);
			node.dump(indent + "\t");
		}

		dump_attrfield(indent + "\t");
		dump_constraint(indent + "\t");

		for (int i=0; i<m_exec_list.size(); i++) {
			PSSExecBlock block = m_exec_list.get(i);
			block.dump(indent + "\t");
		}

		dump_activity(indent + "\t");

		m_target_code.dump(indent + "\t");

		System.out.println(indent + "}");
	}
}
