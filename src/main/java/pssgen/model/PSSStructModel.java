package pssgen.model;

import java.util.*;
import java.util.stream.Collectors;

import pssgen.inst.PSSIAggregate;
import pssgen.inst.PSSStructInst;
import pssgen.solver.PSSConstraint;
import pssgen.solver.PSSConstraintList;

public class PSSStructModel extends PSSModel implements PSSIAggregate {

	PSSStructKind m_struct_kind;
	private ArrayList<PSSAttrField> m_attrfield;
	private PSSConstraintList m_constraint;
	private PSSConstraintList m_default_constraint;

	public PSSStructModel(PSSStructKind struct_kind, String id) {
		super(id);
		m_struct_kind = struct_kind;
		m_attrfield = new ArrayList<PSSAttrField>();
		m_constraint = new PSSConstraintList();
		m_default_constraint = new PSSConstraintList();
	}

	@Override
	public boolean isCompatible(PSSModel model) {
		if (model instanceof PSSDataTypeModel)
			model = ((PSSDataTypeModel) model).resolve();

		return getHierarchyId() == model.getHierarchyId();
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
		node.setParent(this);
		return node;
	}

	public PSSConstraint addConstraint(PSSConstraint node) {
		m_constraint.add(node);
		node.setParent(this);
		return node;
	}

	public PSSConstraint addDefaultConstraint(PSSConstraint node) {
		m_default_constraint.add(node);
		node.setParent(this);
		return node;
	}

	public PSSConstraintList getConstraintList() {
		return m_constraint;
	}

	public PSSStructInst declInst(String id, boolean rand) {
		PSSStructInst struct_var = new PSSStructInst(id, getId(), this, rand);
		for (int i = 0; i < m_attrfield.size(); i++) {
			PSSAttrField attrfield = m_attrfield.get(i);
			attrfield.eval(struct_var);
		}
		return struct_var;
	}

	@Override
	public String getText() {
		return m_struct_kind.toString() + " " + getId() + " { "
				+ String.join(" ", m_attrfield.stream().map(a -> a.getText()).collect(Collectors.toList())) + " }";
	}

	@Override
	public void dump(String indent) {
		System.out.println(indent + m_struct_kind.toString() + " " + getId() + " {");

		for (int i = 0; i < m_attrfield.size(); i++) {
			PSSModel node = m_attrfield.get(i);
			node.dump(indent + "\t");
		}
		System.out.println(indent + "}");
	}

	public final ArrayList<PSSAttrField> getAttrfield() {
		return m_attrfield;
	}

	public final PSSConstraintList getConstraint() {
		return m_constraint;
	}

	public final PSSConstraintList getDefaultConstraint() {
		return m_default_constraint;
	}
}
