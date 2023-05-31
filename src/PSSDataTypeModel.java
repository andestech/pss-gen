
/**
 * A {@code PSSDataTypeModel} is a data type with delayed resolution.
 */
public class PSSDataTypeModel extends PSSModel {

	/** a model where this data type is used */
	PSSModel m_location;

	/** the type name to be resolved later */
	String m_type;

	/**
	 * Constructs this data type.
	 *
	 * @param loc  a model where this data type appears, not necessary the parent
	 * @param type the name of the data type to be resolved later
	 */
	public PSSDataTypeModel(PSSModel loc, String type) {
		super("");
		m_location = loc;
		m_type = type;
	}

	/**
	 * Resolve the data type.
	 *
	 * @return the data type denoted by this one
	 */
	public PSSModel resolve() {
		return m_location.findDeclaration(m_type);
	}

	@Override
	public boolean isCompatible(PSSModel m) {
		return resolve().isCompatible(m);
	}

	@Override
	public PSSInst declInst(String id, boolean rand) {
		PSSModel type_decl = resolve();
		if (type_decl == null) {
			PSSMessage.Error("DataType", "Cannot find type declaration '" + m_type +
					"'");
		}

		return type_decl.declInst(id, rand);
	}

	@Override
	public String getText() {
		return m_type;
	}

	@Override
	public void dump(String indent) {
		System.out.println(indent + m_type);
	}

};
