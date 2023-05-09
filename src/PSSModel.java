import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class PSSModel {

	public ArrayList<PSSModel> children;
	public PSSModel m_parent;
	public String m_id;
	public String m_hierarchy_id;

	public PSSModel(String id) {
		m_id = id;
		m_hierarchy_id = "";
		children = new ArrayList<PSSModel>();
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

	public void addComponentDataDecl(PSSComponentDataDeclModel data_decl) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::addComponentDataDecl is not impelemented");
	}

	public PSSFlowRef addFlowRef(PSSFlowRef node) {
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

	public PSSInst declInst(String id, boolean rand) {
		/**
		 * declare a new instance with the model
		 *
		 * @param id   name of the instance
		 * @param rand the instance can be randomized
		 */
		PSSMessage.Fatal(getClass().getSimpleName() + "::declInst is not impelemented");
		return null;
	}

	public PSSInst declInst(boolean rand) {
		/**
		 * declare a new anonymous instance with the model
		 *
		 * @param rand the instance can be randomized
		 */
		return declInst("<" + m_id + ">", rand);
	}

	public void declInst(PSSInst parent_inst, String id, boolean rand) {
		/**
		 * declare a new instance with the model in the parent instance
		 *
		 * @param parent_inst the parent instance
		 * @param id          name of the instance
		 * @param rand        the instance can be randomized
		 */
		PSSInst inst = declInst(id, rand);
		parent_inst.addInst(inst);
	}

	public void declEnumItem(PSSInst inst) {
		;
	}

	public void declEnumInst(PSSInst inst) {
		for (int i = 0; i < children.size(); i++) {
			PSSModel child = children.get(i);

			child.declEnumItem(inst);
		}
		if (m_parent != null) {
			m_parent.declEnumInst(inst);
		}
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

	public PSSModel findDeclaration(String hierarchy_id) {
		/**
		 * @breif find the declaration of a mode from the current namespace
		 *
		 *        Examples:
		 *        - findDeclaration("action_a")
		 *        - findDeclaration("component_c::action_a")
		 *        - findDeclaration("package_p::component_c::action_a")
		 */
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
		if (m_parent == null)
			return null;
		else
			return m_parent.findDeclaration(hierarchy_id);
	}

	public PSSInferredActionList getInferredAction(boolean output, String flow_data_type) {
		PSSInferredActionList list = new PSSInferredActionList();
		for (int i = 0; i < children.size(); i++) {
			PSSModel child = children.get(i);
			PSSFlowRef ref = child.getFlowRef(output, flow_data_type);

			if (ref != null) {
				PSSInferredAction infa = new PSSInferredAction((PSSAction) child, ref);
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

	public PSSFlowRef getFlowRef(boolean output, String flow_data_type) {
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
