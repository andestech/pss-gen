import java.util.*;

public class PSSScheduleActivity extends PSSActivity {

	ArrayList<PSSActivity> m_list;

	public PSSScheduleActivity() {
		m_list = new ArrayList<PSSActivity>();
	}

	public void addActivity(PSSActivity stmt) {
		m_list.add(stmt);
		stmt.m_parent = this;
	}

	public void evalActivity(PSSInst inst) {
		ArrayList<Integer> schedule = PSSRandom.nextSchedule(m_list.size());

		for (int i=0; i<m_list.size(); i++) {
			int index = schedule.get(i);
			PSSActivity stmt = m_list.get(index);
			stmt.evalActivity(inst);
		}
	}

	public void dump(String indent) {
		System.out.println(indent + "schedule {");
		for (int i=0; i<m_list.size(); i++) {
			PSSActivity stmt = m_list.get(i);
			stmt.dump(indent + "\t");
		}
		System.out.println(indent + "}");
	}
}
