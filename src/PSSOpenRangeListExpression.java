import java.util.*;

public class PSSOpenRangeListExpression extends PSSExpression {

	public ArrayList<PSSOpenRangeValueExpression> m_list;
	

	public PSSOpenRangeListExpression() {
		m_list = new ArrayList<PSSOpenRangeValueExpression>();
	}

	public void addOpenRangeValueExpression(PSSOpenRangeValueExpression item) {
		m_list.add(item);
	}

	public PSSVal eval(PSSInst var) {
		PSSSetVal result = new PSSSetVal();

		for (int i=0; i<m_list.size(); i++) {
			PSSOpenRangeValueExpression item = m_list.get(i);

			PSSVal item_val = item.eval(var);
			result.addVal(item_val);
		}


		return result;
	}

	public String getText() {
		String text = "";

		for (int i=0; i<m_list.size(); i++) {
			PSSOpenRangeValueExpression item = m_list.get(i);

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
