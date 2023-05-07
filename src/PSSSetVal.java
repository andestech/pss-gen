
// Copyright (C) 2021, Andes Technology Corp. Confidential Proprietary
import java.util.*;

public class PSSSetVal extends PSSVal {

    Set<PSSVal> m_set = new HashSet<PSSVal>();

	public PSSSetVal() {
	}

	public void add(PSSVal item) {
		m_set.add(item);
	}

	public void clear() {
		m_set.clear();
	}

    public int size() {
        return m_set.size();
    }

    public void delete(PSSVal elem) {
        if (m_set.contains(elem))
            m_set.remove(elem);
        else
            PSSMessage.Error("PSSSetVal",
                    "The element '" + elem.getText() + "' does not exist.");
    }

    public void insert(PSSVal elem) {
        m_set.add(elem);
    }

    public Set<PSSVal> getSet() {
        return m_set;
    }

	public boolean isEmpty() {
		return (m_set.size() == 0);
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
	public PSSVal indexOf(PSSVal index) {
        PSSMessage.Fatal("Set has no index operator");
        return null;
	}

    @Override
    public PSSBoolVal Equal(PSSVal rhs) {
        if (rhs instanceof PSSSetVal) {
            PSSSetVal rset = (PSSSetVal) rhs;
            if (m_set.size() == rset.getSet().size()) {
                for (PSSVal l: m_set) {
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
        for (PSSVal item: m_set) {
			if (item.InRange(lhs).toBool()) {
				return new PSSBoolVal(true);
			}
		}
		return new PSSBoolVal(false);
	}

    @Override
	public String getText() {
		String text = "";

        for (PSSVal item: m_set) {
			if (text.length() == 0) {
				text = text.concat(item.getText());
			} else {
				text = text.concat(", " + item.getText());
			}
		}
		return "[ " + text + " ]";
	}

}
