package pssgen.activity;

import java.util.*;

import pssgen.expr.PSSExpression;
import pssgen.inst.PSSInst;
import pssgen.inst.PSSIntInst;
import pssgen.inst.PSSRepeatInst;
import pssgen.model.PSSIntModel;
import pssgen.value.PSSIntVal;
import pssgen.value.PSSVal;

public class PSSRepeatActivity extends PSSActivity {

	private PSSExpression m_count;
	private PSSActivity m_stmt;
	private String m_index_id;

	public PSSRepeatActivity(PSSExpression count, PSSActivity stmt, String index_id) {
		m_count = count;
		m_stmt = stmt;
		m_index_id = index_id;
		m_stmt.setParent(this);
	}

	public void evalActivity(PSSInst inst) {
		PSSVal count = m_count.eval(inst);

		if (m_index_id.equals("")) {
			for (int i=0; i<count.toInt(); i++) {
				m_stmt.evalActivity(inst);
			}
		}
		else {
			for (int i=0; i<count.toInt(); i++) {
				// Create a instant for containing index
				PSSRepeatInst repeat_inst = new PSSRepeatInst();
				inst.addInst(repeat_inst);

				// Create a int inst for index
				PSSIntInst index_inst = new PSSIntInst(m_index_id, false, PSSIntModel.DEFAULT_INT_SIZE, true);
				index_inst.assign(new PSSIntVal(i));
				repeat_inst.addInst(index_inst);

				m_stmt.evalActivity(repeat_inst);
			}
		}
	}

	public void dump(String indent) {
		if (m_index_id.equals("")) {
			System.out.println(indent + "repeat (" + m_count.getText() + ") {");
		}
		else {
			System.out.println(indent + "repeat (" + m_index_id + " : " + m_count.getText() + ") {");
		}
		m_stmt.dump(indent + "\t");
		System.out.println(indent + "}");

	}

	public final PSSExpression getCount() {
		return m_count;
	}

	public final PSSActivity getStmt() {
		return m_stmt;
	}

	public final String getIndexId() {
		return m_index_id;
	}
}
