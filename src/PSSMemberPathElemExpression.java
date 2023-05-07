import java.util.*;

public class PSSMemberPathElemExpression extends PSSExpression {

    String m_id;
    List<PSSExpression> m_function_parameter_list = null;
    PSSExpression m_index = null;

    public PSSMemberPathElemExpression(String id) {
        m_id = id;
    }

    public PSSMemberPathElemExpression(String id, List<PSSExpression> args, PSSExpression idx) {
        m_id = id;
        m_function_parameter_list = args;
        m_index = idx;
    }

    // TODO: how to improve this method? Function pointer? getMethod.invoke()?
    private PSSVal evalMethod(PSSInst var, String name, List<PSSExpression> args) {
        /* Evaluate arguments */
        List<PSSVal> vals = new ArrayList<PSSVal>();
        for (PSSExpression arg : args) {
            vals.add(arg.eval(var));
        }

        /* Invoke method */
        PSSVal res = var.evalMethod(name, vals);
        return res;
    }

    @Override
    public PSSInst getInst(PSSInst var) {
        PSSInst inst = null;
        if (m_function_parameter_list == null)
            inst = var.findInstance(m_id);
        else {
            PSSVal val = evalMethod(var, m_id, m_function_parameter_list);
            if (val instanceof PSSRefVal)
                inst = ((PSSRefVal) val).getInst();
        }
        if (m_index != null)
            inst = inst.indexOf(m_index.eval(var));
        return inst;
    }

    @Override
    public PSSVal eval(PSSInst var) {
        PSSVal res = null;
        if (m_function_parameter_list == null) {
            PSSInst inst = var.findInstance(m_id);
            res = inst.toVal();
        } else
            res = evalMethod(var, m_id, m_function_parameter_list);
        if (m_index != null)
            res = res.indexOf(m_index.eval(var));
        return res;
    }

    @Override
    public String getText() {
        StringBuffer sb = new StringBuffer();
        sb.append(m_id);
        if (m_function_parameter_list != null) {
            sb.append("(");
            List<String> strs = new ArrayList<String>();
            for (PSSExpression e : m_function_parameter_list) {
                strs.add(e.getText());
            }
            sb.append(String.join(", ", strs));
            sb.append(")");
        }
        if (m_index != null) {
            sb.append("[" + m_index.getText() + "]");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return getText();
    }

}
