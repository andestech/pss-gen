
/**
 * This is a class for function parameters. The identity (name) of a function
 * parameter is always required. A function parameter may be one of the
 * followings.
 * <ul>
 * <li>Plain data type parameter, which contains an optional direction, a plain
 * data type, and an optional default value</li>
 * <li>Generic type parameter</li>
 * <li>Reference type parameter, which contains a type category</li>
 * <li>Generic struct parameter</li>
 * </ul>
 * Each of the four kinds of parameters can be a variadic parameter. For a plain
 * data type parameter that is also a variadic parameter, the direction and the
 * default value cannot be specified.
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

    /** {@code true} if this argument is a generic type */
    boolean m_type;

    /** a reference to a type category */
    PSSTypeCategory m_type_category;

    /** {@code true} if this argument is a generic struct */
    boolean m_struct;

    private PSSFunctionParameter(PSSFunctionParameterDir dir, PSSModel data_type, String id, PSSExpression def,
            boolean varargs, boolean type, PSSTypeCategory type_category, boolean struct) {
        m_dir = dir;
        m_data_type = data_type;
        m_id = id;
        m_default_value = def;
        m_varargs = varargs;
        m_type = type;
        m_type_category = type_category;
        m_struct = struct;
    }

    /**
     * Constructs a plain data type parameter.
     *
     * @param dir       an optional direction
     * @param data_type the data type of the parameter
     * @param id        the name of the parameter
     * @param def       the optional default value of the parameter
     * @return the parameter
     */
    public static PSSFunctionParameter newPlainDataTypeParameter(PSSFunctionParameterDir dir, PSSModel data_type,
            String id,
            PSSExpression def) {
        return new PSSFunctionParameter(dir, data_type, id, def, false, false, null, false);
    }

    /**
     * Constructs a plain data type parameter without direction.
     *
     * @param data_type the data type of the parameter
     * @param id        the name of the parameter
     * @param def       the optional default value of the parameter
     * @return the parameter
     */
    public static PSSFunctionParameter newPlainDataTypeParameter(PSSModel data_type, String id,
            PSSExpression def) {
        return new PSSFunctionParameter(null, data_type, id, def, false, false, null, false);
    }

    /**
     * Constructs a plain data type parameter without direction and default value.
     *
     * @param data_type the data type of the parameter
     * @param id        the name of the parameter
     * @return the parameter
     */
    public static PSSFunctionParameter newPlainDataTypeParameter(PSSModel data_type, String id) {
        return new PSSFunctionParameter(null, data_type, id, null, false, false, null, false);
    }

    /**
     * Constructs a generic type parameter.
     *
     * @param id the name of the parameter
     * @return the parameter
     */
    public static PSSFunctionParameter newTypeParameter(String id) {
        return new PSSFunctionParameter(null, null, id, null, false, true, null, false);
    }

    /**
     * Constructs a reference parameter.
     *
     * @param type_category the target type of the parameter
     * @param id            the name of the parameter
     * @return the parameter
     */
    public static PSSFunctionParameter newRefParameter(PSSTypeCategory type_category, String id) {
        if (PSSTypeCategory.STRUCT.equals(type_category))
            PSSMessage.Error("PSS 2.0 Section 22.2.4",
                    "In the case of the struct category, the ref modifier shall not be used.");
        return new PSSFunctionParameter(null, null, id, null, false, false, type_category, false);
    }

    /**
     * Constructs a generic struct parameter.
     *
     * @param id the name of the parameter
     * @return the parameter
     */
    public static PSSFunctionParameter newStructParameter(String id) {
        return new PSSFunctionParameter(null, null, id, null, false, false, null, true);
    }

    /**
     * Constructs a data type variadic parameter without direction and default
     * value.
     *
     * @param data_type the data type of the parameter
     * @param id        the name of the parameter
     * @return the parameter
     */
    public static PSSFunctionParameter newVariadicDataTypeParameter(PSSModel data_type, String id) {
        return new PSSFunctionParameter(null, data_type, id, null, true, false, null, false);
    }

    /**
     * Constructs a generic type variadic parameter.
     *
     * @param id the name of the parameter
     * @return the parameter
     */
    public static PSSFunctionParameter newVariadicTypeParameter(String id) {
        return new PSSFunctionParameter(null, null, id, null, true, true, null, false);
    }

    /**
     * Constructs a reference variadic parameter.
     *
     * @param type_category the target type of the parameter
     * @param id            the name of the parameter
     * @return the parameter
     */
    public static PSSFunctionParameter newVariadicRefParameter(PSSTypeCategory type_category, String id) {
        return new PSSFunctionParameter(null, null, id, null, true, false, type_category, false);
    }

    /**
     * Constructs a generic struct variadic parameter.
     *
     * @param id the name of the parameter
     * @return the parameter
     */
    public static PSSFunctionParameter newVariadicStructParameter(String id) {
        return new PSSFunctionParameter(null, null, id, null, true, false, null, true);
    }

    /**
     * Returns {@code true} if this parameter is a plain data type.
     *
     * @param {@code true} if this parameter is a plain data type
     */
    public boolean isPlainDataType() {
        return m_data_type != null;
    }

    /**
     * Returns {@code true} if this parameter is a generic type.
     *
     * @param {@code true} if this parameter is a generic type
     */
    public boolean isType() {
        return m_type;
    }

    /**
     * Returns {@code true} if this parameter is a reference type.
     *
     * @param {@code true} if this parameter is a reference type
     */
    public boolean isRef() {
        return m_type_category != null;
    }

    /**
     * Returns {@code true} if this parameter is a generic struct.
     *
     * @param {@code true} if this parameter is a generic struct
     */
    public boolean isStruct() {
        return m_struct;
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

    /**
     * Returns the type category of this parameter.
     *
     * @return the type category of this parameter
     */
    public PSSTypeCategory getTypeCategory() {
        return m_type_category;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PSSFunctionParameter) {
            PSSFunctionParameter p = (PSSFunctionParameter) obj;
            boolean b1 = isVarargs();
            boolean b2 = p.isVarargs();
            if (b1 ^ b2)
                return false;
            b1 = isType();
            b2 = p.isType();
            if (b1 && b2)
                return true;
            else if (b1 || b2)
                return false;
            b1 = isStruct();
            b2 = p.isStruct();
            if (b1 && b2)
                return true;
            else if (b1 || b2)
                return false;
            b1 = isRef();
            b2 = p.isRef();
            if (b1 && b2)
                return getTypeCategory().equals(p.getTypeCategory());
            else if (b1 || b2)
                return false;

            PSSFunctionParameterDir dir1 = getDirection();
            PSSFunctionParameterDir dir2 = p.getDirection();
            if (dir1 == null)
                return dir2 == null;
            else if (dir2 == null)
                return false;
            else if (!dir1.equals(dir2))
                return false;

            PSSModel dt1 = getDataType();
            PSSModel dt2 = p.getDataType();
            if (!dt1.equals(dt2))
                return false;

            if (!getID().equals(p.getID()))
                return false;

            PSSExpression def1 = getDefaultValue();
            PSSExpression def2 = p.getDefaultValue();
            if (def1 == null)
                return def2 == null;
            else if (def2 == null)
                return false;
            else
                return def1.equals(def2);
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (isPlainDataType()) {
            sb.append(m_dir == null ? "" : m_dir.toString() + " ");
            sb.append(m_data_type.getText());
            sb.append(isVarargs() ? " ... " : " ");
            sb.append(" ");
            sb.append(m_id);
            sb.append(m_default_value == null ? "" : " = " + m_default_value.getText());
        } else {
            if (isType())
                sb.append("type");
            else if (isRef())
                sb.append("ref " + getTypeCategory().toString());
            else
                sb.append("struct");
            sb.append(isVarargs() ? " ... " : " ");
            sb.append(getID());
        }
        return sb.toString();
    }

}