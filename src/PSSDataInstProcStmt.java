
public class PSSDataInstProcStmt extends PSSProcStmt {

    String m_id = null;
    PSSExpression m_dim = null;
    PSSExpression m_init_exp = null;

    PSSDataInstProcStmt(String id, PSSExpression dim, PSSExpression expression) {
        super();
        m_id = id;
        m_dim = dim;
        m_init_exp = expression;
    }

    public PSSInst declProcDataInst(PSSInst parent_inst, PSSModel type, boolean rand) {
        PSSInst data_inst = null;
        if (m_dim == null) {
            data_inst = type.declInst(m_id, rand);
        } else {
            PSSVal dim_val = m_dim.eval(parent_inst);
            data_inst = PSSArrayInst.ofElementDataType(m_id, type, dim_val.toInt(), rand);
        }

        if (data_inst == null)
            PSSMessage.Error("PSSDataInstProcStmt",
                    "Fail to declare the instance \"" + m_id + "\".");

        return data_inst;
    }

    @Override
    public String getText() {
        String result = m_id;
        if (m_dim != null) {
            result = result.concat("[" + m_dim.getText() + "]");
        }
        if (m_init_exp != null) {
            result = result.concat(" = " + m_init_exp.getText());
        }
        return result;
    }

    @Override
    public void dump(String indent) {
        System.out.println(indent + "PSSDataInstProcStmt");
    }

}
