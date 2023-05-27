
/**
 * {@code PSSRefVal} is a reference to a {@code PSSInst}.
 */
public class PSSRefVal extends PSSVal {

	PSSInst m_inst;

	/**
	 * Constructs this reference value.
	 *
	 * @param model the data type of this reference
	 * @param inst  the instance
	 */
	public PSSRefVal(PSSRefModel model, PSSInst inst) {
		super(model);
		m_inst = inst;
	}

	@Override
	public PSSRefModel getTypeModel() {
		return (PSSRefModel) super.getTypeModel();
	}

	/**
	 * Returns the type of the target.
	 *
	 * @return the type of the target
	 */
	public PSSModel getTargetTypeModel() {
		PSSRefModel m = getTypeModel();
		return m == null ? null : m.getTargetTypeModel();
	}

	/**
	 * Returns the instance referred to by this reference.
	 *
	 * @return the instance referred to by this reference
	 */
	public PSSInst getInst() {
		return m_inst;
	}

	/**
	 * Returns the hierarchical ID of the instance referred to by this reference.
	 *
	 * @return the hierarchical ID of the instance referred to by this reference
	 */
	public String getHierarchicalID() {
		return m_inst == null ? "null" : m_inst.getHierarchyId();
	}

	@Override
	public String getText() {
		return m_inst == null ? "null" : m_inst.getHierarchyId();
	}

	@Override
	public String toTargetCode() {
		return m_inst == null ? "null" : m_inst.toTargetCode();
	}

	@Override
	public boolean isRangeVal() {
		return false;
	}

	@Override
	public boolean isBound() {
		return false;
	}

	@Override
	public PSSVal indexOf(PSSVal index) {
		return m_inst == null ? null : new PSSRefVal(null, m_inst.indexOf(index));
	}

	@Override
	public PSSBoolVal Equal(PSSVal rhs) {
		if (rhs instanceof PSSRefVal) {
			PSSRefVal r = (PSSRefVal) rhs;
			return PSSBoolVal.valueOf(m_inst == r.m_inst);
		}
		return PSSBoolVal.FALSE;
	}

	@Override
	public PSSBoolVal NotEqual(PSSVal rhs) {
		return Equal(rhs).LogicalNot();
	}

}
