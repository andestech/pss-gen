import java.util.*;

public class PSSUniqueConstraint extends PSSConstraint {
	ArrayList<PSSMemberPathElemExpression> m_id_list;

	PSSUniqueConstraint() {
		m_id_list = new ArrayList<PSSMemberPathElemExpression>();
	}

	public void add(PSSMemberPathElemExpression id) {
		m_id_list.add(id);
	}

	public PSSDomainMap deduceDomain(PSSInst var) {
		return null;
	}
	public boolean validate(PSSInst var) {
		for (int i=0; i<m_id_list.size()-1; i++) {
			PSSMemberPathElemExpression id1 = m_id_list.get(i);
			PSSInst var1 = id1.getInst(var);
			PSSVal val1 = var1.toVal();
			for (int j=i+1; j<m_id_list.size(); j++) {
				PSSMemberPathElemExpression id2 = m_id_list.get(j);
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
		System.out.print(indent + "unique {");
		for (PSSMemberPathElemExpression item : m_id_list) {
			System.out.print(item.toString() + ',');
		}
		System.out.println("}");
	}
}
