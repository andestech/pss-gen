package pssgen.model;

import java.util.*;

public class PSSPackageModel extends PSSModel {

	public PSSPackageModel(String id) {
		super(id);
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
