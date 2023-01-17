import java.util.*;

public class PSSListModel extends PSSModel {

	PSSModel m_type_model;

	public PSSListModel (PSSModel type_model) {
		super("array<"+type_model.getText()+">");
		m_type_model = type_model;
	}
	public PSSListInst declInst(String id, boolean rand) {
		return new PSSListInst(id, m_type_model, rand);
	}

	public String getText() {
		return m_id;
	}

	public void dump (String indent) {
		System.out.println(indent + getText());
	}
}
