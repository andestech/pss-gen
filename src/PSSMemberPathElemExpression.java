import java.util.*;

/**
 * A {@code PSSMemberPathElemExpression} contains an identifier, an optional
 * list of function parameters, and an optional index.
 */
public class PSSMemberPathElemExpression extends PSSExpression {

    /** the identifier */
    String m_id;

    /** the optional list of function parameters */
    List<PSSExpression> m_function_parameter_list = null;

    /** the optional index */
    PSSExpression m_index = null;

    /**
     * Constructs this object with a specified identifier.
     *
     * @param id the identifier of this object
     */
    public PSSMemberPathElemExpression(String id) {
        m_id = id;
    }

    /**
     * Constructs this object.
     *
     * @param id   the identifier of this object
     * @param args the list of function parameters of this object
     * @param idx  the index of this object
     */
    public PSSMemberPathElemExpression(String id, List<PSSExpression> args, PSSExpression idx) {
        m_id = id;
        m_function_parameter_list = args;
        m_index = idx;
    }

    private PSSVal evalMethod(PSSInst var, String name, List<PSSExpression> args) throws NoSuchMethodException {
        /* Evaluate arguments */
        List<PSSVal> vals = args.stream().map(a -> a.eval(var)).toList();

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
            // m_id may refer to a user defined function

            // Find the component instance containing the definition of the function m_id.
            PSSInst ci = var.getComponentInst();
            PSSModel cm = ci.getTypeModel();

            // Find the function definition
            PSSModel m = cm.findDeclaration(m_id);

            PSSVal res = null;
            if (m instanceof PSSFunctionModel) {
                // Invoke the function
                PSSFunctionModel fm = (PSSFunctionModel) m;
                PSSFunctionInst fi = fm.declInst(ci, m_function_parameter_list.stream().map(p -> p.eval(var)).toList());
                res = fi.eval(var);
            } else {
                try {
                    res = evalMethod(var, m_id, m_function_parameter_list);
                } catch (NoSuchMethodException e) {
                    PSSMessage.Error("", "Function " + m_id + " is not defined.");
                }
            }
            if (res instanceof PSSRefVal)
                inst = ((PSSRefVal) res).getInst();
        }
        if (m_index != null && inst != null)
            inst = inst.indexOf(m_index.eval(var));
        return inst;
    }

    @Override
    public PSSVal eval(PSSInst var) {
        PSSVal res = null;
        if (m_function_parameter_list == null) {
            PSSInst inst = var.findInstance(m_id);
            res = inst.toVal();
        } else {
            // m_id may refer to a user defined function

            // Find the component instance containing the definition of the function m_id.
            PSSInst ci = var.getComponentInst();
            PSSModel cm = ci.getTypeModel();

            // Find the function definition
            PSSModel m = cm.findDeclaration(m_id);

            if (m instanceof PSSFunctionModel) {
                // Invoke the function
                PSSFunctionModel fm = (PSSFunctionModel) m;
                PSSFunctionInst fi = fm.declInst(ci, m_function_parameter_list.stream().map(p -> p.eval(var)).toList());
                res = fi.eval(var);
            } else {
                // m_id may refer to a builtin method associated to var
                try {
                    res = evalMethod(var, m_id, m_function_parameter_list);
                } catch (NoSuchMethodException e) {
                    PSSMessage.Error("", "Function " + m_id + " is not defined.");
                }
            }
        }
        if (m_index != null && res != null)
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
