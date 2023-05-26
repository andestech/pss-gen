
/**
 * A {@code PSSArrayModel} is an array data type.
 */
public class PSSArrayModel extends PSSModel {

	/** the data type of array elements */
	PSSModel m_elem_type_model;

	/** the array size */
	PSSExpression m_size_exp;

	/**
	 * Constructs this array data type.
	 *
	 * @param type_model the data type of array elements
	 * @param size_exp   the array size
	 */
	public PSSArrayModel(PSSModel type_model, PSSExpression size_exp) {
		super("array<" + type_model.getText() + ", " + size_exp.getText() + ">");
		m_elem_type_model = type_model;
		m_size_exp = size_exp;

	}

	@Override
	public boolean isCompatible(PSSModel model) {
		if (model instanceof PSSArrayModel) {
			PSSArrayModel m = (PSSArrayModel) model;
			return getElementTypeModel().isCompatible(m.getElementTypeModel()) && getArraySize() == m.getArraySize();
		} else if (model instanceof PSSListModel) {
			// A list literal may be assigned to an array
			PSSListModel m = (PSSListModel) model;
			return getElementTypeModel().isCompatible(m.getElementTypeModel());
		}
		return false;
	}

	/**
	 * Returns the data type of array elements.
	 *
	 * @return the data type of array elements
	 */
	public PSSModel getElementTypeModel() {
		return m_elem_type_model;
	}

	/**
	 * Returns the array size.
	 *
	 * @return the array size
	 */
	public int getArraySize() {
		return m_size_exp.eval(null).toInt();
	}

	@Override
	public PSSArrayInst declInst(String id, boolean rand) {
		return new PSSArrayInst(id, this, rand);
	}

	@Override
	public String getText() {
		return m_id;
	}

	@Override
	public void dump(String indent) {
		System.out.println(indent + getText());
	}

}
