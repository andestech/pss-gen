import java.util.*;

public class PSSComponentInst extends PSSInst {
	PSSComponentInst(String id, String type_name, PSSModel type_model) {
		super (id, type_name, type_model, false);
	}

	public boolean isComponent() {
		return true;
	}
	public void init_up() {
		PSSModel comp = getTypeModel();
		for (int i=0; i<m_insts.size(); i++) {
			PSSInst inst = m_insts.get(i);
			if (inst.isComponent()) {
				inst.init_up();
			}
		}
		comp.init_up(this);
	}
	public void init_down() {
		PSSModel comp = getTypeModel();
		comp.init_down(this);
		for (int i=0; i<m_insts.size(); i++) {
			PSSInst inst = m_insts.get(i);
			if (inst.isComponent()) {
				inst.init_down();
			}
		}
	}
}
