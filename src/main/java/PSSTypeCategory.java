
/**
 * This is a model of type_category.
 */
public class PSSTypeCategory {

    /** the identity of the action type category */
    public static final String ACTION_ID = "action";

    /** the identity of the component type category */
    public static final String COMPONENT_ID = "component";

    /** the action type category */
    public static final PSSTypeCategory ACTION = new PSSTypeCategory(ACTION_ID);

    /** the component type category */
    public static final PSSTypeCategory COMPONENT = new PSSTypeCategory(COMPONENT_ID);

    /** the struct type category */
    public static final PSSTypeCategory STRUCT = new PSSTypeCategory(PSSStructKind.STRUCT);

    /** the buffer type category */
    public static final PSSTypeCategory BUFFER = new PSSTypeCategory(PSSStructKind.BUFFER);

    /** the stream type category */
    public static final PSSTypeCategory STREAM = new PSSTypeCategory(PSSStructKind.STREAM);

    /** the state type category */
    public static final PSSTypeCategory STATE = new PSSTypeCategory(PSSStructKind.STATE);

    /** the resource type category */
    public static final PSSTypeCategory RESOURCE = new PSSTypeCategory(PSSStructKind.RESOURCE);

    /** the identity of this type category */
    String m_id;

    /** a struct kind is also a type_category */
    PSSStructKind m_kind;

    private PSSTypeCategory(String id) {
        m_id = id;
        m_kind = null;
    }

    private PSSTypeCategory(PSSStructKind kind) {
        m_id = null;
        m_kind = kind;
    }

    /**
     * Returns {@code true} if this type category is an action.
     *
     * @return {@code true} if this type category is an action
     */
    public boolean isAction() {
        return ACTION_ID.equals(m_id);
    }

    /**
     * Returns {@code true} if this type category is a component.
     *
     * @return {@code true} if this type category is a component
     */
    public boolean isComponent() {
        return COMPONENT_ID.equals(m_id);
    }

    /**
     * Returns {@code true} if this type category is a struct kind.
     *
     * @return {@code true} if this type category is a struct kind
     */
    public boolean isStructKind() {
        return m_kind != null;
    }

    /**
     * Returns the struct kind.
     *
     * @return the struct kind, or {@code null} if this type category is not a
     *         struct kind
     */
    public PSSStructKind getStructKind() {
        return m_kind;
    }

    /**
     * Returns {@code true} if the input is an instance of this type category.
     *
     * @param inst an instance
     * @return {@code true} if {@code inst} is an instance of this type category
     */
    public boolean isModelCompatible(PSSModel m) {
        if (m instanceof PSSRefModel) {
            PSSRefModel r = (PSSRefModel) m;
            if (r.isPlainDataType())
                m = r.getPlainTypeModel();
        }

        if (this == ACTION) {
            return m instanceof PSSActionModel;
        } else if (this == COMPONENT) {
            return m instanceof PSSComponentModel;
        } else if (this == STRUCT) {
            if (!(m instanceof PSSStructModel))
                return false;
            PSSStructModel s = (PSSStructModel) m;
            return PSSStructKind.STRUCT.equals(s.getStructKind());
        } else if (this == BUFFER) {
            if (!(m instanceof PSSStructModel))
                return false;
            PSSStructModel s = (PSSStructModel) m;
            return PSSStructKind.BUFFER.equals(s.getStructKind());
        } else if (this == STREAM) {
            if (!(m instanceof PSSStructModel))
                return false;
            PSSStructModel s = (PSSStructModel) m;
            return PSSStructKind.STREAM.equals(s.getStructKind());
        } else if (this == STATE) {
            if (!(m instanceof PSSStructModel))
                return false;
            PSSStructModel s = (PSSStructModel) m;
            return PSSStructKind.STATE.equals(s.getStructKind());
        } else if (this == RESOURCE) {
            if (!(m instanceof PSSStructModel))
                return false;
            PSSStructModel s = (PSSStructModel) m;
            return PSSStructKind.RESOURCE.equals(s.getStructKind());
        }

        return false;
    }

    /**
     * Returns {@code true} if the input is an instance of this type category.
     *
     * @param inst an instance
     * @return {@code true} if {@code inst} is an instance of this type category
     */
    public boolean isInstCompatible(PSSInst inst) {
        if (inst instanceof PSSRefInst)
            inst = ((PSSRefInst) inst).getReference();

        if (this == ACTION) {
            return inst instanceof PSSActionInst;
        } else if (this == COMPONENT) {
            return inst instanceof PSSComponentInst;
        } else if (this == STRUCT) {
            if (!(inst instanceof PSSStructInst))
                return false;
            PSSStructInst s = (PSSStructInst) inst;
            return PSSStructKind.STRUCT.equals(s.getTypeModel().getStructKind());
        } else if (this == BUFFER) {
            if (!(inst instanceof PSSStructInst))
                return false;
            PSSStructInst s = (PSSStructInst) inst;
            return PSSStructKind.BUFFER.equals(s.getTypeModel().getStructKind());
        } else if (this == STREAM) {
            if (!(inst instanceof PSSStructInst))
                return false;
            PSSStructInst s = (PSSStructInst) inst;
            return PSSStructKind.STREAM.equals(s.getTypeModel().getStructKind());
        } else if (this == STATE) {
            if (!(inst instanceof PSSStructInst))
                return false;
            PSSStructInst s = (PSSStructInst) inst;
            return PSSStructKind.STATE.equals(s.getTypeModel().getStructKind());
        } else if (this == RESOURCE) {
            if (!(inst instanceof PSSStructInst))
                return false;
            PSSStructInst s = (PSSStructInst) inst;
            return PSSStructKind.RESOURCE.equals(s.getTypeModel().getStructKind());
        }

        return false;
    }

    /**
     * Returns all type categories.
     *
     * @return all type categories
     */
    public static PSSTypeCategory[] values() {
        return new PSSTypeCategory[] {
                ACTION, COMPONENT, STRUCT, BUFFER, STREAM, STATE, RESOURCE
        };
    }

    /**
     * Returns the type category represented as a specified string.
     *
     * @param str a string representation of a type category
     * @return the type category represented as {@code str}
     */
    public static PSSTypeCategory valueOf(String str) {
        for (PSSTypeCategory t : values()) {
            if (t.toString().equals(str))
                return t;
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PSSTypeCategory) {
            PSSTypeCategory t = (PSSTypeCategory) obj;
            boolean b1 = isAction();
            boolean b2 = t.isAction();
            if (b1 && b2)
                return true;
            if (b1 || b2)
                return false;
            b1 = isComponent();
            b2 = isComponent();
            if (b1 && b2)
                return true;
            if (b1 || b2)
                return false;
            return getStructKind().equals(t.getStructKind());
        }
        return false;
    }

    @Override
    public String toString() {
        return m_id == null ? m_kind.toString() : m_id;
    }

}