import java.util.*;

public class PSSDataDeclModel extends PSSModel {

	public PSSModel m_type;
	PSSDataInstListModel m_inst_list;

	public PSSDataDeclModel(PSSModel type) {
		super("");
		m_type = type;
		m_inst_list = new PSSDataInstListModel();
	}

	public void addDataInst(PSSDataInstModel data_inst) {
		m_inst_list.add(data_inst);
	}

	public void eval(PSSInst parent_inst, boolean rand) {
		for (int i=0; i<m_inst_list.size(); i++) {
			PSSDataInstModel d_inst = m_inst_list.get(i);
			d_inst.eval(parent_inst, m_type, rand);
		}
	}

	public String getText() {
		String result = m_type.getText() + " " + m_inst_list.getText();;
		return result;
	}

	public void dump (String indent) {
		System.out.println(indent + getText());
	}
}
