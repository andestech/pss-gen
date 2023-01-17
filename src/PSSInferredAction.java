import java.util.*;

public class PSSInferredAction {
	PSSAction	m_action;
	PSSFlowRef	m_ref;

	PSSInferredAction (PSSAction action, PSSFlowRef ref) {
		m_action = action;
		m_ref = ref;
	}

	public PSSAction getAction() {
		return m_action;
	}
	public String getRefId() {
		return m_ref.getId();
	}
}


