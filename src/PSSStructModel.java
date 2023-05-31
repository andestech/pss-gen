import java.util.*;

public class PSSStructModel extends PSSModel implements PSSIAggregate {

	PSSStructKind m_struct_kind;
	public ArrayList<PSSAttrField> m_attrfield;
	public PSSConstraintList m_constraint;

	public PSSStructModel(PSSStructKind struct_kind, String id) {
		super(id);
		m_struct_kind = struct_kind;
		m_attrfield = new ArrayList<PSSAttrField>();
		m_constraint = new PSSConstraintList();
	}

	@Override
	public boolean isCompatible(PSSModel model) {
		if (model instanceof PSSDataTypeModel)
			model = ((PSSDataTypeModel) model).resolve();

		return m_hierarchy_id == model.m_hierarchy_id;
	}

	/**
	 * Returns the struct kind of this struct.
	 *
	 * @return the struct kind of this struct
	 */
	public PSSStructKind getStructKind() {
		return m_struct_kind;
	}

	public PSSAttrField addAttrField(PSSAttrField node) {
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
		for (int i = 0; i < m_attrfield.size(); i++) {
			PSSAttrField attrfield = m_attrfield.get(i);
			attrfield.eval(struct_var);
		}
		return struct_var;
	}

	@Override
	public String getText() {
		return m_struct_kind.toString() + " " + m_id + " { "
				+ String.join(" ", m_attrfield.stream().map(a -> a.getText()).toList()) + " }";
	}

	@Override
	public void dump(String indent) {
		System.out.println(indent + m_struct_kind.toString() + " " + m_id + " {");

		for (int i = 0; i < m_attrfield.size(); i++) {
			PSSModel node = m_attrfield.get(i);
			node.dump(indent + "\t");
		}
		System.out.println(indent + "}");
	}

}
