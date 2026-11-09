package pssgen.inst;

import pssgen.model.PSSModel;

/**
 * This class is used to create a new namespace.
 */
public class PSSNamespaceInst extends PSSInst implements PSSIScope {

	public PSSNamespaceInst(String id) {
		super(id, "", false);
	}

	/**
	 * Constructs the scope of a block whose model tells what it is, e.g. the exec kind
	 * of an exec block.
	 *
	 * @param id         the name of the scope
	 * @param type_decl  the model of the block
	 */
	public PSSNamespaceInst(String id, PSSModel type_decl) {
		super(id, "", type_decl, false);
	}

	@Override
	public void addTraversal(PSSActionInst t) {
		getParent().addTraversal(t);
	}

	@Override
	public void addSequentialTraversal(PSSActionInst t) {
		getParent().addSequentialTraversal(t);
	}

}
