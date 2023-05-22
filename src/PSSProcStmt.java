
public class PSSProcStmt extends PSSModel {

	public PSSProcStmt() {
		super("");
	}

	public void eval(PSSInst inst) throws PSSReturnException {
		PSSMessage.Fatal(getClass().getSimpleName() + "::eval is not implemented");
	}

	public void dump(String indent) {
		System.out.println(indent + "stmt");
	}
}
