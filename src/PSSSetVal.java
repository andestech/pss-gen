
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

/**
 * A {@code PSSSetVal} is a set of values.
 */
public class PSSSetVal extends PSSVal implements PSSICollection {

    /** set elements */
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

    /**
     * Constructs this empty set.
     *
     * @param type the data type of this set.
     */
    public PSSSetVal(PSSSetModel type) {
        super(type);
    }

    /**
     * Constructs this empty set with undetermined set element type. The type will
     * be determined when the first element with a determined type is inserted to
     * this set.
     */
    public PSSSetVal() {
        super(null);
    }

    /**
     * Returns an empty set with elements of a specified type.
     *
     * @param elem_data_type the type of set elements
     * @return an empty set
     */
    public static PSSSetVal ofElementDataType(PSSModel elem_data_type) {
        return new PSSSetVal(new PSSSetModel(elem_data_type));
    }

    @Override
    public PSSSetModel getTypeModel() {
        return (PSSSetModel) super.getTypeModel();
    }

    /**
     * Returns the type of list elements.
     *
     * @return the type of list elements
     */
    public PSSModel getElementTypeModel() {
        PSSSetModel m = getTypeModel();
        return m == null ? null : m.getElementTypeModel();
    }

    /**
     * Adds an element to this set.
     *
     * @param item an element to be added
     */
    public void insert(PSSVal elem) {
        if (getTypeModel() == null && elem.getTypeModel() != null)
            setTypeModel(new PSSSetModel(elem.getTypeModel()));
        m_set.add(elem);
    }

    /**
     * Returns the set elements.
     *
     * @return the set elements
     */
    public Set<PSSVal> getSet() {
        return m_set;
    }

    /**
     * Returns {@code true} if this set is empty.
     *
     * @return {@code true} if this set is empty
     */
    public boolean isEmpty() {
        return (m_set.size() == 0);
    }

    @Override
    public PSSBoolVal Equal(PSSVal rhs) {
        if (rhs instanceof PSSListVal) {
            return PSSBoolVal.valueOf(isEmpty() && ((PSSListVal) rhs).isEmpty());
        } else if (rhs instanceof PSSSetVal) {
            PSSSetVal s = (PSSSetVal) rhs;
            return PSSBoolVal.valueOf((m_set.size() == s.m_set.size()) && m_set.equals(s.m_set));
        }
        return PSSBoolVal.FALSE;
    }

    @Override
    public PSSBoolVal NotEqual(PSSVal rhs) {
        return this.Equal(rhs).LogicalNot();
    }

    @Override
    public PSSBoolVal InRange(PSSVal lhs) {
        for (PSSVal item : m_set) {
            if (item.InRange(lhs).toBool()) {
                return PSSBoolVal.TRUE;
            }
        }
        return PSSBoolVal.FALSE;
    }

    @Override
    public String getText() {
        List<String> strs = new ArrayList<String>();
        for (PSSVal elem : m_set)
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
