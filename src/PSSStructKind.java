
/**
 * {@code PSSStructKind} is a model of struct_type.
 */
public class PSSStructKind {

    /** the struct struct_kind */
    public static final PSSStructKind STRUCT = new PSSStructKind();

    /** the buffer struct_kind */
    public static final PSSStructKind BUFFER = new PSSStructKind(PSSObjectKind.buffer);

    /** the stream struct_kind */
    public static final PSSStructKind STREAM = new PSSStructKind(PSSObjectKind.stream);

    /** the state struct_kind */
    public static final PSSStructKind STATE = new PSSStructKind(PSSObjectKind.state);

    /** the resource struct_kind */
    public static final PSSStructKind RESOURCE = new PSSStructKind(PSSObjectKind.resource);

    private PSSObjectKind m_object_kind = null;

    private PSSStructKind() {
    }

    private PSSStructKind(PSSObjectKind kind) {
        m_object_kind = kind;
    }

    /**
     * Returns {@code true} if this object is a struct.
     *
     * @return {@code true} if this object is a struct
     */
    public boolean isStruct() {
        return m_object_kind == null;
    }

    /**
     * Returns {@code true} if this object is an object_kind.
     *
     * @return {@code true} if this object is an object_kind
     */
    public boolean isObject() {
        return m_object_kind != null;
    }

    /**
     * Returns all struct kinds.
     *
     * @return all struct kinds
     */
    public static PSSStructKind[] values() {
        return new PSSStructKind[] {
                STRUCT, BUFFER, STREAM, STATE, RESOURCE
        };
    }

    /**
     * Returns the struct kind represented as a specified string.
     *
     * @param str a string representation of a struct kind
     * @return the struct kind represented as {@code str}
     */
    public static PSSStructKind valueOf(String str) {
        for (PSSStructKind k : values()) {
            if (k.toString().equals(str))
                return k;
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PSSStructKind) {
            PSSStructKind k = (PSSStructKind) obj;
            if (m_object_kind == null && k.m_object_kind == null)
                return true;
            else if (m_object_kind == null || k.m_object_kind == null)
                return false;
            else
                return m_object_kind.equals(k.m_object_kind);
        }
        return false;
    }

    @Override
    public String toString() {
        return m_object_kind == null ? "struct" : m_object_kind.toString();
    }

}