import java.util.ArrayList;

public class PSSDataDeclProcStmt extends PSSProcStmt {

	PSSModel m_type;
	ArrayList<PSSDataInstProcStmt> m_list;

	public PSSDataDeclProcStmt(PSSModel type) {
		m_type = type;
		m_list = new ArrayList<PSSDataInstProcStmt>();
	}

    @Override
	public void eval(PSSInst inst) {
		for (int i = 0; i < m_list.size(); i++) {
			PSSDataInstProcStmt item = m_list.get(i);

			// Check if item.m_id is already declared in the same scope.
			if (inst.findInstance(item.m_id, true) != null) {
				PSSMessage.Error("PSSDataDeclProcStmt", item.m_id + " is redeclared in the same scope.");
			}

            // Declare Procedural Data Inst
            PSSInst child_inst = item.declProcDataInst(inst, m_type, false);
            if (item.m_init_exp != null) {
                PSSVal init_val = item.m_init_exp.eval(inst);
                child_inst.assign(init_val);
            }
            inst.addInst(child_inst);
		}
	}

	public void addInst(PSSDataInstProcStmt stmt) {
		m_list.add(stmt);
	}

    @Override
    public void dump(String indent) {
        for (int i = 0; i < m_list.size(); i++) {
            PSSDataInstProcStmt item = m_list.get(i);
            System.out.println(indent + m_type + " " + item.getText());
        }
    }

}
