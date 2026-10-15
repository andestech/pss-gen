import java.util.*;

public class PSSAggregateExpression extends PSSExpression {

	public ArrayList<PSSExpression> m_list;

	public PSSAggregateExpression() {
		m_list = new ArrayList<PSSExpression>();
	}

	@Override
	public boolean isRandomable (PSSInst var) {
		return false;
	}

	public void addExpression(PSSExpression item) {
		m_list.add(item);
	}

	/**
	 * Returns {@code true} if this is an empty aggregate literal.
	 *
	 * @return {@code true} if this is an empty aggregate literal
	 */
	public boolean isEmpty() {
		return m_list.isEmpty();
	}

	@Override
	public PSSVal eval(PSSInst var) {
		PSSListVal result = new PSSListVal(null);

		for (int i = 0; i < m_list.size(); i++) {
			PSSExpression item = m_list.get(i);

			PSSVal item_val = item.eval(var);
			result.add(item_val);
		}

		return result;
	}

	@Override
	public String getText() {
		String text = "";

		for (int i = 0; i < m_list.size(); i++) {
			PSSExpression item = m_list.get(i);

			if (text.length() == 0) {
				text = text.concat(item.getText());
			} else {
				text = text.concat(", " + item.getText());
			}
		}
		return "{ " + text + " }";
	}

}
