import java.util.*;

public class PSSUnboundRef {
	PSSFlowRefInst	m_ref;

	PSSUnboundRef(PSSFlowRefInst ref) {
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

