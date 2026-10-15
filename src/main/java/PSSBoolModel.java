
/**
 * A {@code PSSBoolModel} is a singleton type model for Booleans. Use
 * {@link PSSBoolModel#getModel()} to obtain the instance.
 */
public class PSSBoolModel extends PSSModel {

	private static PSSBoolModel m_model = null;

	private PSSBoolModel() {
		super("bool");
	}

	@Override
	public PSSInst declInst(String id, boolean rand) {
		PSSBoolInst var = new PSSBoolInst(id, rand);
		return var;
	}

	@Override
	public boolean isCompatible(PSSModel model) {
		return model instanceof PSSBoolModel;
	}

	@Override
	public String getText() {
		return m_id;
	}

	/**
	 * Returns the singleton instance of {@code PSSBoolModel} (not thread safe).
	 *
	 * @return the singleton instance of {@code PSSBoolModel}
	 */
	public static PSSBoolModel getInstance() {
		if (m_model == null)
			m_model = new PSSBoolModel();
		return m_model;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof PSSBoolModel;
	}

}
