package pssgen.value;

import java.util.List;

import pssgen.inst.PSSInst;
import pssgen.inst.PSSStructInst;
import pssgen.model.PSSModel;
import pssgen.model.PSSRefModel;

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
		if (m_inst == null)
			return null;
		PSSInst res = m_inst.indexOf(index);
		PSSModel m = res.getTypeModel();
		return new PSSRefVal(new PSSRefModel(m), res);
	}

	/**
	 * PSS 2.0 Section 9.5.3: two references are equal when they refer to the same
	 * entity; two struct values, aggregates, are compared field by field.
	 */
	@Override
	public PSSBoolVal Equal(PSSVal rhs) {
		if (rhs instanceof PSSRefVal) {
			PSSRefVal r = (PSSRefVal) rhs;
			if (isStruct() && r.isStruct())
				return PSSBoolVal.valueOf(sameFields(r));
			return PSSBoolVal.valueOf(m_inst == r.m_inst);
		}
		return PSSBoolVal.FALSE;
	}

	@Override
	public PSSBoolVal NotEqual(PSSVal rhs) {
		return Equal(rhs).LogicalNot();
	}

	/** An element of a collection that holds struct values contains lhs when it equals it. */
	@Override
	public PSSBoolVal InRange(PSSVal lhs) {
		if (isStruct())
			return Equal(lhs);
		return super.InRange(lhs);
	}

	/** Struct values are equal by their fields, also as collection elements and set keys. */
	@Override
	public boolean equals(Object o) {
		if (isStruct() && o instanceof PSSRefVal && ((PSSRefVal) o).isStruct())
			return sameFields((PSSRefVal) o);
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		if (!isStruct())
			return super.hashCode();
		int h = 1;
		for (PSSInst field : m_inst.getInsts())
			h = 31 * h + field.toVal().hashCode();
		return h;
	}

	private boolean isStruct() {
		return m_inst instanceof PSSStructInst;
	}

	private boolean sameFields(PSSRefVal r) {
		if (m_inst == r.m_inst)
			return true;
		if (m_inst.getTypeModel() != r.m_inst.getTypeModel())
			return false;
		List<PSSInst> a = m_inst.getInsts();
		List<PSSInst> b = r.m_inst.getInsts();
		if (a.size() != b.size())
			return false;
		for (int i = 0; i < a.size(); i++) {
			if (!a.get(i).toVal().equals(b.get(i).toVal()))
				return false;
		}
		return true;
	}

	/**
	 * Returns the value of the instance that val refers to, or val itself when it is
	 * not a reference (null included).
	 */
	/**
	 * Returns the data type of a value. A struct value is a reference to its struct
	 * instance, so its data type is the type of that instance.
	 *
	 * @param val a value
	 * @return the data type of val
	 */
	public static PSSModel typeOf(PSSVal val) {
		if (val instanceof PSSRefVal && ((PSSRefVal) val).getInst() != null)
			return ((PSSRefVal) val).getInst().getTypeModel();
		return val.getTypeModel();
	}

	public static PSSVal deref(PSSVal val) {
		if (val instanceof PSSRefVal) {
			return ((PSSRefVal) val).getInst().toVal();
		}
		return val;
	}
}
