import java.util.*;

public class PSSStructInst extends PSSInst implements PSSIAggregate {


	public PSSStructInst(String id, String type, PSSModel type_decl, boolean rand) {
		super(id, type, type_decl, rand);
	}

	public void randomize() {
		if (m_rand) {
			for (int i=0; i<m_insts.size(); i++) {
				PSSInst inst = m_insts.get(i);
				inst.randomize();
			}
		}
	}
}
