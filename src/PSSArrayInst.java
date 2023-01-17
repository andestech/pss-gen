import java.util.*;

public class PSSArrayInst extends PSSInst {
	ArrayList<PSSInst> m_array_elems;

	PSSArrayInst(String id, PSSModel type_model, int dim, boolean rand) {
		super(id, "", false);
		m_array_elems = new ArrayList<PSSInst>();
		for (int i=0; i<dim; i++) {
			PSSInst inst = type_model.declInst(id + "[" + i + "]", rand);
			m_array_elems.add(inst);
		}
	}

	public void assign(PSSVal val) {
		for (int i=0; i<m_array_elems.size(); i++) {
			PSSInst item = m_array_elems.get(i);
			PSSVal item_val = val.indexOf(i);
			item.assign(item_val);
		}
	}

	public void randomize() {
		for (int i=0; i<m_array_elems.size(); i++) {
			PSSInst item = m_array_elems.get(i);
			item.randomize();
		}
	}

	public PSSInst indexOf(int index) {
		return m_array_elems.get(index);
	}
};

