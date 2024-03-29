import java.util.*;

public class PSSSequenceProcStmt extends PSSProcStmt {

	ArrayList<PSSProcStmt> m_list;

	public PSSSequenceProcStmt() {
		m_list = new ArrayList<PSSProcStmt>();
	}

	public void add(PSSProcStmt stmt) {
		m_list.add(stmt);
	}

	public void eval(PSSInst inst) {
		PSSNamespaceInst body_inst = new PSSNamespaceInst("");
		inst.addInst(body_inst);
		for (int i=0; i<m_list.size(); i++) {
			PSSProcStmt stmt = m_list.get(i);
			stmt.eval(body_inst);
		}
	}

	public void dump(String indent) {
		for (int i=0; i<m_list.size(); i++) {
			PSSProcStmt stmt = m_list.get(i);
			stmt.dump(indent+"\t");
		}
	}
}
