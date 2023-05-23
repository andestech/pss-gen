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

    private PSSVal evalMethod(PSSInst ctx, PSSInst var, String name, List<PSSExpression> args)
            throws NoSuchMethodException {
        /* Evaluate arguments */
        List<PSSVal> vals = args.stream().map(a -> a.eval(ctx)).toList();

        /* Invoke method */
        PSSVal res = var.evalMethod(name, vals);

        return res;
    }

    /**
     * Resolve m_id.
     *
     * @param ctx    the context of the whole hierarchical reference path
     * @param parent the resolved instance of the partial hierarchical reference
     *               path down to the parent of this element
     * @return the resolved instance of the partial hierarchical reference path down
     *         to this element
     */
    private PSSInst getInstOne(PSSInst ctx, PSSInst parent) {
        PSSInst inst = null;
        if (m_function_parameter_list == null) {
            inst = m_parent == null ? parent.findInstance(m_id) : parent.findInstanceUnder(m_id);
            if (inst == null)
                PSSMessage.Error("", getUpperHierarchicalID() + " is not defined.");
        } else {
            // m_id may refer to a user defined function

            // Find the component instance containing the definition of the function m_id.
            PSSInst ci = parent.getComponentInst();
            PSSModel cm = ci.getTypeModel();

            // Find the function definition
            PSSModel m = cm.findDeclaration(m_id);

            PSSVal res = null;
            if (m instanceof PSSFunctionModel) {
                // Invoke the function
                PSSFunctionModel fm = (PSSFunctionModel) m;
                PSSFunctionInst fi = fm.declInst(ci, m_function_parameter_list.stream().map(p -> p.eval(ctx)).toList());
                res = fi.eval(parent);
            } else {
                try {
                    res = evalMethod(ctx, parent, m_id, m_function_parameter_list);
                } catch (NoSuchMethodException e) {
                    PSSMessage.Error("", "Function " + getUpperHierarchicalID() + " is not defined.");
                }
            }
            if (res instanceof PSSRefVal)
                inst = ((PSSRefVal) res).getInst();
        }

        if (m_index != null && inst != null)
            inst = inst.indexOf(m_index.eval(ctx));

        return inst;
    }

    /**
     * Resolve the whole hierarchical reference path.
     *
     * @param ctx    the context of the whole hierarchical reference path
     * @param parent the resolved instance of the partial hierarchical reference
     *               path down to the parent of this element
     * @return the resolved instance of the whole hierarchical reference path
     */
    private PSSInst getInst(PSSInst ctx, PSSInst parent) {
        // resolve m_id
        PSSInst inst = getInstOne(ctx, parent);

        // resolve descendants
        if (m_child != null)
            inst = m_child.getInst(ctx, inst);

        return inst;
    }

    @Override
    public PSSInst getInst(PSSInst var) {
        if (m_parent != null)
            PSSMessage.Fatal(getClass().getName()
                    + "::Cannot get the instance of a partial member path reference using getInst(PSSInst).");
        return getInst(var, var);
    }

    /**
     * Do the final evaluation to PSSVal. Assuming this element is the last one
     * of the whole hierarchical reference path.
     *
     * @param ctx    the context of the whole hierarchical reference path
     * @param parent the resolved instance of the partial hierarchical path down to
     *               the parent of this element
     * @return the evaluation result
     */
    private PSSVal evalLast(PSSInst ctx, PSSInst parent) {
        PSSVal res = null;

        if (m_function_parameter_list == null) {
            PSSInst inst = m_parent == null ? parent.findInstance(m_id) : parent.findInstanceUnder(m_id);
            if (inst == null)
                PSSMessage.Error("", getUpperHierarchicalID() + " is not defined.");
            res = inst.toVal();
        } else {
            // m_id may refer to a user defined function

            // Find the component instance containing the definition of the function m_id.
            PSSInst ci = parent.getComponentInst();
            PSSModel cm = ci.getTypeModel();

            // Find the function definition
            PSSModel m = cm.findDeclaration(m_id);

            if (m instanceof PSSFunctionModel) {
                // Invoke the function
                PSSFunctionModel fm = (PSSFunctionModel) m;
                PSSFunctionInst fi = fm.declInst(ci, m_function_parameter_list.stream().map(p -> p.eval(ctx)).toList());
                res = fi.eval(parent);
            } else {
                // m_id may refer to a builtin method associated to var
                try {
                    res = evalMethod(ctx, parent, m_id, m_function_parameter_list);
                } catch (NoSuchMethodException e) {
                    PSSMessage.Error("", "Function " + getUpperHierarchicalID() + " is not defined.");
                }
            }
        }

        if (m_index != null && res != null)
            res = res.indexOf(m_index.eval(ctx));

        return res;
    }

    /**
     * Evaluate the whole hierarchical reference path.
     *
     * @param ctx    the context of the whole hierarchical reference path
     * @param parent the resolved instance of the partial hierarchical path down to
     *               the parent of this element
     * @return the evaluation result
     */
    private PSSVal eval(PSSInst ctx, PSSInst parent) {
        PSSVal res = null;

        if (m_child != null) {
            PSSInst inst = getInstOne(ctx, parent);
            res = m_child.eval(ctx, inst);
        } else
            res = evalLast(ctx, parent);

        return res;
    }

    @Override
    public PSSVal eval(PSSInst var) {
        if (m_parent != null)
            PSSMessage.Error("", "Cannot evaluate a partial member path reference: " + getLowerHierarchicalID());
        return eval(var, var);
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
