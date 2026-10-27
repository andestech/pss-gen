package pssgen.proc;

import java.util.*;

import pssgen.inst.PSSInst;
import pssgen.model.PSSExecKind;

public class PSSTargetCodeList {
	private ArrayList<PSSTargetCode> m_list;

	public PSSTargetCodeList() {
		m_list = new ArrayList<PSSTargetCode>();
	}

	public void add(PSSTargetCode target_code) {
		m_list.add(target_code);
	}

	public void execute_kind(PSSInst inst, PSSExecKind exec_kind) {
		for (int i=0; i<m_list.size(); i++) {
			PSSTargetCode target_code = m_list.get(i);
			target_code.execute_kind(inst, exec_kind);
		}
	}
	public void dump(String indent) {
		for (int i=0; i<m_list.size(); i++) {
			PSSTargetCode target_code = m_list.get(i);
			target_code.dump(indent);
		}
	}

	public final ArrayList<PSSTargetCode> getList() {
		return m_list;
	}
}
