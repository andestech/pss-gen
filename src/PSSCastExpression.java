import java.util.ArrayList;

public class PSSCastExpression extends PSSExpression {

    PSSModel      m_lhs_casting_type;
    PSSExpression m_rhs_expression;

    public PSSCastExpression (PSSModel lhs, PSSExpression rhs) {
        m_lhs_casting_type = lhs;
        m_rhs_expression   = rhs;
    }

    @Override
    public PSSVal eval (PSSInst var) {
        if (m_rhs_expression instanceof PSSPrimaryExpression) {
            PSSVal val = m_rhs_expression.eval(var);
            val.setTypeModel(m_lhs_casting_type);
            return val;
        }
        ArrayList<PSSInst> insts = m_rhs_expression.getInsts(var);
        if (insts.size() > 0) {
            PSSInst rhs_inst = insts.get(0);
            if (!(rhs_inst instanceof PSSIntInst)) {
                PSSMessage.Fatal("Casting < " + rhs_inst.getClass().getSimpleName() + " > is not yet support.");
            }
            ((PSSIntInst)rhs_inst).castType(m_lhs_casting_type);
            return m_rhs_expression.eval(var);
        }
        PSSMessage.Fatal("Casting < " + m_rhs_expression.getClass().getSimpleName() + " > ( " + m_rhs_expression.getText() + " ) is not yet support.");
        return null;
    }

    @Override
    public String getText() {
        return "(" + m_lhs_casting_type.getText() + ")" + m_rhs_expression.getText();
    }
}
