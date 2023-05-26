import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.List;
import java.util.Map.Entry;

/**
 * A {@code PSSListVal} object is a value_list_literal or the evaluation of an
 * open_range_list. If it is an open_range_list, an element in it may be of type
 * {@link PSSRangeVal} and the two methods {@link #getValList()} and
 * {@link #InRange(PSSVal)} will be used.
 */
public class PSSListVal extends PSSVal implements PSSICollection {

    private List<PSSVal> m_list = new ArrayList<PSSVal>();

    private class PSSListIterator implements PSSIIterator {

        private ListIterator<PSSVal> m_elements;

        public PSSListIterator(ListIterator<PSSVal> li) {
            m_elements = li;
        }

        @Override
        public boolean hasNext() {
            return m_elements.hasNext();
        }

        @Override
        public Entry<PSSVal, PSSVal> next() {
            PSSVal k = new PSSIntVal(m_elements.nextIndex());
            PSSVal v = m_elements.next();
            return new AbstractMap.SimpleEntry<PSSVal, PSSVal>(k, v);
        }

    }

    /**
     * Constructs an empty list of a specified list data type.
     *
     * @param type the data type of this list
     */
    public PSSListVal(PSSListModel type) {
        super(type);
    }

    /**
     * Constructs an empty list. The list data type is determined when the first
     * element with a determined data type is inserted.
     */
    public PSSListVal() {
        this(null);
    }

    /**
     * Returns an empty list with elements of a specified type.
     *
     * @param elem_data_type the data type of list elements
     * @return
     */
    public static PSSListVal ofElementDataType(PSSModel elem_data_type) {
        return new PSSListVal(new PSSListModel(elem_data_type));
    }

    @Override
    public PSSListModel getTypeModel() {
        return (PSSListModel) super.getTypeModel();
    }

    /**
     * Returns the type of list elements.
     *
     * @return the type of list elements
     */
    public PSSModel getElementTypeModel() {
        PSSListModel m = getTypeModel();
        return m == null ? null : m.getElementTypeModel();
    }

    /**
     * Returns the size of this list.
     *
     * @return the size of this list
     */
    public int size() {
        return m_list.size();
    }

    /**
     * Returns {@code true} if this list is empty.
     *
     * @return {@code true} if this list is empty
     */
    public boolean isEmpty() {
        return m_list.isEmpty();
    }

    /**
     * Adds an element to this list.
     *
     * @param elem an element to be added.
     */
    public void add(PSSVal elem) {
        if (getTypeModel() == null && elem.getTypeModel() != null)
            setTypeModel(new PSSListModel(elem.getTypeModel()));
        m_list.add(elem);
    }

    /**
     * Returns the list elements.
     *
     * @return the list elements
     */
    public List<PSSVal> getValList() {
        return m_list;
    }

    @Override
    public String getText() {
        List<String> strs = new ArrayList<String>();
        for (PSSVal elem : m_list)
            strs.add(elem.getText());
        return "{ " + String.join(", ", strs) + " }";
    }

    @Override
    public PSSBoolVal Equal(PSSVal v) {
        return PSSBoolVal.valueOf(this.equals(v));
    }

    @Override
    public PSSBoolVal NotEqual(PSSVal v) {
        return this.Equal(v).LogicalNot();
    }

    @Override
    public boolean isIndexable() {
        return true;
    }

    @Override
    public PSSIIterator iterator() {
        return new PSSListIterator(m_list.listIterator());
    }

    @Override
    public int compareTo(PSSVal o) {
        if (o instanceof PSSListVal) {
            PSSListVal a = (PSSListVal) o;
            int c = Integer.valueOf(m_list.size()).compareTo(Integer.valueOf(a.m_list.size()));
            if (c != 0)
                return c;
            List<PSSVal> mylist = new ArrayList<PSSVal>(m_list);
            List<PSSVal> olist = new ArrayList<PSSVal>(a.m_list);
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
        if (o instanceof PSSListVal) {
            PSSListVal r = (PSSListVal) o;
            return m_list.size() == r.m_list.size() && m_list.equals(r.m_list);
        }
        return false;
    }

    @Override
    public PSSVal randomIn() {
        int size = m_list.size();
        int randi = PSSRandom.nextInt(0, size - 1);
        PSSVal item = m_list.get(randi);

        if (item.isRangeVal())
            return item.randomIn();
        return item;
    }

    @Override
    public PSSBoolVal InRange(PSSVal lhs) {
        for (int i = 0; i < m_list.size(); i++) {
            PSSVal item = m_list.get(i);
            PSSBoolVal b = null;
            if (item instanceof PSSRangeVal)
                b = ((PSSRangeVal) item).InRange(lhs);
            else
                b = item.Equal(lhs);
            if (b.toBool())
                return b;
        }
        return PSSBoolVal.FALSE;
    }

    @Override
    public PSSVal indexOf(PSSVal index) {
        return m_list.get(index.toInt());
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
