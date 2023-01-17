import java.util.*;

public class PSSSetVal extends PSSVal {

	ArrayList<PSSVal> m_list;
	public PSSSetVal () {
		m_list = new ArrayList<PSSVal>();
	}
	public void addVal(PSSVal item) {
		m_list.add(item);
	}
	public void clear() {
		m_list.clear();
	}
	public ArrayList<PSSVal> getValList() {
		return m_list;
	}
	public boolean isEmpty() {
		return (m_list.size() == 0);
	}

	public PSSVal getMin() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::getMin is not implemented");
		return null;
	}
	public PSSVal getMax() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::getMax is not implemented");
		return null;
	}

	public PSSVal indexOf(int index) {
		return m_list.get(index);
	}

	public PSSVal randomIn() {
		int size = m_list.size();
		int randi = PSSRandom.nextInt(0, size-1);

		PSSVal item = m_list.get(randi);

		if (item.isRangeVal()) {
			return item.randomIn();
		}
		else {
			return item;
		}
	}

	public PSSBoolVal InRange(PSSVal lhs) {
		for (int i=0; i<m_list.size(); i++) {
			PSSVal item = m_list.get(i);
			if (item.InRange(lhs).toBool()) {
				return new PSSBoolVal(true);
			}
		}
		return new PSSBoolVal(false);
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
		return "[ " + text + " ]";
	}

	public int toInt() {
		PSSMessage.Error("RANGEVAL", "IntVal cannot be converted to Int");
		return 0;
	}
	public String toString() {
		PSSMessage.Error("RANGEVAL", "IntVal cannot be converted to string");
		return "";
	}
	public boolean toBool() {
		PSSMessage.Error("RANGEVAL", "IntVal cannot be converted to boolean");
		return false;
	}
}
