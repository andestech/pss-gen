package pssgen.inst;

import java.util.*;

import pssgen.model.PSSExecKind;
import pssgen.model.PSSModel;
import pssgen.solver.PSSConstraintList;
import pssgen.solver.PSSDomain;
import pssgen.solver.PSSSolver;
import pssgen.util.PSSMessage;
import pssgen.util.PSSRun;
import pssgen.value.PSSVal;

public class PSSInst {

	abstract class NativeMethod {

		String m_name;

		int m_nargs;

		/**
		 * Constructs a native method.
		 *
		 * @param name  the name of the method
		 * @param nargs the number of arguments, -1 if the function is variadic
		 */
		public NativeMethod(String name, int nargs) {
			m_name = name;
			m_nargs = nargs;
		}

		protected abstract PSSInst doEval(List<PSSVal> args);

		public PSSInst eval(List<PSSVal> args) {
			if (m_nargs >= 0 && args.size() != m_nargs) {
				PSSMessage.Fatal(
						"Mismatched number of parameters to " + m_id + "." + m_name + " (expected = " + m_nargs
								+ ", actual = "
								+ args.size() + ").");
			}
			return doEval(args);
		}

	}

	private String m_id;
	private PSSInst m_parent;
	private String m_type;
	private PSSModel m_type_decl;
	private boolean m_constant;
	private boolean m_rand;
	private boolean m_initialized;

	private boolean m_traversed;
	private ArrayList<PSSInst> m_insts;
	Map<String, NativeMethod> m_native_methods = new HashMap<String, NativeMethod>();

	/** {@code true} if this instance is read-only */
	boolean m_readonly;

	/** the run of a root instance (see {@link #run()}); null below the root */
	private PSSRun m_run;

	/**
	 * the instance that holds this one without being its parent: the array, list,
	 * set or map of an element, the function invocation of a return value
	 */
	private PSSInst m_owner;

	// buildin type
	public PSSInst(String id, String type, boolean rand) {
		m_id = id;
		m_type = type;
		m_rand = rand;
		m_initialized = false;
		m_insts = new ArrayList<PSSInst>();
	}

	// non-buildin type
	public PSSInst(String id, String type, PSSModel type_decl, boolean rand) {
		m_id = id;
		m_type = type;
		m_type_decl = type_decl;
		m_rand = rand;
		m_initialized = false;
		m_traversed = false;
		m_insts = new ArrayList<PSSInst>();
	}

	public PSSInst addInst(PSSInst inst) {
		m_insts.add(inst);
		inst.m_parent = this;
		return inst;
	}

	public void removeInst(PSSInst inst) {
		m_insts.remove(inst);
	}

	public boolean isTraversed() {
		return m_traversed;
	}

	public boolean isComponent() {
		return false;
	}

	/**
	 * Returns the value an empty aggregate literal assigns to this instance, or null
	 * when this instance is not a list, set or map.
	 */
	public PSSVal emptyCollectionVal() {
		return null;
	}

	public void setTraversed() {
		m_traversed = true;
	}

	/**
	 * Returns the root instance of this one.
	 *
	 * @return the root instance of this one
	 */
	public PSSInst getRoot() {
		PSSInst root = this;
		while (root.m_parent != null)
			root = root.m_parent;
		return root;
	}

	/**
	 * Returns the run this instance belongs to: the run set on the nearest instance
	 * on the way to the root, which goes to the parent, or to the owner of an
	 * instance without a parent.
	 *
	 * @return the run of this instance
	 * @throws IllegalStateException if neither this instance nor an instance
	 *                               containing it has a run
	 */
	public PSSRun run() {
		for (PSSInst i = this; i != null; i = i.m_parent != null ? i.m_parent : i.m_owner) {
			if (i.m_run != null)
				return i.m_run;
		}
		throw new IllegalStateException("instance '" + m_id + "' belongs to no run");
	}

	/**
	 * Sets the run of a root instance, or of an instance that has no parent while it
	 * is in use (a function invocation of a package function).
	 *
	 * @param run the run
	 */
	public void setRun(PSSRun run) {
		m_run = run;
	}

	/**
	 * Sets the instance that holds this one without being its parent (see
	 * {@link #run()}).
	 *
	 * @param owner the holding instance
	 */
	public void setOwner(PSSInst owner) {
		m_owner = owner;
	}

	/**
	 * Returns the descendants of this instance.
	 *
	 * @return the descendants of this instance: the children in declaration
	 *         order, then the descendants of each child in the same order
	 */
	public Set<PSSInst> getDescendants() {
		Set<PSSInst> res = new LinkedHashSet<PSSInst>();
		res.addAll(m_insts);
		for (PSSInst child : m_insts) {
			res.addAll(child.getDescendants());
		}
		return res;
	}

	public String getHierarchyId() {
		if (m_parent != null) {
			return m_parent.getHierarchyId() + "." + m_id;
		} else {
			return m_id;
		}
	}

	/**
	 * Returns the type model of this instance.
	 *
	 * @return the type model of this instance
	 */
	public PSSModel getTypeModel() {
		return m_type_decl;
	}

	protected PSSInst findChild(String id) {
		for (int i = 0; i < m_insts.size(); i++) {
			PSSInst child = m_insts.get(i);
			if (child.m_id.equals(id)) {
				return child;
			}
		}
		return null;
	}

	/** Returns the nearest component instance at or above this one, or null. */
	public PSSInst getComponentInst() {
		if (m_parent != null) {
			return m_parent.getComponentInst();
		}
		return null;
	}

	public PSSInst findInstanceUnder(String hierarchy_id) {
		String[] tokens = hierarchy_id.split("\\.", 2);

		PSSInst result = findChild(tokens[0]);

		if (result != null) {
			if (tokens.length <= 1) {
				return result;
			} else {
				return result.findInstanceUnder(tokens[1]);
			}
		}

		return null;
	}

	public PSSInst findStaticInst(String hierarchiy_id) {
		// Currently, find enum items only.
		PSSInst res = null;
		PSSModel m = null;
		PSSInst base = this;
		while (m == null && base != null) {
			m = base.getTypeModel();
			base = base.m_parent;
		}
		while (m != null) {
			res = m.findStaticInst(hierarchiy_id);
			if (res != null)
				break;
			m = m.getParent();
		}
		return res;
	}

	/**
	 * Resolves a path used at this instance (see {@link PSSScope}).
	 *
	 * @param hierarchy_id a path of tokens separated by dots
	 * @param context      where the path is used
	 * @return the instance the path names, or null
	 */
	public PSSInst findInstance(String hierarchy_id, PSSScope.Context context) {
		return PSSScope.resolve(this, hierarchy_id, context);
	}

	public void init_up() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::init_up is not implemented");
	}

	public void init_down() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::init_down is not implemented");
	}

	/**
	 * Access the element at a specified index via "[]" operator.
	 *
	 * @param index an index
	 * @return
	 */
	public PSSInst indexOf(PSSVal index) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::indexOf is not implemented");
		return null;
	}

	public PSSDomain getInitDomain() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::getInitDomain is not implemented");
		return null;
	}

	public void assignDomain(PSSDomain domain) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::assignDomain is not implemented");
	}

	/** The current solution domain of a scalar field; null for an instance without one. */
	public PSSDomain getDomain() {
		return null;
	}

	/**
	 * Puts back a domain that getDomain() returned. Unlike assignDomain it reports no
	 * empty domain: the domain held before was a valid state.
	 */
	public void restoreDomain(PSSDomain domain) {
	}

	public void randomize() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::randomize is not implemented");
	}

	public void assign(PSSVal val) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::assign is not implemented");
	}

	public String toTargetCode() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::toTargetCode is not implemented");
		return "";
	}

	public PSSVal toVal() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::toVal is not implemented");
		return null;
	}

	public PSSConstraintList getConstraintList() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::getConstraintList is not implemented");
		return null;
	}

	public void addTraversal(PSSActionInst inst) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::addTraversal is not implemented");
	}

	public void addSequentialTraversal(PSSActionInst inst) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::addSequentialTraversal is not implemented");
	}

	public void traverseSubAction() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::traverseSubAction is not implemented");
	}

	public void inferSubAction() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::inferSubAction is not implemented");
	}

	public void inferAction(PSSInst parent) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::inferAction is not implemented");
	}

	public void collectConstraint(PSSSolver solver) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::collectConstraint is not implemented");
	}

	public void execute() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::execute is not implemented");
	}

	public void execute_kind(PSSExecKind kind) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::execute_kind is not implemented");
	}

	/**
	 * Returns {@code true} if this instance is read-only. If an instance is
	 * read-only, it cannot be updated by assignment statements but still can be
	 * updated via {@link #assign(PSSVal)}.
	 *
	 * @return {@code true} if this instance is read-only
	 */
	public boolean isReadOnly() {
		return m_readonly;
	}

	/**
	 * Returns {@code true} if this instance is random variable. If an instance is
	 * random variable, it should warning user if it was updated by assignment statements
	 * in pre_solve block.
	 *
	 * @return {@code true} if this instance is random variable
	 */
	public boolean isRandomable() {
		return m_rand;
	}

	/**
	 * Sets the read-only property of this instance. If an instance is read-only, it
	 * cannot be updated by assignment statements but still can be updated via
	 * {@link #assign(PSSVal)}.
	 *
	 * @param readonly {@code true} to make this instance read-only
	 */
	public void setReadOnly(boolean readonly) {
		m_readonly = readonly;
	}

	/**
	 * Registers a native method of this instance.
	 *
	 * @param m
	 */
	protected void addNativeMethod(NativeMethod m) {
		m_native_methods.put(m.m_name, m);
	}

	/**
	 * Invokes exposed methods of this instance.
	 *
	 * @param name the name of the method
	 * @param args the arguments passed to the method
	 * @return the result of the invocation
	 */
	public PSSInst evalMethod(String name, List<PSSVal> args) throws NoSuchMethodException {
		NativeMethod m = m_native_methods.get(name);
		if (m == null)
			throw new NoSuchMethodException();
		return m.eval(args);
	}

	public void dump(String indent) {
		System.out.println(indent + m_id + " <" + m_type + ">" + " " + this.getClass().getSimpleName());
		ArrayList<PSSInst> children = m_insts;
		for (int i = 0; i < children.size(); i++) {
			PSSInst inst = children.get(i);
			inst.dump(indent + "\t");
		}
	}

	public final String getId() {
		return m_id;
	}

	public final PSSInst getParent() {
		return m_parent;
	}

	public final String getType() {
		return m_type;
	}

	public final PSSModel getTypeDecl() {
		return m_type_decl;
	}

	public final boolean isConstant() {
		return m_constant;
	}

	public final boolean isRand() {
		return m_rand;
	}

	public final boolean isInitialized() {
		return m_initialized;
	}

	public final void setInitialized(boolean m_initialized) {
		this.m_initialized = m_initialized;
	}

	public final ArrayList<PSSInst> getInsts() {
		return m_insts;
	}
}
