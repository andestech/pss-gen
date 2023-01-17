import java.util.*;

public class PSSSequenceActivity extends PSSActivity {

	ArrayList<PSSActivity> m_list;

	public PSSSequenceActivity() {
		m_list = new ArrayList<PSSActivity>();
	}

	public void addActivity(PSSActivity stmt) {
		m_list.add(stmt);
		stmt.m_parent = this;
	}

	public void evalActivity(PSSInst inst) {
		for (int i=0; i<m_list.size(); i++) {
			PSSActivity stmt = m_list.get(i);
			stmt.evalActivity(inst);
		}
	}

	public void dump(String indent) {
		System.out.println(indent + "{");
		for (int i=0; i<m_list.size(); i++) {
			PSSActivity stmt = m_list.get(i);
			stmt.dump(indent + "\t");
		}
		System.out.println(indent + "}");
	}
}
