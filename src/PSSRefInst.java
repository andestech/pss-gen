import java.util.*;

public class PSSRefInst extends PSSInst {
	public PSSInst m_var;
	public boolean m_output;

	public PSSRefInst(String id, String type, PSSModel type_decl, boolean output) {
		super(id, type, type_decl, false);
		m_var = null;
		m_output = output;
	}
	public void bind(PSSInst inst) {
		m_var = inst;
	}
	public PSSModel getTypeModel() {
		return m_type_decl;
	}
	public boolean isOutput() {
		return m_output;
	}

	public PSSInst findInstance(String hierarchy_id) {
		if (m_var == null) {
			PSSMessage.Error("REF", "'" + getHierarchyId() + "' is not bound");
		}
		return m_var.findInstance(hierarchy_id);
	}

	public boolean isBound() {
		return m_var != null;
	}

	public void randomize() {
		if (m_var == null) {
			PSSMessage.Error("REF", "'" + getHierarchyId() + "' is not bound");
		}
		m_var.randomize();
	}

	public void dump(String indent) {
		if (m_var != null) {
			m_var.dump(indent);
		}
		else {
			System.out.println(indent + m_id + " <" + m_type + ">");
		}
	}
}
