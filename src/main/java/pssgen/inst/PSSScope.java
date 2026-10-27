package pssgen.inst;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import pssgen.util.PSSMessage;

/**
 * Name resolution: what the first token of a path such as {@code a.b.c} names,
 * seen from an instance, in a given context.
 * <p>
 * The lookup is a walk up the instance tree from the instance where the name is
 * used. Each part of that walk is a {@link Step}; {@link #policy(Context)} says
 * which steps apply where a name is used. The walk itself is the same in every
 * context: the policy only switches steps on and off.
 */
public final class PSSScope {

	/** Where a name is used; the caller of {@link #resolve} says which. */
	public enum Context {
		/** a constraint declared in an action, struct or component, or the domain of a field */
		MEMBER_CONSTRAINT,
		/** an inline {@code with} constraint of a traversal */
		INLINE_CONSTRAINT,
		/** an expression of an activity statement */
		ACTIVITY,
		/** a statement of an exec block */
		EXEC_BLOCK,
		/** a {@code {{...}}} reference of a target template */
		TEMPLATE,
		/** a statement of a function body */
		FUNCTION_BODY,
		/** a field initializer, an array size, an enum item value or a parameter default */
		DECLARATION,
		/** a foreach looking up the iterator and index variables it declared */
		ITERATOR,
		/** a bind statement or action inference looking up a flow-object reference */
		FLOW_REF,
		/** a traversal of a named action handle looking up the handle */
		TRAVERSAL_HANDLE,
		/** the duplicate-declaration check of a procedural data declaration */
		LOCAL_REDECLARATION,
	}

	/** One part of the walk. */
	public enum Step {
		/** a first token {@code this}: the action that traverses the nearest action, else that action */
		KEYWORD_THIS,
		/** a first token {@code comp}: the {@code comp} child of the nearest action */
		KEYWORD_COMP,
		/** a first token {@code pss_top}: the nearest instance named pss_top */
		KEYWORD_PSS_TOP,
		/**
		 * the children of each instance from the start up to the nearest action, and of
		 * that action
		 */
		ENCLOSING_SCOPES,
		/** end the walk after the first instance that opens a scope ({@link PSSIScope}) */
		STOP_AT_LOCAL_SCOPE,
		/** from an action that another action traverses, go on to the traversing action */
		TRAVERSING_ACTIONS,
		/**
		 * from a component, go on above its outermost enclosing component, past the
		 * components in between
		 */
		ENCLOSING_COMPONENT,
		/** the tokens after the first, each a child of the instance before it */
		REST_OF_PATH,
		/**
		 * when nothing is found, the whole path as a static instance or an enum item of
		 * the type models from the start's type up
		 */
		STATIC_ITEMS,
	}

	private static final Map<Context, Set<Step>> POLICY = new EnumMap<Context, Set<Step>>(Context.class);

	static {
		Set<Step> all = EnumSet.complementOf(EnumSet.of(Step.STOP_AT_LOCAL_SCOPE));
		for (Context c : Context.values())
			POLICY.put(c, Collections.unmodifiableSet(EnumSet.copyOf(all)));
		POLICY.put(Context.LOCAL_REDECLARATION, Collections.unmodifiableSet(EnumSet.allOf(Step.class)));
	}

	private PSSScope() {
	}

	/**
	 * Returns the steps that apply in a context.
	 *
	 * @param c a context
	 * @return the steps of the walk in context c
	 */
	public static Set<Step> policy(Context c) {
		return POLICY.get(c);
	}

	/**
	 * Resolves a path used at an instance.
	 *
	 * @param start the instance where the path is used
	 * @param path  a path of tokens separated by dots
	 * @param c     the context of the use
	 * @return the instance the path names, or null
	 */
	public static PSSInst resolve(PSSInst start, String path, Context c) {
		Set<Step> steps = policy(c);
		String[] tokens = path.split("\\.", 2);

		PSSInst inst = null;
		if (steps.contains(Step.KEYWORD_THIS) && "this".equals(tokens[0])) {
			inst = thisAction(start);
		} else if (steps.contains(Step.KEYWORD_COMP) && "comp".equals(tokens[0])) {
			PSSInst action = nearestAction(start);
			if (action == null)
				PSSMessage.Error("", "The reference \"comp\" should be used in an action.");
			inst = action.findChild("comp");
		} else if (steps.contains(Step.KEYWORD_PSS_TOP) && "pss_top".equals(tokens[0])) {
			PSSInst top = start;
			while (top != null && !top.getId().equals("pss_top"))
				top = top.getParent();
			inst = top;
		} else if (steps.contains(Step.ENCLOSING_SCOPES)) {
			inst = enclosingScopes(start, tokens[0], steps);
		}

		if (steps.contains(Step.REST_OF_PATH) && inst != null && tokens.length > 1)
			inst = inst.findInstanceUnder(tokens[1]);

		if (steps.contains(Step.STATIC_ITEMS) && inst == null)
			inst = start.findStaticInst(path);

		return inst;
	}

	/**
	 * PSS 2.0 Section 17.1.3: fields of the containing action that the traversed
	 * sub-action shadows are reached with {@code this}.
	 */
	private static PSSInst thisAction(PSSInst start) {
		PSSInst action_inner = nearestAction(start);
		PSSInst action_outer = action_inner == null ? null : nearestAction(action_inner.getParent());
		if (action_outer != null)
			return action_outer;
		if (action_inner != null)
			return action_inner;
		PSSMessage.Error("", "The reference \"this\" should be used in an action.");
		return null;
	}

	/** The nearest action instance at or above inst, or null. */
	private static PSSInst nearestAction(PSSInst inst) {
		while (!(inst instanceof PSSActionInst) && inst != null)
			inst = inst.getParent();
		return inst;
	}

	private static PSSInst enclosingScopes(PSSInst start, String id, Set<Step> steps) {
		PSSInst inst = null;
		PSSInst base = start;
		while (inst == null && base != null) {
			inst = base.findInstanceUnder(id);

			if (steps.contains(Step.STOP_AT_LOCAL_SCOPE) && base instanceof PSSIScope)
				break;

			// An expression in the definition of a component instance that is declared
			// inside another component instance is evaluated in the nested instance.
			if (base instanceof PSSComponentInst) {
				if (!steps.contains(Step.ENCLOSING_COMPONENT))
					break;
				PSSInst tmp = base;
				while (tmp != null) {
					tmp = tmp.getParent();
					if (tmp instanceof PSSComponentInst)
						base = tmp;
				}
			}

			// An action reaches component data through "comp.", "this." or "pss_top.".
			if (base instanceof PSSActionInst) {
				if (!steps.contains(Step.TRAVERSING_ACTIONS) || nearestAction(base.getParent()) == null)
					break;
			}

			base = base.getParent();
		}
		return inst;
	}
}
