import java.util.*;

public class PSSConstraint extends PSSModel {

	public PSSConstraint() {
		super("");
	}

	public PSSDomainMap deduceDomain(PSSInst var) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::deduceDomain is not implemented");
		return null;
	}

	public boolean validate(PSSInst var) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::validate is not implemented");
		return false;
	}

}
