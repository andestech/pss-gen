
/**
 * This is a class for function parameters.
 */
public class PSSFunctionParameter {

    /** the direction of this parameter (optional) */
    PSSFunctionParameterDir m_dir;

    /** the data type of this parameter */
    PSSModel m_data_type;

    /** the name of this parameter */
    String m_id;

    /** the default value of this parameter (optional) */
    PSSExpression m_default_value;

    /** a varargs flag */
    boolean m_varargs;

    /**
     * Constructs this parameter.
     *
     * @param dir       the direction of this parameter (optional)
     * @param data_type the data type of this parameter
     * @param id        the name of the parameter
     * @param def       the default value of this parameter (optional)
     */
    public PSSFunctionParameter(PSSFunctionParameterDir dir, PSSModel data_type, String id, PSSExpression def) {
        m_dir = dir;
        m_data_type = data_type;
        m_id = id;
        m_default_value = def;
        m_varargs = false;
    }

    /**
     * Constructs a parameter with a data type, a name, and a default value.
     *
     * @param data_type data type of the parameter
     * @param id        name of the parameter
     * @param def       default value of the parameter (optional)
     */
    public PSSFunctionParameter(PSSModel data_type, String id, PSSExpression def) {
        this(null, data_type, id, def);
    }

    /**
     * Constructs a parameter with a data type and a name.
     *
     * @param data_type data type of the parameter
     * @param id        name of the parameter
     */
    public PSSFunctionParameter(PSSModel data_type, String id) {
        this(null, data_type, id, null);
    }

    /**
     * Constructs a parameter with a data type and a name.
     *
     * @param data_type data type of the parameter
     * @param id        name of the parameter
     * @param varargs   {@code true} if this parameter is a varargs parameter
     */
    public PSSFunctionParameter(PSSModel data_type, String id, boolean varargs) {
        this(null, data_type, id, null);
        m_varargs = true;
    }

    /**
     * Returns {@code true} if this parameter is varargs.
     *
     * @return {@code true} if this parameter is varargs
     */
    public boolean isVarargs() {
        return m_varargs;
    }

    /**
     * Returns {@code true} if this parameter is specified as an input.
     *
     * @return {@code true} if this parameter is specified as an input
     */
    public boolean isInput() {
        return PSSFunctionParameterDir.input.equals(m_dir);
    }

    /**
     * Returns {@code true} if this parameter is specified as an output.
     *
     * @return {@code true} if this parameter is specified as an output
     */
    public boolean isOutput() {
        return PSSFunctionParameterDir.output.equals(m_dir);
    }

    /**
     * Returns {@code true} if this parameter is specified as an inout.
     *
     * @return {@code true} if this parameter is specified as an inout
     */
    public boolean isInout() {
        return PSSFunctionParameterDir.inout.equals(m_dir);
    }

    /**
     * Returns the data type of this parameter.
     *
     * @return the parameter data type
     */
    public PSSModel getDataType() {
        return m_data_type;
    }

    /**
     * Returns the direction of this parameter.
     *
     * @return the direction of this parameter, or {@code null} if the direction is
     *         not specified
     */
    public PSSFunctionParameterDir getDirection() {
        return m_dir;
    }

    /**
     * Returns the name of this parameter.
     *
     * @return the parameter name
     */
    public String getID() {
        return m_id;
    }

    /**
     * Returns the default value of this parameter.
     *
     * @return the default value of this parameter, or {@code null} if no default
     *         value is specified
     */
    public PSSExpression getDefaultValue() {
        return m_default_value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PSSFunctionParameter) {
            PSSFunctionParameter p = (PSSFunctionParameter) obj;
            if (m_dir == null)
                return p.m_dir == null;
            else if (p.m_dir == null)
                return false;
            else if (!m_dir.equals(p.m_dir))
                return false;
            if (!m_data_type.equals(p.m_data_type))
                return false;
            if (!m_id.equals(p.m_id))
                return false;
            if (m_default_value == null)
                return p.m_default_value == null;
            else if (p.m_default_value == null)
                return false;
            else
                return m_default_value.equals(p.m_default_value);
        }
        return false;
    }

    @Override
    public String toString() {
        return (m_dir == null ? "" : m_dir.toString() + " ") + m_data_type.getText() + (m_varargs ? " ... " : " ")
                + m_id
                + (m_default_value == null ? "" : " = " + m_default_value.getText());
    }

}