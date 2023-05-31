
import java.util.List;
import java.util.ArrayList;

/**
 * A {@code PSSFunctionInst} is an invocation of a function.
 */
public class PSSFunctionInst extends PSSInst {

    /** the actual parameters */
    List<PSSVal> m_actuals;

    /** the formal parameters */
    List<PSSInst> m_formals;

    /** the returned value */
    PSSInst m_res;

    private static PSSModel resolve(PSSModel m) {
        // A struct or an enum may be a PSSDataTypeModel
        if (m instanceof PSSDataTypeModel)
            m = ((PSSDataTypeModel) m).resolve();

        if (m instanceof PSSIAggregate)
            m = new PSSRefModel(m);

        return m;
    }

    /**
     * Constructs a function invocation.
     *
     * @param m       the function definition
     * @param actuals the actual parameters
     */
    public PSSFunctionInst(PSSFunctionModel m, List<PSSVal> actuals) {
        super(m.getPrototype().getID(), m.getPrototype().toString(), m, false);
        List<PSSFunctionParameter> params = m.getPrototype().getParameters();
        m_actuals = actuals;
        m_formals = new ArrayList<PSSInst>();
        for (int i = 0; i < params.size(); i++) {
            PSSFunctionParameter param = params.get(i);
            PSSVal actual = i < m_actuals.size() ? m_actuals.get(i) : null;

            // PSS 2.0 Section 22.3.3
            // Parameters of aggregate data types are passed as a handle to the instance in
            // the caller. Updates to these parameters in the callee will modify the
            // instances in the caller.
            if (param.isPlainDataType()) {
                PSSModel dt = resolve(param.getDataType());
                m_formals.add(dt.declInst(this, param.getID(), false));
            } else if (param.isRef()) {
                PSSTypeCategory t = param.getTypeCategory();
                PSSInst pi = new PSSRefModel(t).declInst(this, param.getID(), false);
                m_formals.add(pi);
            } else if (param.isType()) {
                if (actual == null)
                    PSSMessage.Error("PSSFunctionInst", "A value is required for the parameter " + param.getID());
                PSSInst pi = actual.getTypeModel().declInst(this, param.getID(), false);
                m_formals.add(pi);
            } else if (param.isStruct()) {
                if (actual == null)
                    PSSMessage.Error("PSSFunctionInst", "A value is required for the parameter " + param.getID());
                PSSInst pi = new PSSRefModel(PSSTypeCategory.STRUCT).declInst(this, param.getID(), false);
                m_formals.add(pi);
            } else {
                PSSMessage.Fatal("Unsupported type of function parameter: " + param.toString());
            }
        }
        PSSFunctionReturnType rt = m.getPrototype().getReturnType();
        if (rt.isVoid())
            m_res = null;
        else {
            PSSModel dr = resolve(rt.getDataType());
            m_res = dr.declInst("<" + m.getPrototype().getID() + ":return>", false);
        }
    }

    /**
     * Evaluates this function invocation.
     *
     * @param var the instance where this function invocation occurs
     * @return the returned value
     */
    public PSSInst eval(PSSInst var) {
        PSSFunctionModel m = (PSSFunctionModel) super.getTypeModel();
        List<PSSFunctionParameter> params = m.getPrototype().getParameters();
        List<PSSProcStmt> stmts = m.getStatements();

        if (m.getStatements() == null)
            PSSMessage.Fatal("Invocation of an external function is not implemented yet");

        if (params.size() > 0 && params.get(params.size() - 1).isVarargs())
            PSSMessage.Error("PSS 2.0 Section 22.2.4",
                    "PSS does not provide a native mechanism to operate on an unspecified number of parameters or on parameters with no declared type, nor does PSS define mapping of functions with generic/varargs parameters to foreign languages.");

        if (m_actuals.size() > params.size())
            PSSMessage.Error("", "Too many actual parameters passed to function " + m.getPrototype().getID());

        // Assign actual parameters
        for (int i = 0; i < m_actuals.size(); i++) {
            m_formals.get(i).assign(m_actuals.get(i));
        }

        for (int i = m_actuals.size(); i < m_formals.size(); i++) {
            PSSExpression def = params.get(i).getDefaultValue();
            if (def == null)
                PSSMessage.Error("", "Formal parameter " + params.get(i).getID() + " is not initialized.");
            m_formals.get(i).assign(def.eval(var));
        }

        // Evaluate function body
        try {
            for (PSSProcStmt stmt : stmts) {
                stmt.eval(this);
            }
        } catch (PSSReturnException e) {
            if (m_res != null)
                m_res.assign(e.getReturnValue());
        }

        return m_res;
    }

    @Override
    public String toString() {
        return super.getTypeModel().getText();
    }

}