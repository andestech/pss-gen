
/**
 * {@code PSSMapModel} represents the type of maps. Maps are non-randomizable.
 */
public class PSSMapModel extends PSSModel implements PSSIAggregate {

    /** The key type */
    PSSModel m_key_type_model;

    /** The value type */
    PSSModel m_val_type_model;

    /**
     * Constructs a map type.
     *
     * @param key_type_model the key type
     * @param val_type_model the value type
     */
    public PSSMapModel(PSSModel key_type_model, PSSModel val_type_model) {
        super("map<" + key_type_model.m_id + "," + val_type_model.m_id + ">");

        m_key_type_model = key_type_model;
        m_val_type_model = val_type_model;
    }

    @Override
    public PSSMapInst declInst(String id, boolean rand) {
        return new PSSMapInst(id, this);
    }

    /**
     * Returns the type of keys.
     *
     * @return the type of keys
     */
    public PSSModel getKeyType() {
        return m_key_type_model;
    }

    /**
     * Returns the type of values.
     *
     * @return the type of values
     */
    public PSSModel getValueType() {
        return m_val_type_model;
    }

    @Override
    public String getText() {
        return m_id;
    }

    @Override
    public void dump(String indent) {
        System.out.println(indent + getText());
    }

    @Override
    public boolean isCompatible(PSSModel model) {
        if (model instanceof PSSMapModel) {
            PSSMapModel m = (PSSMapModel) model;
            return m_key_type_model.isCompatible(m.m_key_type_model)
                    && m_val_type_model.isCompatible(m.m_val_type_model);
        }
        return false;
    }

    @Override
    public String toString() {
        return getText();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PSSMapModel) {
            PSSMapModel m = (PSSMapModel) obj;
            return m_key_type_model.equals(m.m_key_type_model) && m_val_type_model.equals(m.m_val_type_model);
        }
        return false;
    }

}
