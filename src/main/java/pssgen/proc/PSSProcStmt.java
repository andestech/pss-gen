package pssgen.proc;

import pssgen.inst.PSSInst;
import pssgen.model.PSSModel;
import pssgen.util.PSSMessage;

public class PSSProcStmt extends PSSModel {

	public PSSProcStmt() {
		super("");
	}

	public void eval(PSSInst inst, PSSExecKind exec_kind) {
		eval(inst);
	}

	public void eval(PSSInst inst) throws PSSReturnException {
		PSSMessage.Fatal(getClass().getSimpleName() + "::eval is not implemented");
	}

	public void dump(String indent) {
		System.out.println(indent + "stmt");
	}

	/**
	 * Reports a break or continue that reached the end of an exec block or a
	 * function body, that is, one that is not inside a loop statement.
	 *
	 * @param keyword "break" or "continue"
	 */
	public static void outsideLoop(String keyword) {
		PSSMessage.Error("PSS 2.0 Section 22.7.11", "'" + keyword
			+ "' may only appear within a loop statement (repeat, repeat-while or foreach)");
	}
}
