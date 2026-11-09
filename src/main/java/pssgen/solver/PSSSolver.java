package pssgen.solver;

import java.util.*;
import java.util.LinkedHashMap;

import pssgen.expr.PSSEqualExpression;
import pssgen.inst.PSSActionInst;
import pssgen.inst.PSSArrayInst;
import pssgen.inst.PSSBoolInst;
import pssgen.inst.PSSFlowRefInst;
import pssgen.inst.PSSIScalarInst;
import pssgen.inst.PSSInst;
import pssgen.inst.PSSIntInst;
import pssgen.inst.PSSStructInst;
import pssgen.util.PSSException;
import pssgen.util.PSSMessage;
import pssgen.util.PSSRun;

public class PSSSolver {
	PSSInst m_inst;
	// PSSInst keys hash by identity. Insertion order keeps the order of the
	// -info domain lines and the variable that OVERCONSTRAINT names the same
	// on every JVM and every run.
	private LinkedHashMap<PSSInst, PSSConstraintList> m_inst_list;
	private LinkedHashMap<PSSInst, PSSConstraintList> m_default_constraint_list;
	// The domain each field had before the solver first narrowed it, for
	// solveRetryThenPropagate to start solvePropagate from the state before solve().
	private final LinkedHashMap<PSSInst, PSSDomain> m_saved_domains = new LinkedHashMap<PSSInst, PSSDomain>();

	public PSSSolver(PSSInst inst) {
		m_inst_list = new LinkedHashMap<PSSInst, PSSConstraintList>();
		m_default_constraint_list = new LinkedHashMap<PSSInst, PSSConstraintList>();
		m_inst = inst;
	}

	void addDefaultConstraint (PSSInst inst, PSSConstraint constraint) {
		if (!(constraint instanceof PSSExpressionConstraint)) {
			PSSMessage.Fatal("default constraint should be PSSExpressionConstraint class: < " + constraint.getClass().getSimpleName() + " >");
		}

		if (m_default_constraint_list.containsKey(inst)) {
			PSSConstraintList c_list = m_default_constraint_list.get(inst);
			c_list.add(constraint);
		} else {
			PSSConstraintList c_list = new PSSConstraintList();
			c_list.add(constraint);
			m_default_constraint_list.put(inst, c_list);
		}
	}

	public void addDefaultConstraint (PSSInst inst, PSSConstraintList list) {
		for (int i = 0; i < list.size(); i++) {
			PSSConstraint c = list.get(i);
			addDefaultConstraint(inst, c);
		}
	}

	void add(PSSInst inst, PSSConstraint constraint) {
		if (m_inst_list.containsKey(inst)) {
			PSSConstraintList c_list = m_inst_list.get(inst);
			c_list.add(constraint);
		} else {
			PSSConstraintList c_list = new PSSConstraintList();
			c_list.add(constraint);
			m_inst_list.put(inst, c_list);
		}
	}

	public void add(PSSInst inst, PSSConstraintList list) {
		for (int i = 0; i < list.size(); i++) {
			PSSConstraint c = list.get(i);
			add(inst, c);
		}
	}

	public void add(PSSInst inst) {
		if (inst instanceof PSSStructInst) {
			PSSConstraintList c_list = inst.getTypeModel().getConstraintList();
			add(inst, c_list);
			if (inst.isRand())
				addRandomized(inst.getInsts());
		} else if (inst instanceof PSSFlowRefInst) {
			PSSConstraintList c_list = inst.getTypeModel().getConstraintList();
			add(inst, c_list);
			PSSInst flow_object = ((PSSFlowRefInst) inst).getVar();
			if (flow_object != null)
				addRandomized(flow_object.getInsts());
		} else if (inst instanceof PSSArrayInst) {
			if (inst.isRand())
				addRandomized(((PSSArrayInst) inst).elements());
		}
	}

	/**
	 * PSS 2.0 Sections 17.4.1.1 a) and 8.8.1: the rand struct fields of a rand struct
	 * or a flow object and the elements of a rand array are randomized with their
	 * container, so the constraints of their struct types hold for them too.
	 */
	private void addRandomized(List<PSSInst> parts) {
		for (PSSInst part : parts) {
			if (part.isRand())
				add(part);
		}
	}

	void reduceDefaultDomain () {
		for (PSSInst item : m_default_constraint_list.keySet()) {
			PSSConstraintList def_c_list = m_default_constraint_list.get(item);
			PSSConstraintList c_list     = m_inst_list.get(item);

			// Reduce each default constraint
			for (var i = 0; i < def_c_list.size(); i++) {
				// Get random variable from syntax: (default hierarchical_id '==' constant_expression ';')
				PSSExpressionConstraint def_c = (PSSExpressionConstraint)def_c_list.get(i);
				if (!def_c.isActive(item)) continue;
				PSSEqualExpression def_exp    = (PSSEqualExpression)def_c.getExpression();
				PSSInst def_inst              = def_exp.getInsts(item).get(0);
				
				// Assign domain of the random variable as default constraint
				PSSDomainMap def_map = def_c.deduceDomain(item);
				PSSDomain def_domain = def_map.getMap().get(def_inst);
				saveDomain(def_inst);
				def_inst.assignDomain(def_domain);
			}
		}
	}

	/**
	 * The constraints of each instance with its default constraints that apply:
	 * PSS 2.0 Section 17.1.10 a) gives a default "the same semantics as the
	 * corresponding equality constraint", so it narrows domains as one does, and a
	 * conflict with another constraint empties a domain (a contradiction).
	 */
	private LinkedHashMap<PSSInst, PSSConstraintList> withDefaults() {
		LinkedHashMap<PSSInst, PSSConstraintList> res = new LinkedHashMap<PSSInst, PSSConstraintList>();
		for (Map.Entry<PSSInst, PSSConstraintList> e : m_inst_list.entrySet()) {
			PSSConstraintList c_list = new PSSConstraintList();
			for (int i = 0; i < e.getValue().size(); i++)
				c_list.add(e.getValue().get(i));
			res.put(e.getKey(), c_list);
		}
		for (Map.Entry<PSSInst, PSSConstraintList> e : m_default_constraint_list.entrySet()) {
			for (int i = 0; i < e.getValue().size(); i++) {
				PSSExpressionConstraint def_c = (PSSExpressionConstraint) e.getValue().get(i);
				if (def_c.isActive(e.getKey()))
					res.computeIfAbsent(e.getKey(), k -> new PSSConstraintList()).add(def_c);
			}
		}
		return res;
	}

	void reduceDomain() {
		PSSDomainMap result = new PSSDomainMap();

		// Deduce domain by constraint
		LinkedHashMap<PSSInst, PSSConstraintList> constraints = withDefaults();
		for (PSSInst inst : constraints.keySet()) {
			PSSConstraintList c_list = constraints.get(inst);

			for (int i = 0; i < c_list.size(); i++) {
				PSSConstraint c = c_list.get(i);
				PSSDomainMap d_map = c.deduceDomain(inst);
				if (d_map != null) {
					result = result.join(d_map);
				}
			}

		}

		LinkedHashMap<PSSInst, PSSDomain> map = result.getMap();
		for (PSSInst var : map.keySet()) {
			PSSDomain domain = map.get(var);
			saveDomain(var);
			var.assignDomain(domain);
			m_inst.run().log().Info(var.getId() + " domain: " + domain.getText());
		}
	}

	public boolean validate () {
		// Validate default constraint (PSS 2.0 Section 17.1.10 a): "The equality must hold")
		for (PSSInst item : m_default_constraint_list.keySet()) {
			PSSConstraintList def_c_list = m_default_constraint_list.get(item);

			// Validate each default constraint
			for (var i = 0; i < def_c_list.size(); i++) {
				// Get random variable from syntax: (default hierarchical_id '==' constant_expression ';')
				PSSExpressionConstraint def_c = (PSSExpressionConstraint)def_c_list.get(i);
				if (!def_c.isActive(item)) continue;
				if (def_c.validate(item) == false) return false;
			}
		}

		// Validate constraint
		for (PSSInst item : m_inst_list.keySet()) {
			PSSConstraintList c_list = m_inst_list.get(item);
			if (c_list.validate(item) == false) return false;
		}
		return true;
	}

	int solve() {
		int retry = 0;

		reduceDefaultDomain();

		do {
			m_inst.randomize();
			reduceDomain();	// reduce domain with constraints
			m_inst.run().random().limitRetry(++retry);
		} while (validate() == false);

		return retry;
	}

	private void saveDomain(PSSInst inst) {
		if (!m_saved_domains.containsKey(inst)) {
			m_saved_domains.put(inst, inst.getDomain());
		}
	}

	/**
	 * The default solver: solve(), and when it gives up (randomize fail or an empty
	 * domain), solvePropagate() from the domains the fields had before solve(). A model
	 * that solve() solves gets exactly the output of solve(). When both give up, the
	 * failure of solve() is reported.
	 *
	 * @return the number of draws of the solver that succeeded
	 */
	public int solveRetryThenPropagate() {
		try {
			return solve();
		} catch (PSSException e) {
			if (!givesUp(e))
				throw e;
			for (Map.Entry<PSSInst, PSSDomain> d : m_saved_domains.entrySet()) {
				d.getKey().restoreDomain(d.getValue());
			}
			m_inst.run().log().Info("retry solver: " + e.getMessage() + "; solving again with -solver propagate");
			try {
				return solvePropagate();
			} catch (PSSException e2) {
				if (!givesUp(e2))
					throw e2;
				// A PSS-ERROR is stamped now, so that its time follows the messages of
				// the second attempt.
				throw e.getID() == null ? e : PSSException.error(e.getID(), e.getMessage(), System.currentTimeMillis());
			}
		}
	}

	/** True for the failures that end a solver without a fault in the model: no value found. */
	private static boolean givesUp(PSSException e) {
		return "OVERCONSTRAINT".equals(e.getID()) || (e.getID() == null && "randomize fail".equals(e.getMessage()));
	}

	/**
	 * Solves with -solver propagate: narrows the domains of the variables of linear
	 * comparisons (PSSPropagator) before the first draw, draws them one by one from
	 * what the values drawn so far leave, and after a failed check re-draws only the
	 * variables of the violated constraints.
	 *
	 * @return the number of draws
	 */
	public int solvePropagate() {
		PSSRun run = m_inst.run();

		// Default value constraints, as solve() applies them; PSSPropagator then
		// narrows every variable by its hard and default comparisons together.
		reduceDefaultDomain();

		PSSPropagator prop = new PSSPropagator(withDefaults(), this::isDrawnScalar);
		LinkedHashMap<PSSInst, PSSIntDomain> base = new LinkedHashMap<PSSInst, PSSIntDomain>();
		for (PSSInst v : prop.variables()) {
			base.put(v, (PSSIntDomain) v.getInitDomain());
		}
		if (!prop.narrow(base)) {
			prop.emptyVariable().assignDomain(new PSSIntDomain());	// reports OVERCONSTRAINT
		}
		for (PSSInst v : prop.variables()) {
			v.assignDomain(base.get(v));
			run.log().Info(v.getId() + " domain: " + base.get(v).getText());
		}

		int retry = 0;
		Set<PSSInst> redraw = null;	// null: everything
		do {
			run.random().limitRetry(++retry);
			if (redraw == null) {
				m_inst.randomize();
			}
			// Fields that are not variables (in other actions, flow objects) count with
			// their current values, which a full draw may have changed.
			draw(new PSSPropagator(withDefaults(), this::isDrawnScalar), base, redraw);
			redraw = violatedVariables();
			if (redraw == null || !redraw.isEmpty()) {
				deduceOtherDomains(prop.variables());
			}
		} while (redraw == null || !redraw.isEmpty());

		for (PSSInst v : prop.variables()) {
			v.assignDomain(base.get(v));
		}
		return retry;
	}

	/**
	 * Draws the variables of redraw (all when null): those of the comparisons in their
	 * order, each from its base domain narrowed by the values of the variables before
	 * it and of the variables that keep their value; the others with randomize().
	 */
	private void draw(PSSPropagator prop, Map<PSSInst, PSSIntDomain> base, Set<PSSInst> redraw) {
		LinkedHashMap<PSSInst, PSSIntDomain> dom = new LinkedHashMap<PSSInst, PSSIntDomain>();
		for (PSSInst v : prop.variables()) {
			boolean keep = redraw != null && !redraw.contains(v);
			// An array element reached through a random index is a variable only for
			// the index values of this draw; it starts from its full domain.
			PSSIntDomain b = base.computeIfAbsent(v, k -> (PSSIntDomain) k.getInitDomain());
			dom.put(v, keep ? PSSPropagator.single(v.toVal().toBigInteger()) : b);
		}
		boolean open = prop.narrow(dom);
		for (PSSInst v : prop.variables()) {
			if (redraw != null && !redraw.contains(v)) {
				continue;
			}
			// After a dead end the remaining variables come from their base domains;
			// the check then fails and the next round draws them again.
			v.assignDomain(open ? dom.get(v) : base.get(v));
			v.randomize();
			if (open) {
				dom.put(v, PSSPropagator.single(v.toVal().toBigInteger()));
				open = prop.narrow(dom);
			}
		}
		if (redraw != null) {
			for (PSSInst v : redraw) {
				if (!prop.variables().contains(v)) {
					v.randomize();
				}
			}
		}
	}

	/**
	 * Gives the variables that PSSPropagator does not handle (strings, enums, fields
	 * only in if or foreach constraints, ...) the domains that the constraints leave
	 * with the values drawn so far, as solve() does for every variable.
	 */
	private void deduceOtherDomains(List<PSSInst> propagated) {
		PSSDomainMap result = new PSSDomainMap();
		LinkedHashMap<PSSInst, PSSConstraintList> constraints = withDefaults();
		for (PSSInst inst : constraints.keySet()) {
			PSSConstraintList c_list = constraints.get(inst);
			for (int i = 0; i < c_list.size(); i++) {
				PSSDomainMap d_map = c_list.get(i).deduceDomain(inst);
				if (d_map != null) {
					result = result.join(d_map);
				}
			}
		}
		for (Map.Entry<PSSInst, PSSDomain> e : result.getMap().entrySet()) {
			if (!propagated.contains(e.getKey())) {
				e.getKey().assignDomain(e.getValue());
			}
		}
	}

	/**
	 * The variables of the constraints that do not hold: empty when all hold, null
	 * when a violated constraint names no variable that can be drawn alone.
	 */
	private Set<PSSInst> violatedVariables() {
		Set<PSSInst> res = new LinkedHashSet<PSSInst>();
		for (PSSInst item : m_default_constraint_list.keySet()) {
			PSSConstraintList def_c_list = m_default_constraint_list.get(item);
			for (var i = 0; i < def_c_list.size(); i++) {
				PSSExpressionConstraint def_c = (PSSExpressionConstraint) def_c_list.get(i);
				if (!def_c.isActive(item)) continue;
				PSSInst def_inst = ((PSSEqualExpression) def_c.getExpression()).getInsts(item).get(0);
				if (!def_c.validate(item)) res.add(def_inst);
			}
		}
		for (PSSInst item : m_inst_list.keySet()) {
			PSSConstraintList c_list = m_inst_list.get(item);
			for (int i = 0; i < c_list.size(); i++) {
				PSSConstraint c = c_list.get(i);
				if (c.validate(item)) continue;
				if (!(c instanceof PSSExpressionConstraint)) return null;
				boolean any = false;
				for (PSSInst v : ((PSSExpressionConstraint) c).getExpression().getInsts(item)) {
					if (isDrawn(v)) {
						res.add(v);
						any = true;
					}
				}
				if (!any) return null;
			}
		}
		return res;
	}

	/**
	 * True when m_inst.randomize() draws v: a random scalar whose containers up to the
	 * solved action, array elements and arrays included, are random too (a field of a
	 * non-random struct keeps its value, a field of a sub-action is drawn when that
	 * action is solved).
	 */
	private boolean isDrawn(PSSInst v) {
		if (!(v instanceof PSSIScalarInst) || !v.isRandomable())
			return false;
		for (PSSInst p = v.getContainer(); p != null; p = p.getContainer()) {
			if (p == m_inst)
				return true;
			if (p instanceof PSSActionInst || !p.isRandomable())
				return false;
		}
		return false;
	}

	/** isDrawn for the int, bit and bool fields, the variables of PSSPropagator */
	private boolean isDrawnScalar(PSSInst v) {
		return (v instanceof PSSIntInst || v instanceof PSSBoolInst) && isDrawn(v);
	}

	public final LinkedHashMap<PSSInst, PSSConstraintList> getInstList() {
		return m_inst_list;
	}

	public final LinkedHashMap<PSSInst, PSSConstraintList> getDefaultConstraintList() {
		return m_default_constraint_list;
	}
}
