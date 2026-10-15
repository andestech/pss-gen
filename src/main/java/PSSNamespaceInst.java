
/**
 * This class is used to create a new namespace.
 */
public class PSSNamespaceInst extends PSSInst implements PSSIScope {

	public PSSNamespaceInst(String id) {
		super(id, "", false);
	}

	@Override
	public void addTraversal(PSSActionInst t) {
		m_parent.addTraversal(t);
	}

	@Override
	public void addSequentialTraversal(PSSActionInst t) {
		m_parent.addSequentialTraversal(t);
	}

}
