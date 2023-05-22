
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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PSSStructKind) {
            PSSStructKind k = (PSSStructKind) obj;
            if (m_object_kind == null && k.m_object_kind == null)
                return false;
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