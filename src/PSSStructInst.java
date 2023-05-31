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
	public PSSRefVal toVal() {
		return new PSSRefVal(new PSSRefModel(PSSTypeCategory.STRUCT), this);
	}

}
