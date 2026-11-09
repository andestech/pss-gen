package pssgen.solver;

import java.util.*;

import pssgen.inst.PSSInst;
import pssgen.model.PSSModel;

public abstract class PSSConstraint extends PSSModel {

	public PSSConstraint() {
		super("");
	}

	public abstract PSSDomainMap deduceDomain(PSSInst var);

	public abstract boolean validate(PSSInst var);

	/**
	 * Returns true if this constraint refers to search_inst, resolving its names from
	 * parent_inst. Only an expression constraint is searched: the branches of an if
	 * constraint and the body of a foreach constraint are not.
	 */
	boolean refersTo(PSSInst parent_inst, PSSInst search_inst) {
		return false;
	}

}
