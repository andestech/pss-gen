
/**
 * A {@code PSSRefModel} is the data type of a reference.
 */
public class PSSRefModel extends PSSModel {

    /** the data type pointed to by references of this type */
    PSSModel m_data_model;

    /**
     * Constructs a reference type.
     *
     * @param data_model the data type pointed to by references of this type
     */
    public PSSRefModel(PSSModel data_model) {
        super("ref " + data_model.m_id);
        m_data_model = data_model;
    }

    /**
     * Returns the data type pointed to by references of this type
     *
     * @return the data type pointed to by references of this type
     */
    public PSSModel getTargetTypeModel() {
        return m_data_model;
    }

    @Override
    public boolean isCompatible(PSSModel m) {
        if (m instanceof PSSRefModel) {
            PSSRefModel r = (PSSRefModel) m;
            return m_data_model.isCompatible(r.m_data_model);
        }
        return false;
    }

    @Override
    public PSSRefInst declInst(String id, boolean rand) {
        return new PSSRefInst(id, this);
    }

}