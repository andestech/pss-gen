package pssgen.expr;

import java.util.ArrayList;
import java.util.List;

/** A comparison 'left op right' of a constraint, as the propagating solver reads it. */
public final class PSSRelation {

	public enum Op { EQ, NE, LT, LE, GT, GE, IN }

	public final Op op;
	public final PSSExpression left;
	public final PSSExpression right;

	private PSSRelation(Op op, PSSExpression left, PSSExpression right) {
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
		PSSRelation r = of(e);
		if (r != null && r.left != null && r.right != null) {
			res.add(r);
		}
	}

	private static PSSRelation of(PSSExpression e) {
		if (e instanceof PSSEqualExpression) {
			PSSEqualExpression x = (PSSEqualExpression) e;
			return new PSSRelation(Op.EQ, x.m_left, x.m_right);
		}
		if (e instanceof PSSNotEqualExpression) {
			PSSNotEqualExpression x = (PSSNotEqualExpression) e;
			return new PSSRelation(Op.NE, x.m_left, x.m_right);
		}
		if (e instanceof PSSLessThanExpression) {
			PSSLessThanExpression x = (PSSLessThanExpression) e;
			return new PSSRelation(Op.LT, x.m_left, x.m_right);
		}
		if (e instanceof PSSLessEqualExpression) {
			PSSLessEqualExpression x = (PSSLessEqualExpression) e;
			return new PSSRelation(Op.LE, x.m_left, x.m_right);
		}
		if (e instanceof PSSGreaterThanExpression) {
			PSSGreaterThanExpression x = (PSSGreaterThanExpression) e;
			return new PSSRelation(Op.GT, x.m_left, x.m_right);
		}
		if (e instanceof PSSGreaterEqualExpression) {
			PSSGreaterEqualExpression x = (PSSGreaterEqualExpression) e;
			return new PSSRelation(Op.GE, x.m_left, x.m_right);
		}
		if (e instanceof PSSInExpression) {
			PSSInExpression x = (PSSInExpression) e;
			return new PSSRelation(Op.IN, x.m_left, x.m_right);
		}
		return null;
	}
}
