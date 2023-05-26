import java.util.*;

public class PSSDataInstModel extends PSSModel {

	//public String m_id;
	public PSSExpression m_dim_exp;	// array dimension
	public PSSExpression m_init_exp;

	public PSSDataInstModel(String id, PSSExpression dim_exp, PSSExpression init_exp) {
		super(id);
		m_dim_exp = dim_exp;
		m_init_exp = init_exp;
	}

	public void eval(PSSInst parent_inst, PSSModel type, boolean rand) {
		PSSInst inst;

		if (m_dim_exp == null) {
			inst = type.declInst(m_id, rand);
		}
		else {
			PSSVal dim_val = m_dim_exp.eval(parent_inst);
			inst = PSSArrayInst.ofElementDataType(m_id, type, dim_val.toInt(), rand);
		}
		parent_inst.addInst(inst);

		if (m_init_exp != null) {
			PSSVal init_val = m_init_exp.eval(parent_inst);
			inst.assign(init_val);
		}
	}

	public String getText() {
		String result = m_id;
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
}
