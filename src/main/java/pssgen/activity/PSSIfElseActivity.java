package pssgen.activity;

import java.util.*;

import pssgen.expr.PSSExpression;
import pssgen.inst.PSSInst;
import pssgen.util.PSSMessage;
import pssgen.value.PSSBoolVal;
import pssgen.value.PSSVal;

public class PSSIfElseActivity extends PSSActivity {

	private PSSExpression m_cond;
	private PSSActivity m_true_stmt;
	private PSSActivity m_false_stmt;

	public PSSIfElseActivity(PSSExpression cond) {
		m_cond = cond;
		m_true_stmt = null;
		m_false_stmt = null;
	}

	public void addTrueStmt(PSSActivity stmt) {
		m_true_stmt = stmt;
		m_true_stmt.setParent(this);
	}
	public void addFalseStmt(PSSActivity stmt) {
		m_false_stmt = stmt;
		m_false_stmt.setParent(this);
	}

	public void evalActivity(PSSInst inst) {
		PSSVal cond = m_cond.eval(inst);
		// PSS 2.0 Section 13.4.5 a): "expression shall be of type bool."
		if (!(cond instanceof PSSBoolVal))
			PSSMessage.Error("PSS 2.0 Section 13.4.5", "the condition '" + m_cond.getText() + "' of an activity if shall be of type bool");

		if (cond.toBool()) {
			m_true_stmt.evalActivity(inst);
		}
		else {
			if (m_false_stmt != null) {
				m_false_stmt.evalActivity(inst);
			}
		}
	}

	public void dump(String indent) {
		String str_cond = m_cond.getText();

		System.out.println(indent + "if (" + str_cond + ") {");
		m_true_stmt.dump(indent + "\t");
		System.out.println(indent + "}");

		if (m_false_stmt != null) {
			System.out.println(indent + "else {");
			m_false_stmt.dump(indent + "\t");
			System.out.println(indent + "}");
		}
	}

	public final PSSExpression getCond() {
		return m_cond;
	}

	public final PSSActivity getTrueStmt() {
		return m_true_stmt;
	}

	public final PSSActivity getFalseStmt() {
		return m_false_stmt;
	}
}
