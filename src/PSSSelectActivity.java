import java.util.*;

public class PSSSelectActivity extends PSSActivity {

	ArrayList<PSSSelectBranchActivity> m_list;

	public PSSSelectActivity() {
		m_list = new ArrayList<PSSSelectBranchActivity>();
	}

	public void evalActivity(PSSInst inst) {


		ArrayList<Integer> weight_list = new ArrayList<Integer>();
		int total_weight = 0;

		for (int i=0; i<m_list.size(); i++) {
			PSSSelectBranchActivity branch = m_list.get(i);
			int weight = branch.getWeight(inst);
			total_weight += weight;
			weight_list.add(weight);
		}

		int rand_weight = PSSRandom.nextInt(0, total_weight-1);

		total_weight = 0;

		for (int i=0; i<m_list.size(); i++) {
			int weight = weight_list.get(i);
			total_weight += weight;
			if ((weight != 0) && (rand_weight < total_weight)) {
				PSSSelectBranchActivity branch = m_list.get(i);
				branch.evalActivity(inst);
				break;
			}
		}

	}

	public void addBranch(PSSSelectBranchActivity branch) {
		m_list.add(branch);
		branch.m_parent = this;
	}

	public void dump(String indent) {
		System.out.println(indent + "select {");
		for (int i=0; i<m_list.size(); i++) {
			PSSSelectBranchActivity branch = m_list.get(i);
			branch.dump(indent + "\t");
		}
		System.out.println(indent + "}");

	}
}
