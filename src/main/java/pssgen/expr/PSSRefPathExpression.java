package pssgen.expr;

import java.util.ArrayList;

import pssgen.inst.PSSInst;
import pssgen.model.PSSModel;
import pssgen.util.PSSMessage;
import pssgen.value.PSSBoolVal;
import pssgen.value.PSSIntVal;
import pssgen.value.PSSVal;

/**
 * A {@code PSSRefPathExpression} is a simple or hierarchical reference.
 */
public class PSSRefPathExpression extends PSSExpression {

	/** the optional type identifier */
	private String m_type_identifier_elems;

	/** the static_ref_path plus an optional hierarchical_id */
	private PSSMemberPathElemExpression m_ref_path;

	/** the optional beginning index of bit slice */
	private PSSExpression m_bit_slice_from = null;

	/** the optional ending index of bit slice */
	private PSSExpression m_bit_slice_to = null;

	/**
	 * Constructs this reference.
	 *
	 * @param type_identifier_elems the optional type identifier
	 * @param ref_path              the static reference path
	 * @param bit_slice_from        the optional beginning index of bit slice
	 * @param bit_slice_to          the optional ending index of bit slice
	 */
	public PSSRefPathExpression(String type_identifier_elems, PSSMemberPathElemExpression ref_path,
			PSSExpression bit_slice_from, PSSExpression bit_slice_to) {
		if (type_identifier_elems != null) {
			if (type_identifier_elems.endsWith("::"))
				type_identifier_elems = type_identifier_elems.substring(0, type_identifier_elems.length() - 2);
		}
		m_type_identifier_elems = type_identifier_elems;
		m_ref_path = ref_path;
		m_bit_slice_from = bit_slice_from;
		m_bit_slice_to = bit_slice_to;
		if (m_bit_slice_from != null ^ m_bit_slice_to != null)
			PSSMessage.Fatal("[" + getClass().getName() + "] bit_slice should have both msb and lsb");
	}

	@Override
	public boolean isRandomable (PSSInst var) {
		return getInst(var).isRandomable();
	}

	@Override
	public ArrayList<PSSInst> getInsts(PSSInst var) {
		var ret = new ArrayList<PSSInst>();
		ret.add(getInst(var));
		return ret;
	}

	/** The declaration the qualifier names; a leading '::' starts at the outermost namespace. */
	private PSSModel findQualifier(PSSInst var) {
		PSSModel scope = var.getTypeModel();
		String qualifier = m_type_identifier_elems;
		if (qualifier.startsWith("::")) {
			while (scope.getParent() != null)
				scope = scope.getParent();
			qualifier = qualifier.substring(2);
		}
		return scope.findDeclaration(qualifier);
	}

	@Override
	public PSSInst getInst(PSSInst var) {
		PSSInst inst = null;
		if (m_type_identifier_elems != null && !m_type_identifier_elems.equals("")) {
			PSSModel m = findQualifier(var);
			inst = m_ref_path.getInst(m, var);
		} else {
			inst = m_ref_path.getInst(var);
		}
		return inst;
	}

	@Override
	public PSSVal eval(PSSInst var) {
		PSSVal res = evalOriVal(var);
		if (m_bit_slice_from != null && m_bit_slice_to != null) {
			int msb = m_bit_slice_to.eval(var).toInt();
			int lsb = m_bit_slice_from.eval(var).toInt();
			res = res.extract(msb, lsb, var.run().log());
		}
		return res;
	}

	public void assign (PSSInst var, PSSVal val) {
		PSSInst inst = getInst(var);

		if (val instanceof PSSIntVal || val instanceof PSSBoolVal) {
			if (m_bit_slice_from != null && m_bit_slice_to != null) {
				int msb = m_bit_slice_to.eval(var).toInt();
				int lsb = m_bit_slice_from.eval(var).toInt();

				PSSVal ori_val = evalOriVal(var);
				PSSVal invert_mask = new PSSIntVal(ori_val.extract(msb, lsb, var.run().log()).toBigInteger(), msb + 1, false);
				invert_mask = invert_mask.LeftShift(lsb);
				ori_val = ori_val.BitwiseXor(invert_mask);

				PSSVal new_val = new PSSIntVal(val.extract(msb-lsb,0, var.run().log()).toBigInteger(), msb + 1, false);
				new_val = new_val.LeftShift(lsb);
				ori_val = ori_val.BitwiseOr(new_val);

				inst.assign(ori_val);
			} else {
				inst.assign(val);
			}
		} else {
			inst.assign(val);
		}
	}

	@Override
	public String getText() {
		return (m_type_identifier_elems == null || m_type_identifier_elems.equals("") ? ""
				: (m_type_identifier_elems + "::")) + m_ref_path.getText()
				+ (m_bit_slice_from == null || m_bit_slice_to == null ? ""
						: "[" + m_bit_slice_from.getText() + ":" + m_bit_slice_to.getText());
	}

	@Override
	public String toString() {
		return getText();
	}

	private PSSVal evalOriVal (PSSInst var) {
		PSSVal res = null;
		if (m_type_identifier_elems != null && !m_type_identifier_elems.equals("")) {
			PSSModel m = findQualifier(var);
			res = m_ref_path.eval(m, var);
		} else {
			res = m_ref_path.eval(var);
		}
		return res;
	}

	public final String getTypeIdentifierElems() {
		return m_type_identifier_elems;
	}

	public final PSSMemberPathElemExpression getRefPath() {
		return m_ref_path;
	}

	public final PSSExpression getBitSliceFrom() {
		return m_bit_slice_from;
	}

	public final PSSExpression getBitSliceTo() {
		return m_bit_slice_to;
	}
}
