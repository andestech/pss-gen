import java.util.*;

public class PSSTraversal {
	PSSActionInst		m_inst;
	ArrayList<PSSTraversal>	m_dependency;

	PSSTraversal(PSSActionInst inst) {
		m_inst = inst;
		m_dependency = new ArrayList<PSSTraversal>();
	}

	public void addDependency(PSSTraversal t) {
		m_dependency.add(t);
	}
	public PSSActionInst getInst() {
		return m_inst;
	}

	public int getDependencySize() {
		return m_dependency.size();
	}
	public PSSTraversal getDependencyTraversal(int i) {
		return m_dependency.get(i);
	}
}
