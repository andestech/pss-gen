import java.util.*;

public class PSSRepeatActivity extends PSSActivity {

	public PSSExpression m_count;
	public PSSActivity m_stmt;
	public String m_index_id;

	public PSSRepeatActivity(PSSExpression count, PSSActivity stmt, String index_id) {
		m_count = count;
		m_stmt = stmt;
		m_index_id = index_id;
		m_stmt.m_parent = this;
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
}
