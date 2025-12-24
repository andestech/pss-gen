import java.util.*;

public class PSSStructInst extends PSSInst implements PSSIAggregate {

	public PSSStructInst(String id, String type, PSSStructModel type_decl, boolean rand) {
		super(id, type, type_decl, rand);
	}

	@Override
	public PSSStructModel getTypeModel() {
		return (PSSStructModel) super.getTypeModel();
	}

	public void randomize() {
		if (m_rand) {
			for (int i = 0; i < m_insts.size(); i++) {
				PSSInst inst = m_insts.get(i);
				inst.randomize();
			}
		}
	}

	@Override
	public void assign(PSSVal val) {
		if (!(val instanceof PSSRefVal)) {
			PSSMessage.Fatal("struct should be assigned by struct-type: < "
				+ val.getClass().getSimpleName() + " > = " + val.getText());
		}

		PSSInst val_inst = ((PSSRefVal)val).getInst();
		if (!(val_inst instanceof PSSStructInst)) {
			PSSMessage.Fatal("struct should be assigned by struct-type: < "
				+ val_inst.getClass().getSimpleName() + " > = ");
			val_inst.dump("");
		}

		// Assign elements one-by-one
		for (PSSInst val_elem : val_inst.getDescendants()) {
			// Truncate hierarchy path
			String[] elem_hierarchyId = val_elem.getHierarchyId().split("\\.");
			String elem_id = elem_hierarchyId[elem_hierarchyId.length-1];

			PSSInst target_elem = this.findChild(elem_id);
			if (target_elem != null) {
				target_elem.assign(val_elem.toVal());
			}
		}
	}

	@Override
	public PSSRefVal toVal() {
		return new PSSRefVal(new PSSRefModel(PSSTypeCategory.STRUCT), this);
	}

}
