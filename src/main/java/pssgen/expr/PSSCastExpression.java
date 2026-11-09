package pssgen.expr;

import java.util.ArrayList;

import pssgen.inst.PSSInst;
import pssgen.inst.PSSIntInst;
import pssgen.model.PSSModel;
import pssgen.util.PSSMessage;
import pssgen.value.PSSBoolVal;
import pssgen.value.PSSIntVal;
import pssgen.value.PSSVal;

public class PSSCastExpression extends PSSExpression {

    PSSModel      m_lhs_casting_type;
    PSSExpression m_rhs_expression;

    public PSSCastExpression (PSSModel lhs, PSSExpression rhs) {
        m_lhs_casting_type = lhs;
        m_rhs_expression   = rhs;
    }

    /** A cast is random when its operand is. */
    @Override
    public boolean isRandomable (PSSInst var) {
        return m_rhs_expression.isRandomable(var);
    }

    /** The fields of the operand, so that the solver draws them for the constraint. */
    @Override
    public ArrayList<PSSInst> getInsts (PSSInst var) {
        return m_rhs_expression.getInsts(var);
    }

    @Override
    public PSSVal eval (PSSInst var) {
        if (m_rhs_expression instanceof PSSPrimaryExpression) {
            return convert(m_rhs_expression.eval(var));
        }
        ArrayList<PSSInst> insts = m_rhs_expression.getInsts(var);
        if (insts.size() > 0) {
            PSSInst rhs_inst = insts.get(0);
            if (!(rhs_inst instanceof PSSIntInst)) {
                PSSMessage.Fatal("Casting < " + rhs_inst.getClass().getSimpleName() + " > is not yet support.");
            }
            return convert(m_rhs_expression.eval(var));
        }
        PSSMessage.Fatal("Casting < " + m_rhs_expression.getClass().getSimpleName() + " > ( " + m_rhs_expression.getText() + " ) is not yet support.");
        return null;
    }

    /**
     * PSS 2.0 Section 8.11.1: the value of the operand represented as the casting type.
     * The conversion works on a copy, so the operand (a field, a literal) keeps its
     * own value and type.
     */
    private PSSVal convert (PSSVal val) {
        PSSVal res = val;
        if (val instanceof PSSIntVal) {
            res = new PSSIntVal(val.toBigInteger(), val.getTypeModel());
        } else if (val instanceof PSSBoolVal) {
            res = PSSBoolVal.literal(String.valueOf(val.toBool()));
        }
        res.setTypeModel(m_lhs_casting_type);
        return res;
    }

    @Override
    public String getText() {
        return "(" + m_lhs_casting_type.getText() + ")" + m_rhs_expression.getText();
    }
}
