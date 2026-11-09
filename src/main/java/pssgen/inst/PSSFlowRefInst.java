package pssgen.inst;

import java.util.*;

import pssgen.model.PSSModel;
import pssgen.util.PSSMessage;

public class PSSFlowRefInst extends PSSInst {
	private PSSInst m_var;
	private boolean m_output;

	public PSSFlowRefInst(String id, String type, PSSModel type_decl, boolean output) {
		super(id, type, type_decl, false);
		m_var = null;
		m_output = output;
	}
	public void bind(PSSInst inst) {
		m_var = inst;
	}
	public PSSModel getTypeModel() {
		return getTypeDecl();
	}
	public boolean isOutput() {
		return m_output;
	}

	@Override
	public PSSInst findInstanceUnder(String hierarchy_id) {
		if (m_var == null) {
			PSSMessage.Error("REF", "'" + getHierarchyId() + "' is not bound");
		}
		return m_var.findInstanceUnder(hierarchy_id);
	}

	@Override
	public PSSInst findInstance(String hierarchy_id, PSSScope.Context context) {
		if (m_var == null) {
			PSSMessage.Error("REF", "'" + getHierarchyId() + "' is not bound");
		}
		return m_var.findInstance(hierarchy_id, context);
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
			System.out.println(indent + getId() + " <" + getType() + ">");
		}
	}

	public final PSSInst getVar() {
		return m_var;
	}
}
