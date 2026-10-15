import java.util.*;

public class PSSActivity extends PSSModel {

	public PSSActivity() {
		super("");
	}

	public void evalActivity(PSSInst inst) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::evalActivity is not implemented");
	}

	public void dump(String indent) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::dump is not implemented");
	}
}
