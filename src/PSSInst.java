import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class PSSInst {

	public String m_id;
	public PSSInst m_parent;
	public String m_type;
	public PSSModel m_type_decl;
	public boolean m_constant;
	public boolean m_rand;
	public boolean m_initialized;

	private boolean m_traversed;
	public ArrayList<PSSInst> m_insts;

	// buildin type
	public PSSInst(String id, String type, boolean rand) {
		m_id = id;
		m_type = type;
		m_rand = rand;
		m_initialized = false;
		m_insts = new ArrayList<PSSInst>();
	}
	// non-buildin type
	public PSSInst(String id, String type, PSSModel type_decl, boolean rand) {
		m_id = id;
		m_type = type;
		m_type_decl = type_decl;
		m_rand = rand;
		m_initialized = false;
		m_traversed = false;
		m_insts = new ArrayList<PSSInst>();
	}
	public PSSInst addInst (PSSInst inst) {
		m_insts.add(inst);
		inst.m_parent = this;
		return inst;
	}
	public void removeInst (PSSInst inst) {
		m_insts.remove(inst);
	}
	public boolean isTraversed() {
		return m_traversed;
	}
	public boolean isComponent() {
		return false;
	}
	public void setTraversed() {
		m_traversed = true;
	}
	public String getHierarchyId() {
		if (m_parent != null) {
			return m_parent.getHierarchyId() + "." + m_id;
		}
		else {
			return m_id;
		}
	}
	public PSSModel getTypeModel() {
		return m_type_decl;
	}

	private PSSInst findInstanceOne(String id) {
		for (int i=0; i<m_insts.size(); i++) {
			PSSInst child = m_insts.get(i);
			if (child.m_id.equals(id)) {
				return child;
			}
		}
		return null;
	}

	public PSSInst getComponentInst() {
		if (getClass().getSimpleName().equals("PSSComponentInst")) {
			return this;
		}
		else if (m_parent != null) {
			return m_parent.getComponentInst();
		}
		return null;
	}

	public PSSInst findInstance(String hierarchy_id) {
		if (hierarchy_id.matches("comp")) {
			return getComponentInst();
		}
		else if (hierarchy_id.matches("comp\\..*")) {
			PSSInst comp = getComponentInst();
			if (comp != null) {
				return comp.findInstance(hierarchy_id.substring(5));
			}
			else {
				return null;
			}
		}
		else if (hierarchy_id.matches("this\\..*")) {
			if (m_parent != null) {
				return m_parent.findInstance(hierarchy_id.substring(5));
			}
			else {
				return null;
			}
		}


		String[] tokens=hierarchy_id.split("\\.", 2);  

		PSSInst result = findInstanceOne(tokens[0]);

		if (result != null) {
			if (tokens.length <= 1) {
				return result;
			}
			else {
				return result.findInstance(tokens[1]);
			}
		}

		if (m_parent == null)
			return null;
		else
			return m_parent.findInstance(hierarchy_id);
	}
	public void init_up() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::init_up is not impelemented");
	}
	public void init_down() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::init_down is not impelemented");
	}
	public PSSInst indexOf(int index) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::indexOf is not impelemented");
		return null;
	}
	public PSSDomain getInitDomain() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::getInitDomain is not impelemented");
		return null;
	}
	void assignDomain(PSSDomain domain) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::assignDomain is not impelemented");
	}
	public void randomize() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::randomize is not impelemented");
	}
	public void assign(PSSVal val) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::assign is not impelemented");
	}
	public String toTargetCode() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::toTargetCode is not impelemented");
		return "";
	}
	public PSSVal toVal() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::toVal is not impelemented");
		return null;
	}
	public PSSConstraintList getConstraintList() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::getConstraintList is not impelemented");
		return null;
	}
	public void addTraversal(PSSActionInst inst) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::addTraversal is not impelemented");
	}
	public void addSequentialTraversal(PSSActionInst inst) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::addSequentialTraversal is not impelemented");
	}
	public void traverseSubAction() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::traverseSubAction is not impelemented");
	}
	public void inferSubAction() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::inferSubAction is not impelemented");
	}
	public void inferAction(PSSInst parent) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::inferAction is not impelemented");
	}
	public void collectConstraint(PSSSolver solver) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::collectConstraint is not impelemented");
	}


	public void execute() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::execute is not impelemented");
	}
	public void execute_kind(String kind) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::execute_kind is not impelemented");
	}

	public void dump(String indent) {
		System.out.println(indent + m_id + " <" + m_type + ">" + " " + this.getClass().getSimpleName());
		ArrayList<PSSInst> children = m_insts;
		for (int i=0; i<children.size(); i++) {
			PSSInst inst = children.get(i);
			inst.dump(indent + "\t");
		}
	}
}
