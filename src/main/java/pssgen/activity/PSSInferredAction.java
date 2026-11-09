package pssgen.activity;

import java.util.*;

import pssgen.model.PSSActionModel;
import pssgen.model.PSSFlowRefModel;

public class PSSInferredAction {
	PSSActionModel	m_action;
	PSSFlowRefModel	m_ref;

	public PSSInferredAction (PSSActionModel action, PSSFlowRefModel ref) {
		m_action = action;
		m_ref = ref;
	}

	public PSSActionModel getAction() {
		return m_action;
	}
	public String getRefId() {
		return m_ref.getId();
	}
}


