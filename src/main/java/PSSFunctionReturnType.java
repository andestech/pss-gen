
/**
 * A function return type may be void ({@link #VOID}) or a data type
 * (constructed by {@link #valueOf(PSSModel)}).
 */
public class PSSFunctionReturnType {

    /** the data type held by this return type */
    PSSModel m_data_type = null;

    /** the void return type */
    public static final PSSFunctionReturnType VOID = new PSSFunctionReturnType(null);

    private PSSFunctionReturnType(PSSModel data_type) {
        m_data_type = data_type;
    }

    /**
     * Returns {@code true} if this return type is void.
     */
    public boolean isVoid() {
        return m_data_type == null;
    }

    /**
     * Returns the data type held by this return type, or {@code null} if this
     * return type is {@link #VOID}.
     *
     * @return the data type held by this return type, or {@code null} if this
     *         return type is {@link #VOID}
     */
    public PSSModel getDataType() {
        return m_data_type;
    }

    /**
     * Constructs a return type that holds a data type.
     *
     * @param data_type a data type
     * @return a return type that holds data_type
     */
    public static PSSFunctionReturnType valueOf(PSSModel data_type) {
        if (data_type == null)
            throw new IllegalArgumentException("A data type as a function return type must be non-null.");
        return new PSSFunctionReturnType(data_type);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PSSFunctionReturnType) {
            PSSFunctionReturnType r = (PSSFunctionReturnType) obj;
            if (m_data_type == null && r.m_data_type == null)
                return true;
            else if (m_data_type == null || r.m_data_type == null)
                return false;
            else
                return m_data_type.equals(r.m_data_type);
        }
        return false;
    }

    @Override
    public String toString() {
        return m_data_type == null ? "void" : m_data_type.getText();
    }

}