import java.util.*;

public class PSSTargetCodeList {
	public ArrayList<PSSTargetCode> m_list;

	PSSTargetCodeList() {
		m_list = new ArrayList<PSSTargetCode>();
	}

	void add(PSSTargetCode target_code) {
		m_list.add(target_code);
	}

	void execute_kind(PSSInst inst, PSSExecKind exec_kind) {
		for (int i=0; i<m_list.size(); i++) {
			PSSTargetCode target_code = m_list.get(i);
			target_code.execute_kind(inst, exec_kind);
		}
	}
	void dump(String indent) {
		for (int i=0; i<m_list.size(); i++) {
			PSSTargetCode target_code = m_list.get(i);
			target_code.dump(indent);
		}
	}
}

