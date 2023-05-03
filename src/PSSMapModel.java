
/**
 * {@code PSSMapModel} represents the type of maps. Maps are non-randomizable.
 */
public class PSSMapModel extends PSSModel {

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
        PSSMessage.Debug("[" + this.getClass().getName() + "] Creating MapModel: key = " + key_type_model.getId()
                + ", value = " + val_type_model.getId());
        m_key_type_model = key_type_model;
        m_val_type_model = val_type_model;
    }

    @Override
    public PSSMapInst declInst(String id, boolean rand) {
        PSSMessage.Debug("[" + this.getClass().getName() + "] Instantiating MapModel: id = " + id + ", rand = " + rand);
        return new PSSMapInst(id, m_key_type_model, m_val_type_model);
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
    public String toString() {
        return getText();
    }

}
