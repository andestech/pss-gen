import java.util.*;

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

	public String m_id;
	public PSSInst m_parent;
	public String m_type;
	public PSSModel m_type_decl;
	public boolean m_constant;
	public boolean m_rand;
	public boolean m_initialized;

	private boolean m_traversed;
	public ArrayList<PSSInst> m_insts;
	Map<String, NativeMethod> m_native_methods = new HashMap<String, NativeMethod>();

	/** {@code true} if this instance is read-only */
	boolean m_readonly;

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
	 * Returns the descendants of this instance.
	 *
	 * @return the descendants of this instance
	 */
	public Set<PSSInst> getDescendants() {
		Set<PSSInst> res = new HashSet<PSSInst>();
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

	public PSSInst getComponentInst() {
		if (this instanceof PSSComponentInst) {
			return this;
		} else if (m_parent != null) {
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
			m = m.m_parent;
		}
		return res;
	}

	public PSSInst findInstance(String hierarchy_id, boolean local_scope) {
		/* The current resolution order may not follow PSS 2.0. */

		String[] tokens = hierarchy_id.split("\\.", 2);

		/* Find the first token */
		PSSInst inst = null;
		if ("this".equals(tokens[0])) {
			/* PSS 2.0 Section 17.1.3 */
			/*
			 * In cases where the containing-action fields are shadowed (masked) by fields
			 * of the traversed sub-action, they can be explicitly accessed using the
			 * built-in variable this.
			 */
			PSSInst action_inner = this;
			PSSInst action_outer = null;
			while (!(action_inner instanceof PSSActionInst) && action_inner != null)
				action_inner = action_inner.m_parent;
			if (action_inner != null) {
				action_outer = action_inner.m_parent;
				while (!(action_outer instanceof PSSActionInst) && action_outer != null)
					action_outer = action_outer.m_parent;
			}
			if (action_outer != null) {
				// Return the containing action of the sub-action
				inst = action_outer;
			} else if (action_inner != null) {
				// There is no containing action.
				inst = action_inner;
			} else {
				PSSMessage.Error("", "The reference \"this\" should be used in an action.");
			}
		} else if ("comp".equals(tokens[0])) {
			PSSInst action = this;
			while (!(action instanceof PSSActionInst) && action != null)
				action = action.m_parent;
			if (action == null)
				PSSMessage.Error("", "The reference \"comp\" should be used in an action.");
			inst = action.findChild("comp");
		} else if ("pss_top".equals(tokens[0])) {
			PSSInst top = this;
			while (top != null && !top.m_id.equals("pss_top"))
				top = top.m_parent;
			if (top != null)
				inst = top;
		} else {
			PSSInst base = this;
			while (inst == null && base != null) {
				inst = base.findInstanceUnder(tokens[0]);

				// If local_scope is true, search in the local scope.
				if (local_scope && base instanceof PSSIScope)
					break;

				/*
				 * An expression in a nested component instance may be evaluated. In this case,
				 * the expression is in the definition of a component instance while the
				 * component instance is declared inside another component instance.
				 */
				if (base instanceof PSSComponentInst) {
					PSSInst tmp = base;
					while (tmp != null) {
						tmp = tmp.m_parent;
						if (tmp instanceof PSSComponentInst)
							base = tmp;
					}
				}

				/*
				 * Component data should be accessed by "comp.", "this.", or "pss_top." in an
				 * action.
				 */
				if (base instanceof PSSActionInst) {
					// Check if base is a sub-action
					PSSInst a = base.m_parent;
					while (!(a instanceof PSSActionInst) && a != null)
						a = a.m_parent;
					if (a == null)
						break;
				}

				base = base.m_parent;
			}
		}

		if (inst != null && tokens.length > 1)
			inst = inst.findInstanceUnder(tokens[1]);

		if (inst == null) {
			inst = findStaticInst(hierarchy_id);
		}

		return inst;
	}

	public PSSInst findInstance(String hierarchy_id) {
		return findInstance(hierarchy_id, false);
	}

	public void init_up() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::init_up is not impelemented");
	}

	public void init_down() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::init_down is not impelemented");
	}

	/**
	 * Access the element at a specified index via "[]" operator.
	 *
	 * @param index an index
	 * @return
	 */
	public PSSInst indexOf(PSSVal index) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::indexOf is not impelemented");
		return null;
	}

	public PSSDomain getInitDomain() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::getInitDomain is not impelemented");
		return null;
	}

	void assignDomain(PSSDomain domain) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::assignDomain is not impelemented");
	}

	public void randomize() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::randomize is not impelemented");
	}

	public void assign(PSSVal val) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::assign is not impelemented");
	}

	public String toTargetCode() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::toTargetCode is not impelemented");
		return "";
	}

	public PSSVal toVal() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::toVal is not impelemented");
		return null;
	}

	public PSSConstraintList getConstraintList() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::getConstraintList is not impelemented");
		return null;
	}

	public void addTraversal(PSSActionInst inst) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::addTraversal is not impelemented");
	}

	public void addSequentialTraversal(PSSActionInst inst) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::addSequentialTraversal is not impelemented");
	}

	public void traverseSubAction() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::traverseSubAction is not impelemented");
	}

	public void inferSubAction() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::inferSubAction is not impelemented");
	}

	public void inferAction(PSSInst parent) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::inferAction is not impelemented");
	}

	public void collectConstraint(PSSSolver solver) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::collectConstraint is not impelemented");
	}

	public void execute() {
		PSSMessage.Fatal(getClass().getSimpleName() + "::execute is not impelemented");
	}

	public void execute_kind(PSSExecKind kind) {
		PSSMessage.Fatal(getClass().getSimpleName() + "::execute_kind is not impelemented");
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

}
