package pssgen.inst;

/**
 * This class is used to create a new namespace.
 */
public class PSSNamespaceInst extends PSSInst implements PSSIScope {

	public PSSNamespaceInst(String id) {
		super(id, "", false);
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
