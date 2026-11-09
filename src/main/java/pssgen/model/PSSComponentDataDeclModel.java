package pssgen.model;

import java.util.*;

import pssgen.inst.PSSInst;

public class PSSComponentDataDeclModel extends PSSModel {

	boolean m_static;
	boolean m_const;

	PSSDataDeclModel m_data_decl;

	public PSSComponentDataDeclModel(PSSDataDeclModel data_decl) {
		super("");
		m_data_decl = data_decl;
		m_static = false;
		m_const = false;
	}

	public void declInst(PSSInst parent_inst) {
		m_data_decl.eval(parent_inst, false);
	}
	public void dump(String indent) {
		m_data_decl.dump(indent);
	}
}

