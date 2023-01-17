import java.util.*;

public class PSSArrayModel extends PSSModel {

	PSSModel m_type_model;
	PSSExpression m_size_exp;

	public PSSArrayModel (PSSModel type_model, PSSExpression size_exp) {
		super("array<"+type_model.getText()+size_exp.getText()+">");
		m_type_model = type_model;
		m_size_exp = size_exp;
	}
	public PSSArrayInst declInst(String id, boolean rand) {
		PSSVal size_val = m_size_exp.eval(null);

		return new PSSArrayInst(id, m_type_model, size_val.toInt(), rand);
	}

	public String getText() {
		return m_id;
	}

	public void dump (String indent) {
		System.out.println(indent + getText());
	}
}
