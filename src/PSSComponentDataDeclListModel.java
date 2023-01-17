import java.util.*;

public class PSSComponentDataDeclListModel extends PSSModel {

	ArrayList<PSSComponentDataDeclModel> m_list;

	PSSComponentDataDeclListModel() {
		super("");
		m_list = new ArrayList<PSSComponentDataDeclModel>();
	}

	public int size() {
		return m_list.size();
	}
	public void add(PSSComponentDataDeclModel item) {
		m_list.add(item);
	}
	public PSSComponentDataDeclModel get(int i) {
		return m_list.get(i);
	}
	public void declInst(PSSInst parent_inst) {
		for (int i=0; i<m_list.size(); i++) {
			PSSComponentDataDeclModel item = m_list.get(i);
			item.declInst(parent_inst);
		}
	}
	public void dump(String indent) {
		for (int i=0; i<m_list.size(); i++) {
			PSSComponentDataDeclModel item = m_list.get(i);
			item.dump(indent);
		}
	}

}

