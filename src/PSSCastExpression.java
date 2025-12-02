
public class PSSCastExpression extends PSSExpression {

    PSSModel      m_lhs_casting_type;
    PSSExpression m_rhs_expression;

    public PSSCastExpression (PSSModel lhs, PSSExpression rhs) {
        m_lhs_casting_type = lhs;
        m_rhs_expression   = rhs;
    }

    @Override
    public PSSVal eval (PSSInst var) {
        PSSVal rhs_val = m_rhs_expression.eval(var);
        rhs_val.setTypeModel(m_lhs_casting_type);
        return rhs_val;
    }

    @Override
    public String getText() {
        return "(" + m_lhs_casting_type.getText() + ")" + m_rhs_expression.getText();
    }
}
