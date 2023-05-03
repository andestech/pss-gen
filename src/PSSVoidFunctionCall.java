import java.util.*;

public class PSSVoidFunctionCall extends PSSProcStmt {

	PSSFunctionCall func = null;

	PSSVoidFunctionCall(PSSFunctionCall func) {
		this.func = func;
	}

	public void eval(PSSInst var) {
		if (func != null)
			func.eval(var);
	}

}

