
public class PSSReturnProcStmt extends PSSProcStmt {

    PSSExpression m_expression = null;

    public PSSReturnProcStmt() {
    }

    public PSSReturnProcStmt(PSSExpression exp) {
        m_expression = exp;
    }

    public PSSExpression getExpression() {
        return m_expression;
    }

    @Override
    public void eval(PSSInst inst) throws PSSReturnException {
        PSSVal res = m_expression == null ? null : m_expression.eval(inst);
        throw new PSSReturnException(res);
    }

    @Override
    public String getText() {
        return "return" + (m_expression == null ? "" : " " + m_expression.getText());
    }

    @Override
    public void dump(String indent) {
        System.out.println(indent + getText());
    }

    @Override
    public String toString() {
        return getText();
    }

}