package pssgen.expr;

import java.util.ArrayList;
import java.util.List;

/** A comparison 'left op right' of a constraint, as the propagating solver reads it. */
public final class PSSRelation {

	public enum Op {
		EQ("=="), NE("!="), LT("<"), LE("<="), GT(">"), GE(">="), IN("in");

		/** the operator as it is written in PSS */
		public final String token;

		Op(String token) {
			this.token = token;
		}
	}

	public final Op op;
	public final PSSExpression left;
	public final PSSExpression right;

	PSSRelation(Op op, PSSExpression left, PSSExpression right) {
		this.op = op;
		this.left = left;
		this.right = right;
	}

	/**
	 * Returns the comparisons that e requires: e itself, or both sides of each '&&',
	 * recursively. An operand that is not a comparison (or an open range
	 * comparison missing its left side) is left out.
	 */
	public static List<PSSRelation> conjuncts(PSSExpression e) {
		List<PSSRelation> res = new ArrayList<PSSRelation>();
		collect(e, res);
		return res;
	}

	private static void collect(PSSExpression e, List<PSSRelation> res) {
		if (e instanceof PSSLogicalAndExpression) {
			PSSLogicalAndExpression a = (PSSLogicalAndExpression) e;
			collect(a.m_left, res);
			collect(a.m_right, res);
			return;
		}
		PSSRelation r = e.relation();
		if (r != null && r.left != null && r.right != null) {
			res.add(r);
		}
	}
}
