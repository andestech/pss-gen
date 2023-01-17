import java.util.*;

public class PSSDataTypeModel extends PSSModel {
	String m_type;

	public PSSDataTypeModel (String type) {
		super("");
		m_type = type;
	}

	public PSSInst declInst(String id, boolean rand) {
		PSSModel type_decl = findDeclaration(m_type);
		if (type_decl == null) {
			PSSMessage.Error("DataType", "Cannot find type declaration '" + m_type + "'");
		}

		return type_decl.declInst(id, rand);
	}
	public String getText() {
		return m_type;
	}
	public void dump (String indent) {
		System.out.println(indent + m_type);
	}

};
