import java.util.*;

public class PSSAttrField extends PSSModel {

	public boolean m_rand;

	PSSDataDeclModel m_data_decl;

	public PSSAttrField(boolean rand, PSSDataDeclModel data_decl) {
		super("");
		m_rand = rand;
		m_data_decl = data_decl;
	}

	@Override
	public boolean isCompatible(PSSModel model) {
		if (model instanceof PSSAttrField) {
			PSSAttrField m = (PSSAttrField) model;
			return m_data_decl.isCompatible(m.m_data_decl);
		}
		return false;
	}

	public void eval(PSSInst parent_inst) {
		m_data_decl.eval(parent_inst, m_rand);
	}

	public String getText() {
		String result = "";
		if (m_rand) {
			result = result.concat("rand ");
		}
		result = result + m_data_decl.getText();
		return result;
	}

	public void dump (String indent) {
		System.out.println(indent + getText());
	}
}
