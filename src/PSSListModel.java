
public class PSSListModel extends PSSModel {

	PSSModel m_type_model;

	public PSSListModel(PSSModel type_model) {
		super("list<" + type_model.m_id + ">");
		m_type_model = type_model;
	}

	@Override
	public PSSListInst declInst(String id, boolean rand) {
		return new PSSListInst(id, m_type_model);
	}

	@Override
	public String getText() {
		return m_id;
	}

    @Override
    public boolean isCompatible(PSSModel model) {
        if (model instanceof PSSListModel) {
            PSSListModel m = (PSSListModel) model;
            if (m_type_model == null || m.m_type_model == null)
                return true;
            return m_type_model.isCompatible(m.m_type_model);
        }
        return false;
    }

	@Override
	public void dump (String indent) {
		System.out.println(indent + getText());
	}

}
