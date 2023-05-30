
public class PSSComponentInst extends PSSInst {

	PSSComponentInst(String id, String type_name, PSSModel type_model) {
		super(id, type_name, type_model, false);
	}

	@Override
	public boolean isComponent() {
		return true;
	}

	@Override
	public void init_up() {
		PSSModel comp = getTypeModel();
		for (int i = 0; i < m_insts.size(); i++) {
			PSSInst inst = m_insts.get(i);
			if (inst.isComponent()) {
				inst.init_up();
			}
		}
		comp.init_up(this);
	}

	@Override
	public void init_down() {
		PSSModel comp = getTypeModel();
		comp.init_down(this);
		for (int i = 0; i < m_insts.size(); i++) {
			PSSInst inst = m_insts.get(i);
			if (inst.isComponent()) {
				inst.init_down();
			}
		}
	}

	@Override
	public PSSVal toVal() {
		// A component instance may be evaluated in an expression as a constraint.
		return new PSSRefVal(new PSSRefModel(PSSTypeCategory.COMPONENT), this);
	}

}
