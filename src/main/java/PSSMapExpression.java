
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * {@PSSMapExpression} denotes map literals.
 */
public class PSSMapExpression extends PSSExpression {

	private Map<PSSExpression, PSSExpression> map = new HashMap<PSSExpression, PSSExpression>();

	public PSSMapExpression() {
	}

	@Override
	public boolean isRandomable (PSSInst var) {
		return false;
	}

	/**
	 * Inserts a key-value pair to this map literal.
	 *
	 * @param key a key
	 * @param val a value associated with the key
	 */
	public void add(PSSExpression key, PSSExpression val) {
		map.put(key, val);
	}

	@Override
	public PSSMapVal eval(PSSInst var) {
		PSSMapVal res = new PSSMapVal(null);
		for (PSSExpression k : map.keySet()) {
			PSSExpression v = map.get(k);
			PSSVal key = k.eval(var);
			PSSVal val = v.eval(var);
			res.insert(key, val);
		}
		return res;
	}

	@Override
	public String getText() {
		List<String> strs = new ArrayList<String>();
		for (PSSExpression k : map.keySet()) {
			PSSExpression v = map.get(k);
			strs.add(k.getText() + ": " + v.getText());
		}
		return "{ " + String.join(", ", strs) + " }";
	}

}
