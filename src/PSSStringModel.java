
public class PSSStringModel extends PSSModel {

	private static PSSStringModel m_model = null;

	private PSSStringModel() {
		super("string");
	}

	@Override
	public PSSInst declInst(String id, boolean rand) {
		PSSStringInst var = new PSSStringInst(id, rand);
		return var;
	}

	@Override
	public boolean isCompatible(PSSModel model) {
		return model instanceof PSSStringModel;
	}

	@Override
	public String getText() {
		return m_id;
	}

	/**
	 * Returns the singleton instanceof {@code PSSStringModel} (not thread safe).
	 *
	 * @return the singleton instanceof {@code PSSStringModel} (not thread safe)
	 */
	public static PSSStringModel getInstance() {
		if (m_model == null)
			m_model = new PSSStringModel();
		return m_model;
	}

}
