package pssgen.model;

import java.util.*;

import pssgen.inst.PSSInst;

public class PSSPackageModel extends PSSModel {

	public PSSPackageModel(String id) {
		super(id);
	}

	/**
	 * Declares the items of the enums of this package, so that a reference to them
	 * from a component outside the package finds them (PSS 2.0 Section 21.1.3).
	 */
	@Override
	public void declEnumItem(PSSInst inst) {
		for (PSSModel child : getChildren())
			child.declEnumItem(inst);
	}

	public void dump (String indent) {
		
		System.out.println(indent + "package " +  getId() + "{");

		for (int i=0; i<getChildren().size(); i++) {
			PSSModel node = getChildren().get(i);
			node.dump(indent + "\t");
		}
		System.out.println(indent + "}");
	}
}
