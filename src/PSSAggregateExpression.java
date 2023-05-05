import java.util.*;

public class PSSAggregateExpression extends PSSExpression {

	public ArrayList<PSSExpression> m_list;
	

	public PSSAggregateExpression() {
		m_list = new ArrayList<PSSExpression>();
	}

	public void addExpression(PSSExpression item) {
		m_list.add(item);
	}

	public PSSVal eval(PSSInst var) {
		PSSArrayVal result = new PSSArrayVal();

		for (int i=0; i<m_list.size(); i++) {
			PSSExpression item = m_list.get(i);

			PSSVal item_val = item.eval(var);
			result.addVal(item_val);
		}


		return result;
	}

	public String getText() {
		String text = "";

		for (int i=0; i<m_list.size(); i++) {
			PSSExpression item = m_list.get(i);

			if (text.length() == 0) {
				text = text.concat(item.getText());
			}
			else {
				text = text.concat(", " + item.getText());
			}
		}
		return "[ " + text + " ]";
	}
}
