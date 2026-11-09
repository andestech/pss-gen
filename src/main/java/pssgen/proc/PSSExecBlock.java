package pssgen.proc;

import java.util.*;

import pssgen.inst.PSSInst;
import pssgen.inst.PSSNamespaceInst;
import pssgen.model.PSSExecKind;
import pssgen.model.PSSModel;

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
		stmt.setParent(this);
	}

	@Override
	public PSSExecKind getExecKind() {
		return getKind();
	}

	public PSSExecKind getKind() {
		return m_kind;
	}

	public void eval(PSSInst inst) {
		// An anonymous scope for exec local data (PSS 2.0 Section 22.7.1), associated
		// with this model so that the exec kind can be detected
		PSSInst block_inst = new PSSNamespaceInst("", this);
		inst.addInst(block_inst);
		try {
			for (int i = 0; i < m_list.size(); i++) {
				PSSProcStmt stmt = m_list.get(i);
				// if (PSSMessage.isPrintDebugEn()) stmt.dump("PSSExecBlock.eval ");
				stmt.eval(block_inst, getKind());
			}
		} catch (PSSReturnException e) {
		} catch (PSSBreakException e) {
			PSSProcStmt.outsideLoop("break");
		} catch (PSSContinueException e) {
			PSSProcStmt.outsideLoop("continue");
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
