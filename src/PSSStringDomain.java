import java.util.*;
import java.util.List;

/**
 * This class provides a domain of strings.
 */
public class PSSStringDomain extends PSSDomain {

	/** domain elements, may be PSSStringVal */
	List<PSSVal> m_list;

	/**
	 * Constructs an empty domain.
	 */
	public PSSStringDomain() {
		m_list = new ArrayList<PSSVal>();
	}

	/**
	 * Constructs an empty domain if {@code bound} is {@false}. Otherwise constructs
	 * a domain initialized with an unbounded string.
	 *
	 * @param bound
	 */
	public PSSStringDomain(boolean bound) {
		m_list = new ArrayList<PSSVal>();
		if (!bound) {
			PSSStringVal str_val = new PSSStringVal("", false);
			m_list.add(str_val);
		}
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
	 * Inserts a value to this domain.
	 *
	 * @param val a value to be inserted
	 */
	public void addVal(PSSVal val) {
		m_list.add(val);
	}

	@Override
	public boolean isEmpty() {
		return m_list.size() == 0;
	}

	public PSSStringDomain join(List<PSSVal> list) {
		PSSStringDomain result = new PSSStringDomain();

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
	public PSSStringDomain join(PSSDomain dom) {
		PSSStringDomain res = null;
		if (dom instanceof PSSStringDomain) {
			List<PSSVal> list = ((PSSStringDomain) dom).getValList();
			res = join(list);
		} else
			PSSMessage.Fatal("It is illegal to join a string domain with a non-string domain.");
		return res;
	}

	public PSSStringDomain union(List<PSSVal> list) {
		PSSStringDomain result = new PSSStringDomain();

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
	public PSSStringDomain union(PSSDomain dom) {
		PSSStringDomain res = null;
		if (dom instanceof PSSStringDomain) {
			List<PSSVal> list = ((PSSStringDomain) dom).getValList();
			res = union(list);
		} else
			PSSMessage.Fatal("It is illegal to union a string domain with a non-string domain.");
		return res;
	}

	@Override
	public PSSStringDomain reduceDomainEqual(PSSVal val) {
		PSSStringDomain result = new PSSStringDomain(val.isBound());

		for (int i = 0; i < m_list.size(); i++) {
			PSSVal item = m_list.get(i);
			PSSVal tmp = item.join(val);
			result.addVal(tmp);
		}

		return result;
	}

	@Override
	public PSSStringDomain reduceDomainNotEqual(PSSVal val) {
		PSSStringDomain result = new PSSStringDomain();

		for (int i = 0; i < m_list.size(); i++) {
			PSSVal item = m_list.get(i);
			if (item.NotEqual(val).toBool()) {
				result.addVal(item);
			}
		}

		return result;
	}

	@Override
	public PSSStringDomain reduceDomainIn(List<PSSVal> list) {
		return join(list);
	}

	@Override
	public PSSStringVal random() {
		if (isEmpty())
			return null;

		int size = m_list.size();
		int randi = PSSRandom.nextInt(0, size - 1);
		PSSVal item = m_list.get(randi);

		if (item.isBound()) {
			return new PSSStringVal(item.toStr());
		} else {
			String str = PSSRandom.nextString(6);	// Use 6-bit chars to reduce the chance of an accidental match on the first random to about 1.9e-9%.
			return new PSSStringVal(str);
		}
	}

	@Override
	public PSSBoolVal InRange(PSSVal lhs) {
		return PSSBoolVal.valueOf(isInside(lhs));
	}

	private boolean isInside(PSSVal lhs) {
		for (int i = 0; i < m_list.size(); i++) {
			PSSVal item = m_list.get(i);
			if (item.isBound()) {
				if (item.Equal(lhs).toBool()) {
					return true;
				}
			} else {
				return true;
			}
		}
		return false;
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
