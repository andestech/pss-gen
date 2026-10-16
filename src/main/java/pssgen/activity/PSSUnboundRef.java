package pssgen.activity;

import java.util.*;

import pssgen.inst.PSSFlowRefInst;
import pssgen.inst.PSSInst;
import pssgen.model.PSSModel;

public class PSSUnboundRef {
	PSSFlowRefInst	m_ref;

	public PSSUnboundRef(PSSFlowRefInst ref) {
		m_ref = ref;
	}

	PSSModel getTypeModel() {
		return m_ref.getTypeModel();
	}

	public void bind(PSSInst inst) {
		m_ref.bind(inst);
	}

	public boolean isOutput() {
		return m_ref.isOutput();
	}

	public String getTypeId() {
		return m_ref.getTypeModel().getId();
	}
}

