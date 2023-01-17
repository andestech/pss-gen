import java.util.*;

public class PSSDataInstListModel extends PSSModel {
	ArrayList<PSSDataInstModel> m_list;
	PSSDataInstListModel() {
		super("");
		m_list = new ArrayList<PSSDataInstModel>();
	}
	int size() {
		return m_list.size();
	}
	PSSDataInstModel get(int i) {
		return m_list.get(i);
	}
	void add(PSSDataInstModel inst) {
		m_list.add(inst);
	}

	public String getText() {
		String result = "";
		for (int i=0; i<m_list.size(); i++) {
			PSSDataInstModel inst = m_list.get(i);
			if (i == 0) {
				result = inst.getText();
			}
			else {
				result = result + ", " + inst.getText();
			}
		}
		return result;
	}

}

