
/**
 * {@code PSSRefVal} is a reference to a {@code PSSInst}.
 */
public class PSSRefVal extends PSSVal {

	PSSInst m_inst;

	/**
	 * Constructs this reference value.
	 *
	 * @param model the data type
	 * @param inst  the instance
	 */
	public PSSRefVal(PSSModel model, PSSInst inst) {
		super(model);
		m_inst = inst;
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
