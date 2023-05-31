import java.util.*;

public class PSSRepeatProcStmt extends PSSProcStmt {

	public PSSExpression m_count;
	public PSSProcStmt m_stmt;

	public PSSRepeatProcStmt(PSSExpression count, PSSProcStmt stmt) {
		m_count = count;
		m_stmt = stmt;
	}

	public void eval(PSSInst inst) {
		int count = m_count.eval(inst).toInt();

                try {
		for (int i=0; i<count; i++) {
                    try {
			m_stmt.eval(inst);
                    } catch (PSSContinueException dontcare) {
                        // continue
                    }
		}
                } catch (PSSBreakException dontcare) {
                    // break
                }
	}

	public void dump(String indent) {
		String str_count = m_count.getText();

		System.out.println(indent + "repeat (" + str_count + ") {");
		m_stmt.dump(indent + "\t");
		System.out.println(indent + "}");

	}
}
