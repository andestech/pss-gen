
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

public class PSSSetVal extends PSSVal implements PSSICollection {

    Set<PSSVal> m_set = new HashSet<PSSVal>();

    private class SetIterator implements PSSIIterator {

        private Iterator<PSSVal> m_elements;

        public SetIterator(Iterator<PSSVal> i) {
            m_elements = i;
        }

        @Override
        public boolean hasNext() {
            return m_elements.hasNext();
        }

        @Override
        public Entry<PSSVal, PSSVal> next() {
            PSSVal k = null;
            PSSVal v = m_elements.next();
            return new AbstractMap.SimpleEntry<PSSVal, PSSVal>(k, v);
        }

    }

	public PSSSetVal(PSSModel type) {
        super(type);
	}

	public void add(PSSVal item) {
        if (getTypeModel() == null)
            setTypeModel(new PSSSetModel(item.getTypeModel()));
		m_set.add(item);
	}

    public void insert(PSSVal elem) {
        if (getTypeModel() == null)
            setTypeModel(new PSSSetModel(elem.getTypeModel()));
        m_set.add(elem);
    }

    public Set<PSSVal> getSet() {
        return m_set;
    }

	public boolean isEmpty() {
		return (m_set.size() == 0);
	}

    @Override
    public PSSBoolVal Equal(PSSVal rhs) {
        return PSSBoolVal.valueOf(this.equals(rhs));
    }

    @Override
    public PSSBoolVal NotEqual(PSSVal rhs) {
        return this.Equal(rhs).LogicalNot();
    }

    @Override
	public PSSBoolVal InRange(PSSVal lhs) {
        for (PSSVal item: m_set) {
			if (item.InRange(lhs).toBool()) {
				return PSSBoolVal.TRUE;
			}
		}
		return PSSBoolVal.FALSE;
	}

    @Override
    public String getText() {
        List<String> strs = new ArrayList<String>();
        for (PSSVal elem: m_set)
            strs.add(elem.getText());
        return "{ " + String.join(", ", strs) + " }";
    }

    @Override
    public boolean isIndexable() {
        return true;
    }

    @Override
    public PSSIIterator iterator() {
        return new SetIterator(m_set.iterator());
    }

    @Override
    public int compareTo(PSSVal o) {
        if (o instanceof PSSSetVal) {
            PSSSetVal s = (PSSSetVal) o;
            int c = Integer.valueOf(m_set.size()).compareTo(Integer.valueOf(s.m_set.size()));
            if (c != 0)
                return c;
            List<PSSVal> myset = new ArrayList<PSSVal>(m_set);
            List<PSSVal> oset = new ArrayList<PSSVal>(s.m_set);
            Collections.sort(myset);
            Collections.sort(oset);
            for (int i = 0; i < myset.size(); i++) {
                c = myset.get(i).compareTo(oset.get(i));
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
            PSSSetVal s = (PSSSetVal) o;
            return (m_set.size() == s.m_set.size()) && m_set.equals(s.m_set);
        }
        return false;
    }

}
