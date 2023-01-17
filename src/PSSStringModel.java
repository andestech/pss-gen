import java.util.*;

public class PSSStringModel extends PSSModel {
	public PSSStringModel () {
		super("string");
	}
	public PSSInst declInst(String id, boolean rand) {
		PSSStringInst var = new PSSStringInst(id, rand);
		return var;
	}
}
