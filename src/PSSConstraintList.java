import java.util.*;

public class PSSConstraintList {
	ArrayList<PSSConstraint> m_list;

	PSSConstraintList() {
		m_list = new ArrayList<PSSConstraint>();
	}

	void add(PSSConstraint constraint) {
		m_list.add( constraint);
	}

	int size() {
		return m_list.size();
	}

	PSSConstraint get(int i) {
		return m_list.get(i);
	}

	public boolean validate(PSSInst inst) {
		for (int i=0; i<m_list.size(); i++) {
			PSSConstraint c = m_list.get(i);
			if (!c.validate(inst)) {
				return false;
			}
		}
		return true;
	}

	public void dump (String indent) {
		for (int i=0; i<m_list.size(); i++) {
			PSSConstraint c = m_list.get(i);
			c.dump(indent);
		}
	}
}

