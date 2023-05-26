
/**
 * A {@code PSSListModel} denotes a list data type.
 */
public class PSSListModel extends PSSModel {

	/** the data type of list elements */
	PSSModel m_elem_type_model;

	/**
	 * Constructs this list.
	 *
	 * @param type_model the data type of list elements
	 */
	public PSSListModel(PSSModel type_model) {
		super("list<" + type_model.m_id + ">");
		m_elem_type_model = type_model;
	}

	/**
	 * Returns the data type of list elements.
	 *
	 * @return the data type of list elements
	 */
	public PSSModel getElementTypeModel() {
		return m_elem_type_model;
	}

	@Override
	public PSSListInst declInst(String id, boolean rand) {
		return new PSSListInst(id, this);
	}

	@Override
	public String getText() {
		return m_id;
	}

	@Override
	public boolean isCompatible(PSSModel model) {
		if (model instanceof PSSListModel) {
			PSSListModel m = (PSSListModel) model;
			return m_elem_type_model.isCompatible(m.m_elem_type_model);
		}
		return false;
	}

	@Override
	public void dump(String indent) {
		System.out.println(indent + getText());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PSSListModel) {
			PSSListModel m = (PSSListModel) obj;
			return m_elem_type_model.equals(m.m_elem_type_model);
		}
		return false;
	}

}
