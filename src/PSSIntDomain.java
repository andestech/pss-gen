import java.util.*;

public class PSSIntDomain extends PSSDomain {

	public ArrayList<PSSVal> m_list;
	public PSSIntDomain () {
		m_list = new ArrayList<PSSVal>();
	}
	public PSSIntDomain (int width, boolean sign) {
		m_list = new ArrayList<PSSVal>();
		PSSVal range_val = PSSRangeVal.getIntRangeVal(width, sign);
		m_list.add(range_val);
	}
	public PSSIntDomain (PSSVal val) {
		m_list = new ArrayList<PSSVal>();
		m_list.add(val);
	}
	public boolean isEmpty() {
		return (m_list.size() == 0);
	}
	public void addVal(PSSVal item) {
		m_list.add(item);
	}
	public ArrayList<PSSVal> getValList() {
		return m_list;
	}
	public PSSVal getMin() {
		PSSVal min = null;
		for (int i=0; i<m_list.size(); i++) {
			PSSVal item = m_list.get(i);
			if (min == null) {
				if (item.isRangeVal()) {
					min = item.getMin();
				}
				else {
					min = item;
				}
			}
			else {
				if (item.LessThan(min).toBool()) {
					min = item;
				}
			}
		}
		return min;
	}
	public PSSVal getMax() {
		PSSVal max = null;
		for (int i=0; i<m_list.size(); i++) {
			PSSVal item = m_list.get(i);
			if (max == null) {
				if (item.isRangeVal()) {
					max = item.getMax();
				}
				else {
					max = item;
				}
			}
			else {
				if (item.GreaterThan(max).toBool()) {
					max = item;
				}
			}
		}
		return max;
	}

	public PSSIntDomain reduceDomainEqual(PSSVal val) {
		if (InRange(val).toBool()) {
			return new PSSIntDomain(val);
		}
		else {
			return null;
		}
	}

	public PSSIntDomain reduceDomainLessEqual(PSSVal val) {
		PSSIntDomain result = new PSSIntDomain();

		for (int i=0; i<m_list.size(); i++) {
			PSSVal item = m_list.get(i);
			PSSVal tmp_item = item.reduceLessEqual(val);
			if (tmp_item != null) {
				result.addVal(tmp_item);
			}
		}
		return result;
	}
	public PSSIntDomain reduceDomainLessThan (PSSVal val) {
		PSSVal tmp_val = val.Sub(PSSIntVal.ONE());
		return reduceDomainLessEqual(tmp_val);
	}
	public PSSIntDomain reduceDomainGreaterThan (PSSVal val) {
		PSSVal tmp_val = val.Add(PSSIntVal.ONE());
		return reduceDomainGreaterEqual(tmp_val);
	}
	public PSSIntDomain reduceDomainGreaterEqual(PSSVal val) {
		PSSIntDomain result = new PSSIntDomain();

		for (int i=0; i<m_list.size(); i++) {
			PSSVal item = m_list.get(i);
			PSSVal tmp_item = item.reduceGreaterEqual(val);
			if (tmp_item != null) {
				result.addVal(tmp_item);
			}
		}
		return result;
	}
	public PSSIntDomain reduceDomainNotEqual(PSSVal val) {
		PSSIntDomain result = new PSSIntDomain();

		for (int i=0; i<m_list.size(); i++) {
			PSSVal item = m_list.get(i);
			if (item.isRangeVal()) {
				PSSVal min = item.getMin();
				PSSVal max = item.getMax();
				if (min.Equal(val).toBool()) {
					min = min.Add(PSSIntVal.ONE());
					result.addVal(new PSSRangeVal(min.toBigInteger(), max.toBigInteger()));
				}
				else if (max.Equal(val).toBool()) {
					max = max.Sub(PSSIntVal.ONE());	
					result.addVal(new PSSRangeVal(min.toBigInteger(), max.toBigInteger()));
				}
				else if (item.InRange(val).toBool()) {
					PSSVal tmp1 = val.Sub(PSSIntVal.ONE());
					PSSVal tmp2 = val.Add(PSSIntVal.ONE());
					result.addVal(new PSSRangeVal(min.toBigInteger(), tmp1.toBigInteger()));
					result.addVal(new PSSRangeVal(tmp2.toBigInteger(), max.toBigInteger()));
				}
				else {
					result.addVal(item);
				}
			}
			else {
				if (item.NotEqual(val).toBool()) {
					result.addVal(item);
				}
			}
		}
		return result;
	}
	public PSSIntDomain reduceDomainIn(PSSSetVal val) {
		ArrayList<PSSVal> list = val.getValList();
		return join(list);
	}
	public PSSIntDomain join(PSSDomain dom) {
		ArrayList<PSSVal> list = ((PSSIntDomain)dom).getValList();
		return join(list);
	}
	public PSSIntDomain union(PSSDomain dom) {
		ArrayList<PSSVal> list = ((PSSIntDomain)dom).getValList();
		return union(list);
	}

	public PSSIntDomain join(ArrayList<PSSVal> list) {
		PSSIntDomain result = new PSSIntDomain();

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
	public PSSIntDomain union(ArrayList<PSSVal> list) {
		PSSIntDomain result = new PSSIntDomain();

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


	public PSSVal random() {
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
		return "{" + text + "}";
	}

	@Override
	public int toInt() {
		PSSMessage.Error("RANGEVAL", "IntVal cannot be converted to Int");
		return 0;
	}
	@Override
	public String toStr() {
		PSSMessage.Error("RANGEVAL", "IntVal cannot be converted to string");
		return "";
	}
	@Override
	public boolean toBool() {
		PSSMessage.Error("RANGEVAL", "IntVal cannot be converted to boolean");
		return false;
	}
}
