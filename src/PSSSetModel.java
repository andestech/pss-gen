
public class PSSSetModel extends PSSModel {

    PSSModel m_data_type;

    public PSSSetModel(PSSModel data_type) {
        super("set<" + data_type.m_id + ">");
        m_data_type = data_type;
    }

    @Override
    public PSSSetInst declInst(String id, boolean rand) {
        return new PSSSetInst(id, m_data_type);
    }

    @Override
    public String getText() {
        return m_id;
    }

    @Override
    public boolean isCompatible(PSSModel model) {
        if (model instanceof PSSSetModel) {
            PSSSetModel m = (PSSSetModel) model;
            if (m_data_type == null || m.m_data_type == null)
                return true;
            else
                return m_data_type.isCompatible(m.m_data_type);
        }
        return false;
    }

    @Override
    public void dump(String indent) {
        System.out.println(indent + getText());
    }

}
