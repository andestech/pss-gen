import java.util.*;

public class PSSBoolModel extends PSSModel {
	public PSSBoolModel () {
		super("bool");
	}

	public PSSInst declInst(String id, boolean rand) {
		PSSBoolInst var = new PSSBoolInst(id, rand);
		return var;
	}
}
