import java.util.*;

public class PSSUniqueConstraint extends PSSConstraint {
	ArrayList<PSSRefPathExpression> m_id_list;

	PSSUniqueConstraint() {
		m_id_list = new ArrayList<PSSRefPathExpression>();
	}

	public void add(String id) {
		PSSRefPathExpression ref = new PSSRefPathExpression(id);
		m_id_list.add(ref);
	}

	public PSSDomainMap deduceDomain(PSSInst var) {
		return null;
	}
	public boolean validate(PSSInst var) {
		for (int i=0; i<m_id_list.size()-1; i++) {
			PSSRefPathExpression id1 = m_id_list.get(i);
			PSSInst var1 = id1.getInst(var);
			PSSVal val1 = var1.toVal();
			for (int j=i+1; j<m_id_list.size(); j++) {
				PSSRefPathExpression id2 = m_id_list.get(j);
				PSSInst var2 = id2.getInst(var);
				PSSVal val2 = var2.toVal();
				if (val1.Equal(val2).toBool()) {
					return false;
				}
			}
		}
		return true;
	}

	public void dump(String indent) {
		System.out.println(indent + "unique");
	}
}
