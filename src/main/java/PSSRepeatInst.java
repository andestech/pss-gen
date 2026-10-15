import java.util.*;

public class PSSRepeatInst extends PSSInst {

	PSSRepeatInst() {
		super("", "", false);
	}

	public void addTraversal(PSSActionInst t) {
		m_parent.addTraversal(t);
	}
	public void addSequentialTraversal(PSSActionInst t) {
		m_parent.addSequentialTraversal(t);
	}
}

