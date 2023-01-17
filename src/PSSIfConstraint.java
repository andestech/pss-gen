import java.util.*;

public class PSSIfConstraint extends PSSConstraint {
	PSSExpression m_cond;
	ArrayList<PSSConstraint> m_true;
	ArrayList<PSSConstraint> m_false;

	PSSIfConstraint(PSSExpression expression) {
		m_cond = expression;
		m_true = new ArrayList<PSSConstraint>();
		m_false = new ArrayList<PSSConstraint>();
	}
	public void addTrueConstraint(PSSConstraint node) {
		m_true.add(node);
		node.m_parent = this;
	}
	public void addFalseConstraint(PSSConstraint node) {
		m_false.add(node);
		node.m_parent = this;
	}

	public PSSDomainMap deduceDomainList(ArrayList<PSSConstraint> list, PSSInst var) {
		PSSDomainMap result = new PSSDomainMap();
		for (int i=0; i<list.size(); i++) {
			PSSConstraint c = list.get(i);
			PSSDomainMap tmp = c.deduceDomain(var);
			if (tmp != null) {
				result = result.join(tmp);
			}
		}
		return result;
	}

	public PSSDomainMap deduceDomain(PSSInst var) {
		PSSDomainMap true_map = deduceDomainList(m_true, var);
		PSSDomainMap false_map = deduceDomainList(m_false, var);

		return true_map.union(false_map);
	}
	private boolean validate_list(PSSInst var, ArrayList<PSSConstraint> list) {
		for (int i=0; i<list.size(); i++) {
			PSSConstraint c = list.get(i);
			if (c.validate(var) == false) {
				return false;
			}
		}
		return true;
	}

	public boolean validate(PSSInst var) {
		PSSVal cond = m_cond.eval(var);
		
		if (cond.toBool()) {
			return validate_list(var, m_true);
		}
		else {
			return validate_list(var, m_false);
		}
	}
	public String getText() {
		return new String("if (" + m_cond.getText() + ")");
	}

	public void dump(String indent) {
		System.out.println(indent + "if (" + m_cond.getText() + ")");
	}
}
