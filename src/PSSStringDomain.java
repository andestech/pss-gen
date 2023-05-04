import java.util.*;

public class PSSStringDomain extends PSSDomain {

	ArrayList<PSSVal> m_list;

	public PSSStringDomain () {
		m_list = new ArrayList<PSSVal>();
	}

	public PSSStringDomain (boolean bound) {
		m_list = new ArrayList<PSSVal>();
		if (!bound) {
			PSSStringVal str_val = new PSSStringVal("", false);
			m_list.add(str_val);
		}
	}

	public ArrayList<PSSVal> getValList() {
		return m_list;
	}

	public void addVal(PSSVal val) {
		m_list.add(val);
	}
	public boolean isEmpty() {
		return m_list.size()==0;
	}

	public PSSStringDomain union(ArrayList<PSSVal> list) {
		PSSStringDomain result = new PSSStringDomain();

		for (int i=0; i<list.size(); i++) {
			PSSVal item = list.get(i);
			result.addVal(item);
		}
		for (int j=0; j<m_list.size(); j++) {
			PSSVal item2 = m_list.get(j);
			result.addVal(item2);
		}
		return result;
	}
	public PSSStringDomain union(PSSDomain dom) {
		ArrayList<PSSVal> list = ((PSSStringDomain)dom).getValList();
		return union(list);
	}

	public PSSStringDomain reduceDomainEqual(PSSVal val) {
		PSSStringDomain result = new PSSStringDomain(true);

		for (int i=0; i<m_list.size(); i++) {
			PSSVal item = m_list.get(i);
			PSSVal tmp = item.join(val);
			result.addVal(tmp);
		}

		return result;
	}
	public PSSStringDomain reduceDomainNotEqual(PSSVal val) {
		PSSStringDomain result = new PSSStringDomain();
	
		for (int i=0; i<m_list.size(); i++) {
			PSSVal item = m_list.get(i);
			if (item.NotEqual(val).toBool()) {
				result.addVal(item);
			}
		}

		return result;
	}
	public PSSStringDomain join(PSSDomain dom) {
		ArrayList<PSSVal> list = ((PSSStringDomain)dom).getValList();
		return join(list);
	}
	public PSSStringDomain join(ArrayList<PSSVal> list) {
		PSSStringDomain result = new PSSStringDomain();

		for (int i=0; i<list.size(); i++) {
			PSSVal item = list.get(i);
			for (int j=0; j<m_list.size(); j++) {
				PSSVal item2 = m_list.get(j);
				PSSVal join_val = item.join(item2);
				if (join_val != null) {
					result.addVal(join_val);
				}
			}
		}
		return result;
	}
	public PSSStringDomain reduceDomainIn(PSSSetVal val) {
		ArrayList<PSSVal> list = val.getValList();
		return join(list);
	}

	public PSSStringVal random() {
		int size = m_list.size();
		int randi = PSSRandom.nextInt(0, size-1);
		PSSVal item = m_list.get(randi);

		if (item.isBound()) {
			return new PSSStringVal(item.toStr());
		}
		else {
			String str = PSSRandom.nextString();
			return new PSSStringVal(str);
		}
	}

	public PSSBoolVal InRange(PSSVal lhs) {
		return new PSSBoolVal(isInside(lhs));
	}

	private boolean isInside(PSSVal lhs) {
		for (int i=0; i<m_list.size(); i++) {
			PSSVal item = m_list.get(i);
			if (item.isBound()) {
				if (item.Equal(lhs).toBool()) {
					return true;
				}
			}
			else {
				return true;
			}
		}
		return false;
	}

	public String getText() {
		String text = "";

		for (int i=0; i<m_list.size(); i++) {
			PSSVal item = m_list.get(i);
			if (text.length() == 0) {
				text = text.concat(item.getText());
			}
			else {
				text = text.concat(", " + item.getText());
			}
		}

		return "{" + text + "}";
	}

}
