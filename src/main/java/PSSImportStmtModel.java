import java.util.*;

public class PSSImportStmtModel extends PSSModel {

	String m_type_id;

	public PSSImportStmtModel(String type_id) {
		super("");

		m_type_id = type_id;
	}

	public void dump (String indent) {
		
		System.out.println(indent + "import " +  m_type_id);

	}
	public PSSModel findDeclaration(String id) {
		PSSModel package_decl = m_parent.findPackageDeclaration(m_type_id);;

		if (package_decl == null) {
			PSSMessage.Error("IMPORT", "cannot not find package '" + m_type_id + "'");
		}

		return package_decl.findDeclaration(id);
	}
}
