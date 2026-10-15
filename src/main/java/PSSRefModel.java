
/**
 * A {@code PSSRefModel} is the data type of a reference. A reference can point
 * to a variable of a type category, or a variable of a plain data type.
 */
public class PSSRefModel extends PSSModel {

    /** the type category of this reference */
    PSSTypeCategory m_type_category;

    /** the plain data type of this reference */
    PSSModel m_data_model;

    /**
     * Constructs a reference type of a type category.
     *
     * @param type_category a type category
     */
    public PSSRefModel(PSSTypeCategory type_category) {
        super("ref " + type_category.toString());
        m_type_category = type_category;
        m_data_model = null;
    }

    /**
     * Constructs a reference type of a plain data type.
     *
     * @param data_model a plain data type
     */
    public PSSRefModel(PSSModel data_model) {
        super("ref " + data_model.m_id);
        m_type_category = null;
        m_data_model = data_model;
    }

    /**
     * Returns {@code true} if this is a reference of a type category.
     *
     * @return {@code true} if this is a reference of a type category
     */
    public boolean isTypeCategory() {
        return m_type_category != null;
    }

    /**
     * Returns {@code true} if this is a reference of a plain data type.
     *
     * @return {@code true} if this is a reference of a plain data type
     */
    public boolean isPlainDataType() {
        return m_data_model != null;
    }

    /**
     * Returns the data type pointed to by references of this type
     *
     * @return the data type pointed to by references of this type
     */
    public PSSTypeCategory getTypeCategory() {
        return m_type_category;
    }

    /**
     * Returns the plain data type.
     *
     * @return the plain data type
     */
    public PSSModel getPlainTypeModel() {
        return m_data_model;
    }

    @Override
    public boolean isCompatible(PSSModel m) {
        if (m instanceof PSSRefModel) {
            PSSRefModel r = (PSSRefModel) m;
            boolean b1 = isTypeCategory();
            boolean b2 = r.isTypeCategory();
            if (b1 && b2)
                return getTypeCategory().equals(r.getTypeCategory());
            else if (b1 || b2) {
                PSSTypeCategory t = b1 ? getTypeCategory() : r.getTypeCategory();
                PSSModel o = b1 ? r.getPlainTypeModel() : getPlainTypeModel();
                return t.isModelCompatible(o);
            } else
                return getPlainTypeModel().isCompatible(r.getPlainTypeModel());
        }
        return false;
    }

    @Override
    public PSSRefInst declInst(String id, boolean rand) {
        return new PSSRefInst(id, this);
    }

}