
/**
 * A {@code PSSSetModel} denotes the type of a set.
 */
public class PSSSetModel extends PSSModel implements PSSIAggregate {

    /** the data type of set elements */
    PSSModel m_elem_data_type;

    /**
     * Constructs this model.
     *
     * @param data_type the type of set elements
     */
    public PSSSetModel(PSSModel data_type) {
        super("set<" + data_type.m_id + ">");
        m_elem_data_type = data_type;
    }

    /**
     * Returns the data type of set elements.
     *
     * @return the data type of set elements
     */
    public PSSModel getElementTypeModel() {
        return m_elem_data_type;
    }

    @Override
    public PSSSetInst declInst(String id, boolean rand) {
        return new PSSSetInst(id, this);
    }

    @Override
    public String getText() {
        return m_id;
    }

    @Override
    public boolean isCompatible(PSSModel model) {
        if (model instanceof PSSSetModel) {
            PSSSetModel m = (PSSSetModel) model;
            return m_elem_data_type.isCompatible(m.m_elem_data_type);
        }
        return false;
    }

    @Override
    public void dump(String indent) {
        System.out.println(indent + getText());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PSSSetModel) {
            PSSSetModel m = (PSSSetModel) obj;
            return m_elem_data_type.equals(m.m_elem_data_type);
        }
        return false;
    }

}
