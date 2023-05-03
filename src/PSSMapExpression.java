
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
		PSSMapVal res = new PSSMapVal();
		for (PSSExpression k : map.keySet()) {
			PSSExpression v = map.get(k);
			res.insert(k.eval(var), v.eval(var));
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
