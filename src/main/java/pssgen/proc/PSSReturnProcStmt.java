package pssgen.proc;

import pssgen.expr.PSSExpression;
import pssgen.inst.PSSFunctionInst;
import pssgen.inst.PSSInst;
import pssgen.value.PSSVal;

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
        PSSVal res = m_expression == null ? null : PSSExpression.expecting(m_expression, result(inst)).eval(inst);
        throw new PSSReturnException(res);
    }

    /**
     * PSS 2.0 Section 9.4.3: the return type of a function determines the expected type
     * of the expression in its return statement. Returns the result instance of the
     * function invocation that inst is in, or null.
     */
    private static PSSInst result(PSSInst inst) {
        for (PSSInst i = inst; i != null; i = i.getParent()) {
            if (i instanceof PSSFunctionInst)
                return ((PSSFunctionInst) i).getResultInst();
        }
        return null;
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