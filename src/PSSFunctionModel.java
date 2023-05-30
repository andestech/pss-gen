
import java.util.List;

/**
 * A {@code PSSFunctionModel} is a definition of a PSS native function.
 */
public class PSSFunctionModel extends PSSModel {

    /** the function availability (optional, see PSS 2.0 Section 22.3.1) */
    PSSPlatformQualifier m_platform_qualifier;

    /** indicates if this function is pure (see PSS 2.0 Section 22.2.5) */
    boolean m_pure;

    /** the prototype of this function */
    PSSFunctionPrototype m_prototype;

    /** the statements of this function */
    List<PSSProcStmt> m_stmts;

    /** {@code true} if this function is defined */
    boolean m_is_defined;

    /**
     * Constructs this function.
     *
     * @param qualifier the function availability (optional)
     * @param pure      {@code true} if this function is a pure
     * @param prototype the prototype of this function
     * @param stmts     the statements of this function, {@code null} if no
     *                  definition is available
     */
    public PSSFunctionModel(PSSPlatformQualifier qualifier, boolean pure, PSSFunctionPrototype prototype,
            List<PSSProcStmt> stmts) {
        super(prototype.getID());
        m_platform_qualifier = qualifier;
        m_pure = pure;
        m_prototype = prototype;
        m_stmts = stmts;
        m_is_defined = m_stmts != null;

        // Check if the function is pure
        if (pure) {
            // PSS 2.0 Section 22.2.5 (a)
            if (prototype.getParameters().size() == 0) {
                PSSMessage.Error("PSS 2.0 Section 22.2.5 (a)",
                        "Only non-void functions with no output or inout parameters may be declared pure.");
            } else {
                for (PSSFunctionParameter param : prototype.getParameters()) {
                    if (param.getDirection().equals(PSSFunctionParameterDir.output)
                            || param.getDirection().equals(PSSFunctionParameterDir.inout))
                        PSSMessage.Error("",
                                "Only non-void functions with no output or inout parameters may be declared pure.");
                }
            }
        }

        // Check PSS 2.0 Sectio 22.2.2
        if (isNative() || isTargetTemplate()) {
            for (PSSFunctionParameter param : prototype.getParameters()) {
                if (param.getDirection() != null)
                    PSSMessage.Error("PSS 2.0 Section 22.2.2",
                            "In the declaration of native functions and target-template functions, direction modifiers shall not be used.");
            }
        }
    }

    /**
     * Constructs this function.
     *
     * @param pure      {@code true} if this function is a pure
     * @param prototype the prototype of this function
     * @param stmts     the statements of this function, {@code null} if no
     *                  definition is available
     */
    public PSSFunctionModel(boolean pure, PSSFunctionPrototype prototype, List<PSSProcStmt> stmts) {
        this(null, pure, prototype, stmts);
    }

    /**
     * Constructs this function.
     *
     * @param pure      {@code true} if this function is a pure
     * @param prototype the prototype of this function
     */
    public PSSFunctionModel(boolean pure, PSSFunctionPrototype prototype) {
        this(null, pure, prototype, null);
    }

    @Override
    public PSSInst declInst(String id, boolean rand) {
        PSSMessage.Fatal(getClass().getSimpleName()
                + "::use declInst(PSSInst, List<PSSVal>) instead of declInst(String, boolean)");
        return null;
    }

    @Override
    public PSSInst declInst(boolean rand) {
        PSSMessage.Fatal(
                getClass().getSimpleName() + "::use declInst(PSSInst, List<PSSVal>) instead of declInst(boolean)");
        return null;
    }

    @Override
    public PSSInst declInst(PSSInst parent_inst, String id, boolean rand) {
        PSSMessage.Fatal(getClass().getSimpleName()
                + "::use declInst(PSSInst, List<PSSVal>) instead of declInst(PSSInst, String, boolean)");
        return null;
    }

    /**
     * Creates an instance of a function invocation.
     *
     * @param parent  the instance containing the definition of this function
     * @param actuals the actual parameters
     * @return the function invocation
     */
    public PSSFunctionInst declInst(PSSInst parent, List<PSSVal> actuals) {
        PSSFunctionInst fi = new PSSFunctionInst(this, actuals);
        parent.addInst(fi);
        return fi;
    }

    /**
     * Returns {@code true} if this function is pure.
     *
     * @return {@code true} if this function is pure
     */
    public boolean isPure() {
        return m_pure;
    }

    /**
     * Returns {@code true} if this function is native.
     *
     * @return {@code true} if this function is native
     */
    public boolean isNative() {
        PSSMessage.Warning(getClass().getSimpleName() + "::isNative is to be implemented");
        return true;
    }

    /**
     * Returns {@code true} if this function is a target template.
     *
     * @return {@code true} if this function is a target template
     */
    public boolean isTargetTemplate() {
        PSSMessage.Warning(getClass().getSimpleName() + "::isTargetTemplate is to be implemented");
        return false;
    }

    /**
     * Returns the prototype of this function.
     *
     * @return the prototype of this function
     */
    public PSSFunctionPrototype getPrototype() {
        return m_prototype;
    }

    /**
     * Returns the function body, which may be {@code null} if the function
     * definition is not specified.
     *
     * @return the function body, or {@code null} if the function definition is not
     *         specified
     */
    public List<PSSProcStmt> getStatements() {
        return m_stmts;
    }

    /**
     * Shadows this function using a specified function.
     *
     * @param m a function
     */
    public void redeclare(PSSFunctionModel m) {
        m_prototype.redeclare(m.m_prototype);

        // PSS 2.0 Section 22.2.5 (b)
        if (m_pure)
            m.m_pure = true;
        else if (m.m_pure) {
            PSSMessage.Error("PSS 2.0 Section 22.2.5",
                    "A non-pure function shall not be declared as pure in derived types.");
        }
    }

    /**
     * Defines this function.
     *
     * @param stmts the function body of this function
     */
    public void define(List<PSSProcStmt> stmts) {
        m_stmts = stmts;
        m_is_defined = m_stmts != null;
    }

    @Override
    public String getText() {
        return toString();
    }

    @Override
    public String toString() {
        return (m_pure ? "pure " : "") + "function " + m_prototype.toString() + " {"
                + String.join(";\n", m_stmts.stream().map(stmt -> stmt.getText()).toList()) + "}";
    }

}