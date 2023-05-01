// Copyright (C) 2021, Andes Technology Corp. Confidential Proprietary
import java.util.*;

public class PSSMapExpression extends PSSExpression {

	private Map<PSSExpression, PSSExpression> map = new HashMap<PSSExpression, PSSExpression>();

	public PSSMapExpression() {
	}

	public void add(PSSExpression key, PSSExpression val) {
		map.put(key, val);
	}

	public PSSMapVal eval(PSSInst var) {
		PSSMapVal res = new PSSMapVal();
		for (PSSExpression k : map.keySet()) {
			PSSExpression v = map.get(k);
			res.insert(k.eval(var), v.eval(var));
		}
		return res;
	}

	public String getText() {
		List<String> strs = new ArrayList<String>();
		for (PSSExpression k : map.keySet()) {
			PSSExpression v = map.get(k);
			strs.add(k.getText() + ": " + v.getText());
		}
		return "{ " + String.join(", ", strs) + " }";
	}

}
