import java.util.*;

public class PSSUnboundRefList {
	ArrayList<PSSUnboundRef> m_list;

	PSSUnboundRefList() {
		m_list = new ArrayList<PSSUnboundRef>();
	}
	public int size() {
		return m_list.size();
	}
	public PSSUnboundRef get(int i) {
		return m_list.get(i);
	}
	public void add(PSSUnboundRef item) {
		m_list.add(item);
	}
	public void addAll(PSSUnboundRefList list) {
		m_list.addAll(list.m_list);
	}
}

