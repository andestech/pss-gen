package pssgen.model;

import java.util.*;

import pssgen.inst.PSSInst;
import pssgen.inst.PSSStaticConstInst;

public class PSSDataDeclModel extends PSSModel {

	private PSSModel m_type;
	PSSDataInstListModel m_inst_list;

	public PSSDataDeclModel(PSSModel type) {
		super("");
		m_type = type;
		m_inst_list = new PSSDataInstListModel();
	}

	@Override
	public boolean isCompatible(PSSModel model) {
		if (model instanceof PSSDataDeclModel) {
			PSSDataDeclModel m = (PSSDataDeclModel) model;
			return m_type.isCompatible(m.m_type);
		}
		return false;
	}

	public void addDataInst(PSSDataInstModel data_inst) {
		m_inst_list.add(data_inst);
	}

	public void eval(PSSInst parent_inst, boolean rand) {
		for (int i=0; i<m_inst_list.size(); i++) {
			PSSDataInstModel d_inst = m_inst_list.get(i);
			d_inst.eval(parent_inst, m_type, rand);
		}
	}

	/**
	 * Declares the fields of this declaration as static constants of a type or package
	 * (PSS 2.0 Sections 8.1.1, 21.1.1.1 b)), found by name through the static members of
	 * scope.
	 *
	 * @param scope the type or package that declares the constants
	 */
	public void declStatic(PSSModel scope) {
		for (int i=0; i<m_inst_list.size(); i++)
			scope.addStaticInst(new PSSStaticConstInst(scope, m_inst_list.get(i), m_type));
	}

	public String getText() {
		String result = m_type.getText() + " " + m_inst_list.getText();;
		return result;
	}

	public void dump (String indent) {
		System.out.println(indent + getText());
	}

	public final PSSModel getType() {
		return m_type;
	}
}
