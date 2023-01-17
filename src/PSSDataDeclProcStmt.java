import java.util.*;

public class PSSDataDeclProcStmt extends PSSProcStmt {

	PSSModel m_type;
	ArrayList<PSSDataInstProcStmt> m_list;

	public PSSDataDeclProcStmt(PSSModel type) {
		m_type = type;
		m_list = new ArrayList<PSSDataInstProcStmt>();
	}

	public void eval(PSSInst inst) {
		//PSSModel type_decl = findDeclaration(m_type);
		//if (type_decl == null) {
		//	PSSMessage.Error("DATADecl", "Cannot find type declaration " + m_type);
		//}

		// Declare Reference Inst
		for (int i=0; i<m_list.size(); i++) {
			PSSDataInstProcStmt item = m_list.get(i);
			PSSInst child_inst = m_type.declInst(item.m_id, false);
			if (item.m_expression != null) {
				PSSVal val = item.m_expression.eval(inst);
				child_inst.assign(val);
			}
			inst.addInst(child_inst);
		}
	}

	public void addInst(PSSDataInstProcStmt stmt) {
		m_list.add(stmt);
	}

	public void dump(String indent) {
		for (int i=0; i<m_list.size(); i++) {
			PSSDataInstProcStmt item = m_list.get(i);
			System.out.println(indent + m_type + " " + item.getText());
		}
	}
}
