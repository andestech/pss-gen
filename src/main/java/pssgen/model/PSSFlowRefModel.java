package pssgen.model;

import java.util.*;

import pssgen.inst.PSSFlowRefInst;
import pssgen.inst.PSSInst;

public class PSSFlowRefModel extends PSSModel {

	private boolean m_output;	// input, output
	private String m_type;

	public PSSFlowRefModel(String id, boolean output, String type) {
		super(id);
		m_output = output;
		m_type = type;
	}

	public PSSFlowRefInst declInst(PSSInst parent_inst, PSSModel type_model) {
		PSSFlowRefInst flowref = new PSSFlowRefInst(getId(), m_type, type_model, m_output);
		parent_inst.addInst(flowref);
		return flowref;
	}

	public boolean hasFlowRef(boolean output, String flow_data_type) {
		return (m_output == output) && m_type.equals(flow_data_type);
	}

	public void dump (String indent) {
		String direction = m_output ? "output" : "input";
		System.out.println(indent + direction + " " + m_type + " " + getId());
	}

	public Integer eval(PSSInst inst) {
		return 0;
	}

	public final boolean isOutput() {
		return m_output;
	}

	public final String getType() {
		return m_type;
	}
}
