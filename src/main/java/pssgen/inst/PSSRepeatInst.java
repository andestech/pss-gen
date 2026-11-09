package pssgen.inst;

import java.util.*;

public class PSSRepeatInst extends PSSInst {

	public PSSRepeatInst() {
		super("", "", false);
	}

	public void addTraversal(PSSActionInst t) {
		getParent().addTraversal(t);
	}
	public void addSequentialTraversal(PSSActionInst t) {
		getParent().addSequentialTraversal(t);
	}
}

