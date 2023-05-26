import java.util.*;

public class PSSExecBlock extends PSSModel {

	ArrayList<PSSProcStmt> m_list;
	String m_kind;

	public PSSExecBlock(String kind) {
		super("");
		m_kind = kind;
		m_list = new ArrayList<PSSProcStmt>();
	}

	public void addStmt(PSSProcStmt stmt) {
		m_list.add(stmt);
		stmt.m_parent = this;
	}

	public String getKind() {
		return m_kind;
	}

	public void eval(PSSInst inst) {
		// Add an anonymous instance for exec local data
		PSSInst block_inst = new PSSInst("", "", false);
		inst.addInst(block_inst);
		try {
			for (int i = 0; i < m_list.size(); i++) {
				PSSProcStmt stmt = m_list.get(i);
				stmt.eval(block_inst);
			}
		} catch (PSSReturnException e) {
		}
		inst.removeInst(block_inst);
	}

	public void dump(String indent) {
		System.out.println(indent + "exec " + m_kind + " {");
		for (int i = 0; i < m_list.size(); i++) {
			PSSProcStmt stmt = m_list.get(i);
			stmt.dump(indent + "\t");
		}
		System.out.println(indent + "}");
	}
}
