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

}
