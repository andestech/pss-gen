import java.util.*;

public class PSSArrayVal extends PSSVal {

    private ArrayList<PSSVal> m_array = new ArrayList<PSSVal>();

    public PSSArrayVal() {
    }

    public int size() {
        return m_array.size();
    }

    public int sum() {
        if (!(m_array.get(0) instanceof PSSIntVal))
            PSSMessage.Fatal("Array.sum() is only used on Int type");

        int sum = 0;
        for (PSSVal e: m_array)
            sum += e.toInt();
        return sum;
    }

    public void addVal(PSSVal elem) {
        m_array.add(elem);
    }

    public void add(PSSVal elem) {
        m_array.add(elem);
    }

    public void insert(PSSVal elem) {
        m_array.add(elem);
    }

    public ArrayList<PSSVal> getArray() {
        return m_array;
    }

    public ArrayList<PSSVal> getValList() {
        return m_array;
    }

    @Override
    public String getText() {
		List<String> strs = new ArrayList<String>();
        for (PSSVal elem: m_array)
            strs.add(elem.getText());
        return "{ " + String.join(", ", strs) + " }";
    }

    @Override
    public int compareTo(PSSVal o) {
        if (o instanceof PSSArrayVal) {
            PSSArrayVal a = (PSSArrayVal) o;
            int c = Integer.valueOf(m_array.size()).compareTo(Integer.valueOf(a.m_array.size()));
            if (c != 0)
                return c;
            List<PSSVal> mylist = new ArrayList<PSSVal>(m_array);
            List<PSSVal> olist = new ArrayList<PSSVal>(a.m_array);
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
		if (o instanceof PSSArrayVal) {
			PSSArrayVal r = (PSSArrayVal) o;
			return m_array.size() == r.m_array.size() && m_array.containsAll(r.m_array);
		}
		return false;
	}

    @Override
    public PSSVal randomIn() {
        int size = m_array.size();
        int randi = PSSRandom.nextInt(0, size - 1);
        PSSVal item = m_array.get(randi);

        if (item.isRangeVal())
            return item.randomIn();
        return item;
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
