import java.util.*;

/**
 * A {@code PSSMemberPathElemExpression} contains an identifier, an optional
 * list of function parameters, and an optional index. It may has a parent or a
 * child or both if it is used in a hierarchical reference path.
 */
public class PSSMemberPathElemExpression extends PSSExpression {

    /** the parent of this expression */
    PSSMemberPathElemExpression m_parent;

    /** the child of this expression */
    PSSMemberPathElemExpression m_child;

    /** the identifier */
    String m_id;

    /** the optional list of function parameters */
    List<PSSExpression> m_function_parameter_list;

    /** the optional index */
    PSSExpression m_index;

    /**
     * Constructs this expression.
     *
     * @param id   the identifier of this expression
     * @param args the optional list of function parameters of this expression
     * @param idx  the optional index of this expression
     */
    public PSSMemberPathElemExpression(String id, List<PSSExpression> args, PSSExpression idx) {
        m_id = id;
        m_function_parameter_list = args;
        m_index = idx;
    }

    /**
     * Constructs this expression with a specified identifier.
     *
     * @param id the identifier of this expression
     */
    public PSSMemberPathElemExpression(String id) {
        this(id, null, null);
    }

    /**
     * Returns the parent of this expression.
     *
     * @return the parent of this expression
     */
    public PSSMemberPathElemExpression getParent() {
        return m_parent;
    }

    /**
     * Sets the parent of this expression.
     *
     * @param p the parent of this expression
     */
    public void setParent(PSSMemberPathElemExpression p) {
        m_parent = p;
        if (p != null)
            p.setChild(this);
    }

    /**
     * Returns the child of this expression.
     *
     * @return the child of this expression
     */
    public PSSMemberPathElemExpression getChild() {
        return m_child;
    }

    private void setChild(PSSMemberPathElemExpression c) {
        m_child = c;
    }

    /**
     * Returns the upper hierarchical ID of this expression. The upper hierarchical
     * ID includes the IDs of all ancestors of this expression and the ID of this
     * expression.
     *
     * @return the upper hierarchical ID of this expression
     */
    public String getUpperHierarchicalID() {
        return m_parent == null ? m_id : (m_parent.getUpperHierarchicalID() + "." + m_id);
    }

    /**
     * Returns the lower hierarchical ID of this expression. The lower hierarchical
     * ID includes the ID of this expression and the IDs of all descendants of this
     * expression.
     *
     * @return the lower hierarchical ID of this expression
     */
    public String getLowerHierarchicalID() {
        return m_child == null ? m_id : (m_id + "." + m_child.getLowerHierarchicalID());
    }

    private PSSVal evalMethod(PSSInst var, String name, List<PSSExpression> args) throws NoSuchMethodException {
        /* Evaluate arguments */
        List<PSSVal> vals = args.stream().map(a -> a.eval(var)).toList();

        /* Invoke method */
        PSSVal res = var.evalMethod(name, vals);

        return res;
    }

    private PSSInst getInstOne(PSSInst var) {
        PSSInst inst = null;
        if (m_function_parameter_list == null) {
            inst = m_parent == null ? var.findInstance(m_id) : var.findInstanceUnder(m_id);
            if (inst == null)
                PSSMessage.Error("", getUpperHierarchicalID() + " is not defined.");
        } else {
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
                    PSSMessage.Error("", "Function " + getUpperHierarchicalID() + " is not defined.");
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
    public PSSInst getInst(PSSInst var) {
        PSSInst inst = getInstOne(var);

        if (m_child != null)
            inst = m_child.getInst(inst);

        return inst;
    }

    private PSSVal evalLast(PSSInst var) {
        PSSVal res = null;

        if (m_function_parameter_list == null) {
            PSSInst inst = m_parent == null ? var.findInstance(m_id) : var.findInstanceUnder(m_id);
            if (inst == null)
                PSSMessage.Error("", getUpperHierarchicalID() + " is not defined.");
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
                    PSSMessage.Error("", "Function " + getUpperHierarchicalID() + " is not defined.");
                }
            }
        }

        if (m_index != null && res != null)
            res = res.indexOf(m_index.eval(var));

        return res;
    }

    @Override
    public PSSVal eval(PSSInst var) {
        PSSVal res = null;

        if (m_child != null) {
            PSSInst inst = getInstOne(var);
            res = m_child.eval(inst);
        } else
            res = evalLast(var);

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
        if (m_child != null)
            sb.append("." + m_child.getText());

        return sb.toString();
    }

    @Override
    public String toString() {
        return getText();
    }

}
