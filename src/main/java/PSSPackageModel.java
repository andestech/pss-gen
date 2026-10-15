import java.util.*;

public class PSSPackageModel extends PSSModel {

	public PSSPackageModel(String id) {
		super(id);
	}

	public void dump (String indent) {
		
		System.out.println(indent + "package " +  m_id + "{");

		for (int i=0; i<children.size(); i++) {
			PSSModel node = children.get(i);
			node.dump(indent + "\t");
		}
		System.out.println(indent + "}");
	}
}
