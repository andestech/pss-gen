import java.util.*;

public class PSSListInst extends PSSInst {
	ArrayList<PSSInst> m_list;

	PSSListInst(String id, PSSModel type_model, boolean rand) {
		super(id, "list<"+type_model.m_id+">", rand);
		m_list = new ArrayList<PSSInst>();
	}

	public void assign(PSSVal val) {
		for (int i=0; i<m_list.size(); i++) {
			PSSInst item = m_list.get(i);
			PSSVal item_val = val.indexOf(i);
			item.assign(item_val);
		}
	}

	public void randomize() {
		for (int i=0; i<m_list.size(); i++) {
			PSSInst item = m_list.get(i);
			item.randomize();
		}
	}

	public PSSInst indexOf(int index) {
		if (index >= m_list.size()) {
			PSSMessage.Error("LISTINDEX", "'"+getHierarchyId()+"' index '"+index+"' is out of range");
		}
		return m_list.get(index);
	}
};

