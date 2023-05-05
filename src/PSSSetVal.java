import java.util.*;

public class PSSSetVal extends PSSVal {

	ArrayList<PSSVal> m_list;

	public PSSSetVal() {
		m_list = new ArrayList<PSSVal>();
	}

	public void addVal(PSSVal item) {
		m_list.add(item);
	}

	public void clear() {
		m_list.clear();
	}

    public int size() {
        return m_list.size();
    }

    public void delete(PSSVal elem) {
        if (m_list.contains(elem))
            m_list.remove(elem);
        else {
            PSSMessage.Error("PSSSetVal",
                    "The element '" + elem.getText() + "' does not exist.");
        }
    }

    public void insert(PSSVal elem) {
        // If the element already exists, Set.insert() shall have no effect.
        if (!m_list.contains(elem))
            m_list.add(elem);
    }

	public List<PSSVal> getValList() {
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

    @Override
	public PSSVal indexOf(int index) {
		return m_list.get(index);
	}

    @Override
	public PSSVal randomIn() {
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
    public PSSBoolVal Equal(PSSVal rhs) {
        if (rhs instanceof PSSSetVal) {
            PSSSetVal rset = (PSSSetVal) rhs;
            if (m_list.size() == rset.getValList().size()) {
                // TODO: Change "ArrayList<PSSVal>" to "HashSet<PSSVal>"
                for (PSSVal l: m_list) {
                    if (!rset.InRange(l).toBool())
                        return new PSSBoolVal(false);
                }
                return new PSSBoolVal(true);
            }
        }
        return new PSSBoolVal(false);
    }

    @Override
    public PSSBoolVal NotEqual(PSSVal rhs) {
        return Equal(rhs).LogicalNot();
    }

    @Override
	public PSSBoolVal InRange(PSSVal lhs) {
		for (int i = 0; i < m_list.size(); i++) {
			PSSVal item = m_list.get(i);
			if (item.InRange(lhs).toBool()) {
				return new PSSBoolVal(true);
			}
		}
		return new PSSBoolVal(false);
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
		return "[ " + text + " ]";
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

	@Override
	public int compareTo(PSSVal o) {
		if (o instanceof PSSSetVal) {
			PSSSetVal s = (PSSSetVal) o;
			int c = Integer.valueOf(m_list.size()).compareTo(Integer.valueOf(s.m_list.size()));
			if (c != 0)
				return c;
			List<PSSVal> mylist = new ArrayList<PSSVal>(m_list);
			List<PSSVal> olist = new ArrayList<PSSVal>(s.m_list);
			Collections.sort(mylist);
			Collections.sort(olist);
			for (int i = 0; i < mylist.size(); i++) {
				c = mylist.get(i).compareTo(olist.get(i));
				if (c != 0)
					return c;
			}
			return 0;
		}
		return super.compareTo(o);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof PSSSetVal) {
			PSSSetVal r = (PSSSetVal) o;
			/* TODO: not correct if m_list contains duplicate elements (why use list for set?) */
			return m_list.size() == r.m_list.size() && m_list.containsAll(r.m_list);
		}
		return false;
	}

}
