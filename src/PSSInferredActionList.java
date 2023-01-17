import java.util.*;

public class PSSInferredActionList {
	ArrayList<PSSInferredAction> m_list;

	PSSInferredActionList () {
		m_list = new ArrayList<PSSInferredAction>();
	}

	public int size() {
		return m_list.size();
	}

	public PSSInferredAction get(int i) {
		return m_list.get(i);
	}

	public void add(PSSInferredAction item) {
		m_list.add(item);
	}
	public void addAll(PSSInferredActionList list) {
		m_list.addAll(list.m_list);
	}

}


