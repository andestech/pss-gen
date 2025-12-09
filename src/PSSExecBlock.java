import java.util.*;

public class PSSExecBlock extends PSSModel {

	List<PSSProcStmt> m_list;

	PSSExecKind m_kind;

	public PSSExecBlock(PSSExecKind kind, List<PSSProcStmt> stmts) {
		super("");
		m_kind = kind;
		m_list = stmts;
	}

	public PSSExecBlock(PSSExecKind kind) {
		this(kind, new ArrayList<PSSProcStmt>());
	}

	public void addStmt(PSSProcStmt stmt) {
		m_list.add(stmt);
		stmt.m_parent = this;
	}

	public PSSExecKind getKind() {
		return m_kind;
	}

	public void eval(PSSInst inst) {
		// Add an anonymous instance for exec local data
		// Associate the instance with this model so that the exec kind can be detected
		PSSInst block_inst = new PSSInst("", "", this, false);
		inst.addInst(block_inst);
		try {
			for (int i = 0; i < m_list.size(); i++) {
				PSSProcStmt stmt = m_list.get(i);
				// if (PSSMessage.isPrintDebugEn()) stmt.dump("PSSExecBlock.eval ");
				stmt.eval(block_inst, getKind());
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
