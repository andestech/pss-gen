import java.util.*;

/**
 * A {@code PSSListVal} is a value_list_literal
 */
public class PSSListVal extends PSSVal {

    private List<PSSVal> m_list = new ArrayList<PSSVal>();

    public PSSListVal() {
    }

    public int size() {
        return m_list.size();
    }

    public int sum() {
        if (!(m_list.get(0) instanceof PSSIntVal))
            PSSMessage.Fatal("Array.sum() is only used on Int type");

        int sum = 0;
        for (PSSVal e: m_list)
            sum += e.toInt();
        return sum;
    }

    public void add(PSSVal elem) {
        m_list.add(elem);
    }

    public List<PSSVal> getValList() {
        return m_list;
    }

    @Override
    public String getText() {
		List<String> strs = new ArrayList<String>();
        for (PSSVal elem: m_list)
            strs.add(elem.getText());
        return "{ " + String.join(", ", strs) + " }";
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
		if (o instanceof PSSListVal) {
			PSSListVal r = (PSSListVal) o;
			return m_list.size() == r.m_list.size() && m_list.containsAll(r.m_list);
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

    public PSSBoolVal InRange(PSSVal lhs) {
        for (int i=0; i<m_list.size(); i++) {
                PSSVal item = m_list.get(i);
                if (item.InRange(lhs).toBool()) {
                        return new PSSBoolVal(true);
                }
        }
        return new PSSBoolVal(false);
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
