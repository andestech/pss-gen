import java.util.*;

public class PSSModel {

	public ArrayList<PSSModel> children;
	public PSSModel m_parent;
	public String m_id;
	public String m_hierarchy_id;

	List<PSSInst> m_static_insts;

	public PSSModel(String id) {
		m_id = id;
		m_hierarchy_id = "";
		children = new ArrayList<PSSModel>();
		m_static_insts = new ArrayList<PSSInst>();
	}

	/**
	 * Returns {@code true} if the specified type model is compatible with this type
	 * model.
	 *
	 * @param model a type model
	 * @return {@code true} if the specified type model is compatible with this type
	 *         model
	 */
	public boolean isCompatible(PSSModel model) {
		return false;
	}

	public PSSModel addChild(PSSModel node) {
		children.add(node);
		node.m_parent = this;
		if (m_hierarchy_id.equals("")) {
			node.m_hierarchy_id = node.m_id;
		} else {
			node.m_hierarchy_id = this.m_hierarchy_id + "::" + node.m_id;
		}
		return node;
	}

	public void addStaticInst(PSSInst inst) {
		m_static_insts.add(inst);
	}

	public PSSInst findStaticInst(String id) {
		// static instances in this model
		for (PSSInst inst : m_static_insts) {
			if (inst.m_id.equals(id))
				return inst;
		}
		// enum items in this model
		for (PSSModel c : children) {
			if (c instanceof PSSEnumModel) {
				PSSEnumModel m = (PSSEnumModel) c;
				PSSInst inst = m.findStaticInst(id);
				if (inst != null)
					return inst;
			}
		}
		return null;
	}

	public void addComponentDataDecl(PSSComponentDataDeclModel data_decl) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::addComponentDataDecl is not impelemented");
	}

	public PSSFlowRefModel addFlowRef(PSSFlowRefModel node) {
		return null;
	}

	public PSSAttrField addAttrField(PSSAttrField node) {
		return null;
	}

	public PSSConstraint addConstraint(PSSConstraint node) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::addConstraint is not impelemented");
		return null;
	}

	public void addExecBlock(PSSExecBlock block) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::addExecBlock is not impelemented");
	}

	public PSSConstraintList getConstraintList() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::getConstraintList is not impelemented");
		return null;
	}

	/**
	 * Declares a new instance of this model.
	 *
	 * @param id   name of the instance
	 * @param rand {@code true} if the instance can be randomized
	 */
	public PSSInst declInst(String id, boolean rand) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::declInst is not impelemented");
		return null;
	}

	/**
	 * Declares a new instance of this model. The instance and this model will have
	 * the same ID. This method simply invokes {@link #declInst(String, boolean)}.
	 *
	 * @param rand the instance can be randomized
	 */
	public PSSInst declInst(boolean rand) {
		return declInst(m_id, rand);
	}

	/**
	 * Declares a new instance of the model in the parent instance.
	 *
	 * @param parent_inst the parent instance
	 * @param id          name of the instance
	 * @param rand        {@code true} if the instance can be randomized
	 */
	public PSSInst declInst(PSSInst parent_inst, String id, boolean rand) {
		PSSInst inst = declInst(id, rand);
		parent_inst.addInst(inst);
		return inst;
	}

	public void declEnumItem(PSSInst inst) {
		;
	}

	public void declEnumInst(PSSInst inst) {
		for (int i = 0; i < children.size(); i++) {
			PSSModel child = children.get(i);

			child.declEnumItem(inst);
		}
		// if (m_parent != null) {
		// 	m_parent.declEnumInst(inst);
		// }
	}

	public void init_up(PSSInst inst) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::init_up is not impelemented");
	}

	public void init_down(PSSInst inst) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::init_down is not impelemented");
	}

	public void evalActivity(PSSActionInst inst) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::evalActivity is not impelemented");
	}

	public void getTargetCode(PSSInst inst, String exec_kind) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::getTargetCode is not impelemented");
	}

	public PSSModel findObject(String hierarchy_id) {
		if (hierarchy_id.equals(m_hierarchy_id)) {
			return this;
		}

		for (int i = 0; i < children.size(); i++) {
			PSSModel node = children.get(i);
			node = node.findObject(hierarchy_id);
			if (node != null) {
				return node;
			}
		}

		return null;
	}

	/**
	 * Finds a declaration under this model.
	 *
	 * @param hierarchy_id
	 * @return
	 */
	public PSSModel findDeclarationUnder(String hierarchy_id) {
		String id = hierarchy_id;
		String child_id = "";

		String[] tokens = hierarchy_id.split("\\::", 2);
		if (tokens.length == 2) {
			id = tokens[0];
			child_id = tokens[1];
		}
		for (int i = 0; i < children.size(); i++) {
			PSSModel child = children.get(i);

			if (child.m_id.equals(id)) {
				if (child_id.length() == 0) {
					return child;
				} else {
					return child.findDeclaration(child_id);
				}
			}

			if (child.getClass().getSimpleName().equals("PSSImportStmtModel")) {
				PSSModel node = child.findDeclaration(id);
				if (node != null) {
					return node;
				}
			}
		}
		return null;
	}

	public PSSModel findDeclaration(String hierarchy_id) {
		/**
		 * @breif find the declaration of a mode from the current namespace
		 *
		 *        Examples:
		 *        - findDeclaration("action_a")
		 *        - findDeclaration("component_c::action_a")
		 *        - findDeclaration("package_p::component_c::action_a")
		 */
		PSSModel res = findDeclarationUnder(hierarchy_id);
		if (res != null)
			return res;

		if (m_parent == null)
			return null;
		else
			return m_parent.findDeclaration(hierarchy_id);
	}

	public PSSInferredActionList getInferredAction(boolean output, String flow_data_type) {
		PSSInferredActionList list = new PSSInferredActionList();
		for (int i = 0; i < children.size(); i++) {
			PSSModel child = children.get(i);
			PSSFlowRefModel ref = child.getFlowRef(output, flow_data_type);

			if (ref != null) {
				PSSInferredAction infa = new PSSInferredAction((PSSActionModel) child, ref);
				list.add(infa);
			}
		}
		if (m_parent != null) {
			PSSInferredActionList parent_list = m_parent.getInferredAction(output, flow_data_type);
			list.addAll(parent_list);
		}
		return list;
	}

	public PSSComponentModel getComp() {
		if (m_parent != null) {
			if (m_parent.getClass().getSimpleName().equals("PSSComponentModel")) {
				return (PSSComponentModel) m_parent;
			} else {
				return m_parent.getComp();
			}
		}
		return null;
	}

	public PSSModel findPackageDeclaration(String id) {
		for (int i = 0; i < children.size(); i++) {
			PSSModel child = children.get(i);
			if (child.m_id.equals(id)) {
				return child;
			}
		}
		if (m_parent == null)
			return null;
		else
			return m_parent.findPackageDeclaration(id);
	}

	public PSSFlowRefModel getFlowRef(boolean output, String flow_data_type) {
		return null;
	}

	public String getId() {
		return m_id;
	}

	public String getText() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::getText is not impelemented");
		return null;
	}

	public void dump(String indent) {
		for (int i = 0; i < children.size(); i++) {
			PSSModel node = children.get(i);
			node.dump(indent);
		}
	}
}
