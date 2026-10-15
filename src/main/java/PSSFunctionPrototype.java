
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * This is a class for function prototypes.
 */
public class PSSFunctionPrototype {

    /** the function return type */
    PSSFunctionReturnType m_return_type;

    /** the function name */
    String m_id;

    /** the function parameters */
    List<PSSFunctionParameter> m_params;

    /**
     * Constructs a function prototype with a return type, a function name, and a
     * list of function parameters.
     *
     * @param return_type the function return type
     * @param id          the function name
     * @param params      the function parameters
     */
    public PSSFunctionPrototype(PSSFunctionReturnType return_type, String id, List<PSSFunctionParameter> params) {
        m_return_type = return_type;
        m_id = id;
        m_params = params;

        // Check PSS 2.0 Section 22.2.3 (b)
        if (params != null) {
            boolean has_default = false;
            for (PSSFunctionParameter param : params) {
                if (param.getDefaultValue() != null)
                    has_default = true;
                else if (has_default)
                    PSSMessage.Error("PSS 2.0 Section 22.2.3 (b)",
                            "In a function declaration, following a parameter with a specified default value, all subsequent parameters must also have default values specified.");
            }
        }
    }

    /**
     * Constructs a function prototype with a function name and a list of function
     * parameters. The function return type is {@link PSSReturnType#VOID}.
     *
     * @param id
     * @param params
     */
    public PSSFunctionPrototype(String id, List<PSSFunctionParameter> params) {
        this(PSSFunctionReturnType.VOID, id, params);
    }

    /**
     * Constructs a function prototype with a return type and a function name. The
     * function parameters is empty.
     *
     * @param return_type the function return type
     * @param id          the function name
     */
    public PSSFunctionPrototype(PSSFunctionReturnType return_type, String id) {
        this(return_type, id, new ArrayList<PSSFunctionParameter>());
    }

    /**
     * Constructs a function prototype with a function name. The function return
     * type is {@link PSSReturnType#VOID}. The function parameters is empty.
     *
     * @param id the function name
     */
    public PSSFunctionPrototype(String id) {
        this(PSSFunctionReturnType.VOID, id, new ArrayList<PSSFunctionParameter>());
    }

    /**
     * Returns the function return type.
     *
     * @return the function return type
     */
    public PSSFunctionReturnType getReturnType() {
        return m_return_type;
    }

    /**
     * Returns the function name.
     *
     * @return the function name
     */
    public String getID() {
        return m_id;
    }

    /**
     * Shadows a function of this prototype using a redeclared function of a
     * specified prototype.
     *
     * @param p the prototype of the redeclared function
     */
    public void redeclare(PSSFunctionPrototype p) {
        if (!getReturnType().equals(p.getReturnType())) {
            PSSMessage.Error("PSS 2.0 Section 22.2",
                    "The function declaration in the derived component must have the same return type as that in the base component.");
        }
        if (getParameters().size() != p.getParameters().size()) {
            PSSMessage.Error("PSS 2.0 Section 22.2",
                    "The function declaration in the derived component must have the same arguments as that in the base component.");
        }
        for (int i = 0; i < getParameters().size(); i++) {
            PSSFunctionParameter param = getParameters().get(i);
            PSSFunctionParameter param_re = p.getParameters().get(i);
            if (!param.getDataType().equals(param_re.getDataType())) {
                PSSMessage.Error("PSS 2.0 Section 22.2",
                        "The function declaration in the derived component must have the same arguments as that in the base component.");
            }
            if (param_re.getDefaultValue() != null) {
                PSSMessage.Error("PSS 2.0 Section 22.2.3 (c)",
                        "A default parameter value shall not be specified in the redeclaration of a function if already declared for the same parameter in a previous declaration.");
            }
        }
    }

    /**
     * Returns the function parameters.
     *
     * @return the function parameters
     */
    public List<PSSFunctionParameter> getParameters() {
        return m_params;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PSSFunctionPrototype) {
            PSSFunctionPrototype p = (PSSFunctionPrototype) obj;
            return m_return_type.equals(p.m_return_type) && m_id.equals(p.m_id)
                    && m_params.equals(p.m_params);
        }
        return false;
    }

    @Override
    public String toString() {
        return m_return_type.toString() + " " + m_id + "("
                + String.join(", ", m_params.stream().map(p -> p.toString()).collect(Collectors.toList())) + ")";
    }

}
