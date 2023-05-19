import java.util.*;

/**
 * This class provides a domain of integers.
 */
public class PSSIntDomain extends PSSDomain {

	/* domain elements, may be a PSSRangeVal or a PSSIntVal */
	List<PSSVal> m_list;

	/**
	 * Constructs an empty domain.
	 */
	public PSSIntDomain() {
		m_list = new ArrayList<PSSVal>();
	}

	/**
	 * Constructs a domain with all integers of a specified width and signedness.
	 *
	 * @param width a bit size
	 * @param sign  a signedness
	 */
	public PSSIntDomain(int width, boolean sign) {
		m_list = new ArrayList<PSSVal>();
		PSSVal range_val = PSSRangeVal.getIntRangeVal(width, sign);
		m_list.add(range_val);
	}

	/**
	 * Constructs a domain with a single element.
	 *
	 * @param val a value
	 */
	public PSSIntDomain(PSSVal val) {
		m_list = new ArrayList<PSSVal>();
		m_list.add(val);
	}

	@Override
	public boolean isEmpty() {
		return m_list.size() == 0;
	}

	/**
	 * Inserts a value to this domain.
	 *
	 * @param item a value to be inserted
	 */
	public void addVal(PSSVal item) {
		m_list.add(item);
	}

	/**
	 * Returns the domain elements.
	 *
	 * @return the domain elements
	 */
	public List<PSSVal> getValList() {
		return m_list;
	}

	/**
	 * Returns the minimal element in this domain.
	 *
	 * @return the minimal element in this domain, or {@code null} if this domain is
	 *         empty
	 */
	public PSSVal getMin() {
		PSSVal min = null;
		for (int i = 0; i < m_list.size(); i++) {
			PSSVal item = m_list.get(i);
			if (min == null) {
				if (item.isRangeVal()) {
					min = item.getMin();
				} else {
					min = item;
				}
			} else {
				if (item.LessThan(min).toBool()) {
					min = item;
				}
			}
		}
		return min;
	}

	/**
	 * Returns the maximal element in this domain.
	 *
	 * @return the maximal element in this domain, or {@code null} if this domain is
	 *         empty
	 */
	public PSSVal getMax() {
		PSSVal max = null;
		for (int i = 0; i < m_list.size(); i++) {
			PSSVal item = m_list.get(i);
			if (max == null) {
				if (item.isRangeVal()) {
					max = item.getMax();
				} else {
					max = item;
				}
			} else {
				if (item.GreaterThan(max).toBool()) {
					max = item;
				}
			}
		}
		return max;
	}

	public PSSIntDomain join(List<PSSVal> list) {
		PSSIntDomain result = new PSSIntDomain();

		for (int i = 0; i < list.size(); i++) {
			PSSVal item = list.get(i);
			for (int j = 0; j < m_list.size(); j++) {
				PSSVal item2 = m_list.get(j);
				PSSVal join_val = item.join(item2);
				if (join_val != null) {
					result.addVal(join_val);
				}
			}
		}
		return result;
	}

	@Override
	public PSSIntDomain join(PSSDomain dom) {
		List<PSSVal> list = ((PSSIntDomain) dom).getValList();
		return join(list);
	}

	@Override
	public PSSIntDomain union(PSSDomain dom) {
		List<PSSVal> list = ((PSSIntDomain) dom).getValList();
		return union(list);
	}

	public PSSIntDomain union(List<PSSVal> list) {
		PSSIntDomain result = new PSSIntDomain();

		for (int i = 0; i < list.size(); i++) {
			PSSVal item = list.get(i);
			result.addVal(item);
		}
		for (int j = 0; j < m_list.size(); j++) {
			PSSVal item2 = m_list.get(j);
			result.addVal(item2);
		}
		return result;
	}

	@Override
	public PSSIntDomain reduceDomainEqual(PSSVal val) {
		if (InRange(val).toBool()) {
			return new PSSIntDomain(val);
		} else {
			return new PSSIntDomain();
		}
	}

	@Override
	public PSSIntDomain reduceDomainLessEqual(PSSVal val) {
		PSSIntDomain result = new PSSIntDomain();

		for (int i = 0; i < m_list.size(); i++) {
			PSSVal item = m_list.get(i);
			PSSVal tmp_item = item.reduceLessEqual(val);
			if (tmp_item != null) {
				result.addVal(tmp_item);
			}
		}
		return result;
	}

	@Override
	public PSSIntDomain reduceDomainLessThan(PSSVal val) {
		/* TODO: will this subtraction underflow? */
		PSSVal tmp_val = val.Sub(PSSIntVal.ONE());
		return reduceDomainLessEqual(tmp_val);
	}

	@Override
	public PSSIntDomain reduceDomainGreaterThan(PSSVal val) {
		/* TODO: will this addition overflow? */
		PSSVal tmp_val = val.Add(PSSIntVal.ONE());
		return reduceDomainGreaterEqual(tmp_val);
	}

	@Override
	public PSSIntDomain reduceDomainGreaterEqual(PSSVal val) {
		PSSIntDomain result = new PSSIntDomain();

		for (int i = 0; i < m_list.size(); i++) {
			PSSVal item = m_list.get(i);
			PSSVal tmp_item = item.reduceGreaterEqual(val);
			if (tmp_item != null) {
				result.addVal(tmp_item);
			}
		}
		return result;
	}

	@Override
	public PSSIntDomain reduceDomainNotEqual(PSSVal val) {
		PSSIntDomain result = new PSSIntDomain();

		for (int i = 0; i < m_list.size(); i++) {
			PSSVal item = m_list.get(i);
			if (item.isRangeVal()) {
				PSSVal min = item.getMin();
				PSSVal max = item.getMax();
				if (min.Equal(val).toBool()) {
					min = min.Add(PSSIntVal.ONE());
					result.addVal(new PSSRangeVal(min.toBigInteger(), max.toBigInteger()));
				} else if (max.Equal(val).toBool()) {
					max = max.Sub(PSSIntVal.ONE());
					result.addVal(new PSSRangeVal(min.toBigInteger(), max.toBigInteger()));
				} else if (item.InRange(val).toBool()) {
					PSSVal tmp1 = val.Sub(PSSIntVal.ONE());
					PSSVal tmp2 = val.Add(PSSIntVal.ONE());
					result.addVal(new PSSRangeVal(min.toBigInteger(), tmp1.toBigInteger()));
					result.addVal(new PSSRangeVal(tmp2.toBigInteger(), max.toBigInteger()));
				} else {
					result.addVal(item);
				}
			} else {
				if (item.NotEqual(val).toBool()) {
					result.addVal(item);
				}
			}
		}
		return result;
	}

	@Override
	public PSSIntDomain reduceDomainIn(PSSListVal val) {
		List<PSSVal> list = val.getValList();
		return join(list);
	}

	@Override
	public PSSVal random() {
		if (isEmpty())
			return null;

		int size = m_list.size();
		int randi = PSSRandom.nextInt(0, size - 1);

		PSSVal item = m_list.get(randi);

		if (item.isRangeVal()) {
			return item.randomIn();
		} else {
			return item;
		}
	}

	@Override
	public PSSBoolVal InRange(PSSVal lhs) {
		for (int i = 0; i < m_list.size(); i++) {
			PSSVal item = m_list.get(i);
			if (item.InRange(lhs).toBool()) {
				return PSSBoolVal.TRUE;
			}
		}
		return PSSBoolVal.FALSE;
	}

	@Override
	public String getText() {
		String text = "";

		for (int i = 0; i < m_list.size(); i++) {
			PSSVal item = m_list.get(i);

			if (text.length() == 0) {
				text = text.concat(item.getText());
			} else {
				text = text.concat(", " + item.getText());
			}
		}
		return "{" + text + "}";
	}

}
