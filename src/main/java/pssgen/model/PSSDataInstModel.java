package pssgen.model;

import java.util.*;

import pssgen.expr.PSSExpression;
import pssgen.inst.PSSArrayInst;
import pssgen.inst.PSSInst;
import pssgen.value.PSSVal;

public class PSSDataInstModel extends PSSModel {

	//public String m_id;
	private PSSExpression m_dim_exp;	// array dimension
	private PSSExpression m_init_exp;

	public PSSDataInstModel(String id, PSSExpression dim_exp, PSSExpression init_exp) {
		super(id);
		m_dim_exp = dim_exp;
		m_init_exp = init_exp;
	}

	public void eval(PSSInst parent_inst, PSSModel type, boolean rand) {
		PSSInst inst;

		if (m_dim_exp == null) {
			inst = type.declInst(getId(), rand);
		}
		else {
			PSSVal dim_val = m_dim_exp.eval(parent_inst);
			inst = PSSArrayInst.ofElementDataType(getId(), type, dim_val.toInt(), rand);
		}
		parent_inst.addInst(inst);

		if (m_init_exp != null) {
			PSSVal init_val = PSSExpression.expecting(m_init_exp, inst).eval(parent_inst);
			inst.assign(init_val);
		}
	}

	public String getText() {
		String result = getId();
		if (m_dim_exp != null) {
			result = result.concat("[" + m_dim_exp.getText() + "]");
		}
		if (m_init_exp != null) {
			result = result.concat(" = " + m_init_exp.getText());
		}
		return result;
	}

	public void dump () {
		System.out.println("PSSDataInstModel");
	}

	public final PSSExpression getDimExp() {
		return m_dim_exp;
	}

	public final PSSExpression getInitExp() {
		return m_init_exp;
	}
}
