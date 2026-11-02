package pssgen.model;

import java.util.*;

import pssgen.inst.PSSInst;
import pssgen.util.PSSMessage;

public class PSSImportStmtModel extends PSSModel {

	String m_type_id;

	/** true for a wildcard import {@code import p::*;} */
	private final boolean m_wildcard;

	/**
	 * Constructs an import statement.
	 *
	 * @param type_id  the imported package
	 * @param wildcard true for {@code import p::*;}
	 * @param alias    the name {@code pp} of {@code import p as pp;}, else ""; a qualified
	 *                 name {@code pp::x} finds this import, which searches the package
	 */
	public PSSImportStmtModel(String type_id, boolean wildcard, String alias) {
		super(alias);

		m_type_id = type_id;
		m_wildcard = wildcard;
	}

	public void dump (String indent) {
		
		System.out.println(indent + "import " +  m_type_id);

	}
	/**
	 * Returns an item of an enum declared in the package that this wildcard import
	 * imports. Imports are not transitive (PSS 2.0 Section 21.1.3), so the package's
	 * own imports are not searched.
	 *
	 * @param id the item name
	 * @return the item, or null
	 */
	public PSSInst findImportedEnumItem(String id) {
		if (!m_wildcard)
			return null;
		PSSModel package_decl = getParent().findPackageDeclaration(m_type_id);
		if (!(package_decl instanceof PSSPackageModel))
			return null;
		for (PSSModel c : package_decl.getChildren()) {
			if (c instanceof PSSEnumModel) {
				PSSInst inst = c.findStaticInst(id);
				if (inst != null)
					return inst;
			}
		}
		return null;
	}

	/** true while a lookup searches the imported package through this import */
	private boolean m_searching;

	/**
	 * Returns the imported package.
	 *
	 * @return the package; an error when there is none
	 */
	public PSSModel findPackage() {
		PSSModel package_decl = getParent().findPackageDeclaration(m_type_id);

		if (package_decl == null) {
			PSSMessage.Error("IMPORT", "cannot not find package '" + m_type_id + "'");
		}
		return package_decl;
	}

	@Override
	public PSSInst findStaticInst(String id) {
		if (getId().isEmpty())
			return super.findStaticInst(id);
		return findPackage().findStaticInst(id);
	}

	public PSSModel findDeclaration(String id) {
		PSSModel package_decl = findPackage();

		// Two packages may import each other; a lookup that comes back to this import
		// has searched the package already.
		if (m_searching)
			return null;
		m_searching = true;
		try {
			return package_decl.findDeclaration(id);
		} finally {
			m_searching = false;
		}
	}
}
