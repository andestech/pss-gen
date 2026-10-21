package pssgen.solver;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import pssgen.expr.PSSLinear;
import pssgen.expr.PSSRelation;
import pssgen.inst.PSSInst;
import pssgen.value.PSSIntVal;
import pssgen.value.PSSVal;

/**
 * Bounds propagation over the comparisons of a solver's hard constraints: every
 * top-level expression constraint, split at '&&', whose two sides are linear
 * (PSSLinear) or which is 'x in [constant list]'. Other constraints are only
 * checked after sampling.
 */
public final class PSSPropagator {

	/** at most this many sweeps over the comparisons */
	static final int MAX_SWEEPS = 64;

	/** 'diff op 0' with diff = left - right, or 'var in list' */
	private static final class Item {
		final PSSRelation.Op op;
		final PSSLinear diff;
		final PSSInst var;
		final List<PSSVal> list;

		Item(PSSRelation.Op op, PSSLinear diff) {
			this.op = op;
			this.diff = diff;
			this.var = null;
			this.list = null;
		}

		Item(PSSInst var, List<PSSVal> list) {
			this.op = PSSRelation.Op.IN;
			this.diff = null;
			this.var = var;
			this.list = list;
		}
	}

	private final List<Item> m_items = new ArrayList<Item>();
	private final List<PSSInst> m_vars = new ArrayList<PSSInst>();
	private PSSInst m_empty;

	/**
	 * Reads the comparisons of constraints. Fields that are not variables count
	 * with their value at this time.
	 *
	 * @param constraints the hard constraints of a solver, by the instance they are
	 *                    evaluated in
	 * @param isVariable  the int, bit and bool fields the solver draws
	 */
	public PSSPropagator(Map<PSSInst, PSSConstraintList> constraints, Predicate<PSSInst> isVariable) {
		for (Map.Entry<PSSInst, PSSConstraintList> e : constraints.entrySet()) {
			PSSInst scope = e.getKey();
			PSSConstraintList list = e.getValue();
			for (int i = 0; i < list.size(); i++) {
				PSSConstraint c = list.get(i);
				if (c instanceof PSSExpressionConstraint) {
					for (PSSRelation r : PSSRelation.conjuncts(((PSSExpressionConstraint) c).getExpression())) {
						add(r, scope, isVariable);
					}
				}
			}
		}
	}

	private void add(PSSRelation r, PSSInst scope, Predicate<PSSInst> isVariable) {
		PSSLinear left = PSSLinear.of(r.left, scope, isVariable);
		if (r.op == PSSRelation.Op.IN) {
			if (left == null || left.terms().size() != 1 || left.constant().signum() != 0)
				return;
			Map.Entry<PSSInst, BigInteger> t = left.terms().entrySet().iterator().next();
			List<PSSVal> list = PSSLinear.constantList(r.right, scope);
			if (!t.getValue().equals(BigInteger.ONE) || list == null)
				return;
			addVar(t.getKey());
			m_items.add(new Item(t.getKey(), list));
			return;
		}
		PSSLinear right = PSSLinear.of(r.right, scope, isVariable);
		if (left == null || right == null)
			return;
		PSSLinear diff = PSSLinear.difference(left, right);
		if (diff.terms().isEmpty())
			return;
		for (PSSInst v : diff.terms().keySet()) {
			addVar(v);
		}
		m_items.add(new Item(r.op, diff));
	}

	private void addVar(PSSInst v) {
		if (!m_vars.contains(v))
			m_vars.add(v);
	}

	/** The variables of the comparisons, in the order they first appear. */
	public List<PSSInst> variables() {
		return m_vars;
	}

	/** The variable whose domain the last failed narrow() emptied. */
	public PSSInst emptyVariable() {
		return m_empty;
	}

	/**
	 * Narrows dom, which holds a domain for every variable, to a fixed point (at most
	 * MAX_SWEEPS sweeps). Returns false, with emptyVariable() set, when a domain
	 * becomes empty.
	 */
	public boolean narrow(Map<PSSInst, PSSIntDomain> dom) {
		m_empty = null;
		for (int sweep = 0; sweep < MAX_SWEEPS; sweep++) {
			boolean changed = false;
			for (Item item : m_items) {
				if (item.op == PSSRelation.Op.IN) {
					changed |= update(dom, item.var, dom.get(item.var).reduceDomainIn(item.list));
				} else {
					for (Map.Entry<PSSInst, BigInteger> t : item.diff.terms().entrySet()) {
						changed |= update(dom, t.getKey(), narrowTerm(dom, item, t.getKey(), t.getValue()));
					}
				}
				if (m_empty != null)
					return false;
			}
			if (!changed)
				break;
		}
		return true;
	}

	private boolean update(Map<PSSInst, PSSIntDomain> dom, PSSInst v, PSSIntDomain d) {
		if (d.isEmpty()) {
			m_empty = v;
			return true;
		}
		if (d.getText().equals(dom.get(v).getText()))
			return false;
		dom.put(v, d);
		return true;
	}

	/** the domain of v that 'a * v + rest op 0' leaves, rest bounded by the other domains */
	private static PSSIntDomain narrowTerm(Map<PSSInst, PSSIntDomain> dom, Item item, PSSInst v, BigInteger a) {
		BigInteger lo = item.diff.constant();
		BigInteger hi = item.diff.constant();
		for (Map.Entry<PSSInst, BigInteger> t : item.diff.terms().entrySet()) {
			if (t.getKey() == v)
				continue;
			BigInteger[] b = bounds(dom.get(t.getKey()));
			BigInteger c = t.getValue();
			BigInteger p = c.multiply(b[0]), q = c.multiply(b[1]);
			lo = lo.add(p.min(q));
			hi = hi.add(p.max(q));
		}
		// a * v op -rest, for rest in [lo, hi]
		PSSIntDomain d = dom.get(v);
		switch (item.op) {
		case EQ:
			return atMost(atLeast(d, a, hi.negate()), a, lo.negate());
		case LE:
			return atMost(d, a, lo.negate());
		case LT:
			return atMost(d, a, lo.negate().subtract(BigInteger.ONE));
		case GE:
			return atLeast(d, a, hi.negate());
		case GT:
			return atLeast(d, a, hi.negate().add(BigInteger.ONE));
		case NE:
			if (lo.equals(hi)) {
				BigInteger[] qr = lo.negate().divideAndRemainder(a);
				if (qr[1].signum() == 0)
					return d.reduceDomainNotEqual(new PSSIntVal(qr[0]));
			}
			return d;
		default:
			return d;
		}
	}

	/** d restricted to a * v <= k */
	private static PSSIntDomain atMost(PSSIntDomain d, BigInteger a, BigInteger k) {
		if (a.signum() > 0)
			return d.reduceDomainLessEqual(new PSSIntVal(floorDiv(k, a)));
		return d.reduceDomainGreaterEqual(new PSSIntVal(ceilDiv(k, a)));
	}

	/** d restricted to a * v >= k */
	private static PSSIntDomain atLeast(PSSIntDomain d, BigInteger a, BigInteger k) {
		if (a.signum() > 0)
			return d.reduceDomainGreaterEqual(new PSSIntVal(ceilDiv(k, a)));
		return d.reduceDomainLessEqual(new PSSIntVal(floorDiv(k, a)));
	}

	static BigInteger floorDiv(BigInteger x, BigInteger y) {
		BigInteger[] qr = x.divideAndRemainder(y);
		if (qr[1].signum() != 0 && (qr[1].signum() != y.signum()))
			return qr[0].subtract(BigInteger.ONE);
		return qr[0];
	}

	static BigInteger ceilDiv(BigInteger x, BigInteger y) {
		BigInteger[] qr = x.divideAndRemainder(y);
		if (qr[1].signum() != 0 && (qr[1].signum() == y.signum()))
			return qr[0].add(BigInteger.ONE);
		return qr[0];
	}

	/** {min, max} of a non-empty domain */
	static BigInteger[] bounds(PSSIntDomain d) {
		BigInteger min = null, max = null;
		for (PSSVal item : d.getValList()) {
			BigInteger lo = item.isRangeVal() ? item.getMin().toBigInteger() : item.toBigInteger();
			BigInteger hi = item.isRangeVal() ? item.getMax().toBigInteger() : item.toBigInteger();
			min = min == null ? lo : min.min(lo);
			max = max == null ? hi : max.max(hi);
		}
		return new BigInteger[] { min, max };
	}

	/** the domain {value} */
	static PSSIntDomain single(BigInteger value) {
		return new PSSIntDomain(new PSSIntVal(value));
	}

	/** a copy of every domain of dom */
	static LinkedHashMap<PSSInst, PSSIntDomain> copy(Map<PSSInst, PSSIntDomain> dom) {
		return new LinkedHashMap<PSSInst, PSSIntDomain>(dom);
	}
}
