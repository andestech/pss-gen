import java.util.*;

public class PSSExpression {

	public PSSExpression() {
		;
	}

	public PSSDomainMap deduceDomain(PSSInst var) {
		PSSMessage.Fatal(getClass().getSimpleName() + ".deduceDomain() is not implemented");
		return null;
	}

	public PSSVal eval(PSSInst var) {
		PSSMessage.Fatal(getClass().getSimpleName() + ".eval() is not implemented");
		return null;
	}
	public PSSInst getInst(PSSInst var) {
		return null;
	}

	public String getText() {
		return "";
	}
}
