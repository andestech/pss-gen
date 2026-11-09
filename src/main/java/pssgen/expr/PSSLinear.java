package pssgen.expr;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import pssgen.inst.PSSBoolInst;
import pssgen.inst.PSSInst;
import pssgen.inst.PSSIntInst;
import pssgen.value.PSSBoolVal;
import pssgen.value.PSSIntVal;
import pssgen.value.PSSVal;

/**
 * An integer expression as sum(coef * x) + constant over the variables a solver
 * draws (int, bit and bool fields; a bool counts as 0 or 1). Expressions evaluate
 * on BigIntegers without wrapping, so bounds derived from this form hold for eval().
 */
public final class PSSLinear {

	/** the random variables, in the order they first appear, with their coefficients */
	private final LinkedHashMap<PSSInst, BigInteger> m_terms;

	private final BigInteger m_constant;

	private PSSLinear(LinkedHashMap<PSSInst, BigInteger> terms, BigInteger constant) {
		m_terms = terms;
		m_constant = constant;
	}

	private static PSSLinear constant(BigInteger c) {
		return new PSSLinear(new LinkedHashMap<PSSInst, BigInteger>(), c);
	}

	public Map<PSSInst, BigInteger> terms() {
		return m_terms;
	}

	public BigInteger constant() {
		return m_constant;
	}

	/** this + k * other */
	private PSSLinear plus(PSSLinear other, BigInteger k) {
		LinkedHashMap<PSSInst, BigInteger> t = new LinkedHashMap<PSSInst, BigInteger>(m_terms);
		for (Map.Entry<PSSInst, BigInteger> e : other.m_terms.entrySet()) {
			BigInteger c = t.getOrDefault(e.getKey(), BigInteger.ZERO).add(e.getValue().multiply(k));
			if (c.signum() == 0) {
				t.remove(e.getKey());
			} else {
				t.put(e.getKey(), c);
			}
		}
		return new PSSLinear(t, m_constant.add(other.m_constant.multiply(k)));
	}

	private PSSLinear times(BigInteger k) {
		return constant(BigInteger.ZERO).plus(this, k);
	}

	/** left - right */
	public static PSSLinear difference(PSSLinear left, PSSLinear right) {
		return left.plus(right, BigInteger.ONE.negate());
	}

	/**
	 * Returns e in linear form, or null when e is not built from integer and Boolean
	 * literals, scalar int/bit/bool fields, unary minus, +, - and * by a constant.
	 * Fields that are not variables count with their current value.
	 *
	 * @param e          an expression
	 * @param scope      the instance e is evaluated in
	 * @param isVariable the fields the solver draws
	 * @return the linear form of e, or null
	 */
	public static PSSLinear of(PSSExpression e, PSSInst scope, Predicate<PSSInst> isVariable) {
		if (e instanceof PSSPrimaryExpression) {
			return ofValue(((PSSPrimaryExpression) e).m_val);
		}
		if (e instanceof PSSAddExpression) {
			PSSAddExpression a = (PSSAddExpression) e;
			return combine(of(a.m_left, scope, isVariable), of(a.m_right, scope, isVariable), BigInteger.ONE);
		}
		if (e instanceof PSSSubExpression) {
			PSSSubExpression s = (PSSSubExpression) e;
			return combine(of(s.m_left, scope, isVariable), of(s.m_right, scope, isVariable), BigInteger.ONE.negate());
		}
		if (e instanceof PSSUnaryMinusExpression) {
			PSSLinear x = of(((PSSUnaryMinusExpression) e).m_exp, scope, isVariable);
			return x == null ? null : x.times(BigInteger.ONE.negate());
		}
		if (e instanceof PSSMulExpression) {
			PSSMulExpression m = (PSSMulExpression) e;
			PSSLinear l = of(m.m_left, scope, isVariable);
			PSSLinear r = of(m.m_right, scope, isVariable);
			if (l == null || r == null) {
				return null;
			}
			if (l.m_terms.isEmpty()) {
				return r.times(l.m_constant);
			}
			if (r.m_terms.isEmpty()) {
				return l.times(r.m_constant);
			}
			return null;
		}
		if (e instanceof PSSRefPathExpression) {
			PSSRefPathExpression r = (PSSRefPathExpression) e;
			if (r.getBitSliceFrom() != null || r.getBitSliceTo() != null) {
				return null;
			}
			return ofVariable(r.getInst(scope), isVariable);
		}
		if (e instanceof PSSMemberPathElemExpression) {
			return ofVariable(e.getInst(scope), isVariable);
		}
		return null;
	}

	private static PSSLinear combine(PSSLinear l, PSSLinear r, BigInteger k) {
		return (l == null || r == null) ? null : l.plus(r, k);
	}

	private static PSSLinear ofValue(PSSVal v) {
		if (v instanceof PSSIntVal || v instanceof PSSBoolVal) {
			return constant(v.toBigInteger());
		}
		return null;
	}

	private static PSSLinear ofVariable(PSSInst inst, Predicate<PSSInst> isVariable) {
		if (inst instanceof PSSIntInst) {
			PSSIntInst i = (PSSIntInst) inst;
			if (i.isAccessBitSelect()) {
				i.toVal();	// clears the bit-select that resolving the reference set
				return null;
			}
		} else if (!(inst instanceof PSSBoolInst)) {
			return null;
		}
		if (isVariable.test(inst)) {
			LinkedHashMap<PSSInst, BigInteger> t = new LinkedHashMap<PSSInst, BigInteger>();
			t.put(inst, BigInteger.ONE);
			return new PSSLinear(t, BigInteger.ZERO);
		}
		return constant(inst.toVal().toBigInteger());
	}

	/**
	 * Returns the values of a constant open range list (the right side of 'in'), or
	 * null when it is not constant.
	 */
	public static List<PSSVal> constantList(PSSExpression e, PSSInst scope) {
		if (!(e instanceof PSSOpenRangeListExpression) || !e.getInsts(scope).isEmpty()) {
			return null;
		}
		return e.eval(scope).getValList();
	}
}
