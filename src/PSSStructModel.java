import java.util.*;

public class PSSStructModel extends PSSModel {
	public ArrayList<PSSAttrField> m_attrfield;
	public PSSConstraintList m_constraint;

	public PSSStructModel (String id) {
		super(id);
		m_attrfield = new ArrayList<PSSAttrField>();
		m_constraint = new PSSConstraintList();
	}
	public PSSAttrField addAttrField (PSSAttrField node) {
		m_attrfield.add(node);
		node.m_parent = this;
		return node;
	}
	public PSSConstraint addConstraint(PSSConstraint node) {
		m_constraint.add(node);
		node.m_parent = this;
		return node;
	}
	public PSSConstraintList getConstraintList() {
		return m_constraint;
	}
	public PSSStructInst declInst(String id, boolean rand) {
		PSSStructInst struct_var = new PSSStructInst(id, m_id, this, rand);
		for (int i=0; i<m_attrfield.size(); i++) {
			PSSAttrField attrfield = m_attrfield.get(i);
			attrfield.eval(struct_var);
		}
		return struct_var;
	}

	public void dump (String indent) {
		
		System.out.println(indent + "struct " +  m_id + " {");

		for (int i=0; i<m_attrfield.size(); i++) {
			PSSModel node = m_attrfield.get(i);
			node.dump(indent + "\t");
		}
		System.out.println(indent + "}");
	}
}
