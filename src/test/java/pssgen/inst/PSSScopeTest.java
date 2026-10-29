package pssgen.inst;

import static org.junit.jupiter.api.Assertions.*;

import java.util.EnumSet;

import org.junit.jupiter.api.Test;

import pssgen.inst.PSSScope.Context;
import pssgen.inst.PSSScope.Step;

/** The lookup policy: which steps of the name lookup apply in which context. */
class PSSScopeTest {

	/**
	 * A name in the definition of a type does not reach the action that traverses it
	 * (#49); an inline constraint and the lookups of handles, flow objects and
	 * iterators do. Only the redeclaration check stops at the first local scope.
	 */
	@Test
	void onlyInlineConstraintsAndHandleLookupsReachTheTraversingAction() {
		EnumSet<Context> own_type = EnumSet.of(Context.MEMBER_CONSTRAINT, Context.ACTIVITY, Context.EXEC_BLOCK,
			Context.TEMPLATE, Context.FUNCTION_BODY, Context.DECLARATION);
		for (Context c : Context.values()) {
			if (c == Context.LOCAL_REDECLARATION)
				continue;
			EnumSet<Step> expected = EnumSet.complementOf(EnumSet.of(Step.STOP_AT_LOCAL_SCOPE));
			if (own_type.contains(c))
				expected.remove(Step.TRAVERSING_ACTIONS);
			assertEquals(expected, PSSScope.policy(c), c.name());
		}
	}

	@Test
	void redeclarationCheckStopsAtTheLocalScope() {
		assertEquals(EnumSet.complementOf(EnumSet.of(Step.STATIC_ITEMS)), PSSScope.policy(Context.LOCAL_REDECLARATION));
	}

	@Test
	void policiesCannotBeChangedByCallers() {
		assertThrows(UnsupportedOperationException.class,
			() -> PSSScope.policy(Context.MEMBER_CONSTRAINT).remove(Step.TRAVERSING_ACTIONS));
	}

	/** A path names the child of its first token's instance, found from the start's parent. */
	@Test
	void resolvesFirstTokenUpTheTreeAndTheRestDown() {
		PSSInst top = new PSSInst("top", "", false);
		PSSInst a = top.addInst(new PSSInst("a", "", false));
		PSSInst b = a.addInst(new PSSInst("b", "", false));
		PSSInst here = top.addInst(new PSSInst("here", "", false));
		assertSame(b, PSSScope.resolve(here, "a.b", Context.EXEC_BLOCK));
		assertSame(a, here.findInstance("a", Context.EXEC_BLOCK));
		assertNull(PSSScope.resolve(here, "c", Context.EXEC_BLOCK));
	}
}
