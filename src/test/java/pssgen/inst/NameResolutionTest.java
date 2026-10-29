package pssgen.inst;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import pssgen.cli.PSSGenMain;

/**
 * What a name means where it is used: one input per way the name lookup can go
 * (PSSInst.findInstance, findInstanceUnder, findStaticInst, findChild). Each test
 * records what PSSGen does today; a test named knownBug... records a result that
 * the LRM does not allow, and names the issue.
 */
class NameResolutionTest {

	@TempDir
	Path tmp;

	/** exit status, stdout without the elapsed-time prefix, and test.s (null if not written) */
	private String[] run(String name) throws Exception {
		return run(name, "pss_top::root_a");
	}

	private String[] run(String name, String root) throws Exception {
		Path in = tmp.resolve("test.pss");
		try (InputStream s = getClass().getResourceAsStream("name_resolution/" + name + ".pss")) {
			assertNotNull(s, name);
			Files.copy(s, in, StandardCopyOption.REPLACE_EXISTING);
		}
		Path out = tmp.resolve("test.s");
		Files.deleteIfExists(out);
		String[] args = { "-seed", "1", in.toString(), "-o", out.toString(), "-root", root };
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		int status;
		try (PrintStream o = new PrintStream(bo, true, "UTF-8")) {
			status = PSSGenMain.run(args, o, o);
		}
		String text = bo.toString("UTF-8").replaceAll("(?m)^\\[\\d+\\.\\d{3}s\\] ", "");
		String testS = Files.exists(out) ? new String(Files.readAllBytes(out), StandardCharsets.UTF_8) : null;
		return new String[] { String.valueOf(status), text, testS };
	}

	/** The input generates test.s with exactly these lines. */
	private void assertGenerates(String name, String... lines) throws Exception {
		String[] r = run(name);
		assertEquals("0", r[0], r[1]);
		assertEquals(String.join("\n", lines) + "\n", r[2], r[1]);
	}

	/** The input fails with exit status 1 and this last line on stdout. */
	private void assertFails(String name, String lastLine) throws Exception {
		String[] r = run(name);
		assertEquals("1", r[0], r[1]);
		String[] out = r[1].split("\n");
		assertEquals(lastLine, out[out.length - 1], r[1]);
	}

	// --- a field of the action itself

	@Test
	void ownFieldFromMemberConstraintExecBlockAndTemplate() throws Exception {
		assertGenerates("own_field", "x=4 y=5");
	}

	@Test
	void subActionFieldShadowsTraversingActionField() throws Exception {
		assertGenerates("shadowed_by_sub_action", "v=1 w=1");
	}

	// --- a field of the traversing action

	/** #49, PSS 2.0 21.3 b): a member constraint's names resolve in its own type, not in the traversing action. */
	@Test
	void memberConstraintDoesNotSeeTraversingActionField() throws Exception {
		assertFails("traversing_member_constraint", "PSS-ERROR: [PSSMemberPathElemExpression] root_val is not defined.");
	}

	/** #49: two traversals down, the root action's field is not found either. */
	@Test
	void memberConstraintDoesNotSeeFieldTwoTraversalsUp() throws Exception {
		assertFails("traversing_two_levels", "PSS-ERROR: [PSSMemberPathElemExpression] root_val is not defined.");
	}

	/** The exec block analog of #49. */
	@Test
	void execBlockDoesNotSeeTraversingActionField() throws Exception {
		assertFails("traversing_exec_block", "PSS-ERROR: [PSSMemberPathElemExpression] root_val is not defined.");
	}

	/** The target template analog of #49. */
	@Test
	void templateDoesNotSeeTraversingActionField() throws Exception {
		assertFails("traversing_template", "PSS-ERROR: [PSSMemberPathElemExpression] root_val is not defined.");
	}

	/** The activity analog of #49. */
	@Test
	void activityDoesNotSeeTraversingActionField() throws Exception {
		assertFails("traversing_activity", "PSS-ERROR: [PSSMemberPathElemExpression] n is not defined.");
	}

	/** The field initializer analog of #49. */
	@Test
	void fieldInitializerDoesNotSeeTraversingActionField() throws Exception {
		assertFails("traversing_declaration", "PSS-ERROR: [PSSMemberPathElemExpression] root_val is not defined.");
	}

	/** PSS 2.0 17.1.3: an inline constraint may name fields of the traversing action. */
	@Test
	void inlineConstraintSeesTraversingActionField() throws Exception {
		assertGenerates("traversing_inline_constraint", "val=9");
	}

	/** PSS 2.0 17.1.3: only the containing action, not the action that traverses it in turn. */
	@Test
	void inlineConstraintDoesNotSeeFieldTwoTraversalsUp() throws Exception {
		assertFails("traversing_inline_two_levels", "PSS-ERROR: [PSSMemberPathElemExpression] root_val is not defined.");
	}

	// --- this, comp, pss_top

	@Test
	void thisIsTheTraversingActionOrTheRootAction() throws Exception {
		assertGenerates("this_keyword", "root this.v=9", "sub v=1 w=9");
	}

	@Test
	void thisOutsideAnActionIsAnError() throws Exception {
		assertFails("this_outside_action", "PSS-ERROR: [] The reference \"this\" should be used in an action.");
	}

	@Test
	void compIsTheComponentOfTheAction() throws Exception {
		assertGenerates("comp_keyword", "x=6 cv=6");
	}

	@Test
	void compOutsideAnActionIsAnError() throws Exception {
		assertFails("comp_outside_action", "PSS-ERROR: [] The reference \"comp\" should be used in an action.");
	}

	@Test
	void pssTopIsTheTopComponent() throws Exception {
		assertGenerates("pss_top_keyword", "x=11 tv=11");
	}

	/** An action does not see its component's fields without comp., this. or pss_top. */
	@Test
	void componentFieldWithoutCompIsNotFound() throws Exception {
		assertFails("component_field_unqualified", "PSS-ERROR: [PSSMemberPathElemExpression] cv is not defined.");
	}

	// --- paths, enum items, package statics

	@Test
	void restOfPathGoesDownTheInstanceTree() throws Exception {
		assertGenerates("multi_token_path", "b=13");
	}

	@Test
	void enumItemsUnqualifiedAndQualifiedWithTheType() throws Exception {
		assertGenerates("enum_items", "c=5 d=6");
	}

	@Test
	void enumItemsOfTheEnclosingPackage() throws Exception {
		String[] r = run("package_statics", "p::pss_top::root_a");
		assertEquals("0", r[0], r[1]);
		assertEquals("m=2 n=1\n", r[2], r[1]);
	}

	/** PSS 2.0 21.1.3: pkg::type::item from a component outside the package. */
	@Test
	void enumItemQualifiedWithAnotherPackage() throws Exception {
		assertGenerates("package_statics_qualified", "m=2");
	}

	/** PSS 2.0 21.1.3, 21.3 a): an item of an enum of a wildcard-imported package, unqualified. */
	@Test
	void enumItemOfAWildcardImportedPackage() throws Exception {
		assertGenerates("package_statics_import", "n=1");
	}

	/** PSS 2.0 21.1.3: "import statements are not transitive." */
	@Test
	void enumItemOfAPackageImportedByAnImportedPackageIsNotFound() throws Exception {
		assertFails("package_statics_not_transitive", "PSS-ERROR: [PSSMemberPathElemExpression] B is not defined.");
	}

	// --- components

	@Test
	void nestedComponentInitializerSeesItsOwnFields() throws Exception {
		assertGenerates("component_nested", "b=3");
	}

	/** The lookup jumps from a nested component past the enclosing components. */
	@Test
	void nestedComponentInitializerDoesNotSeeTheEnclosingComponent() throws Exception {
		assertFails("component_nested_outer", "PSS-ERROR: [PSSMemberPathElemExpression] tv is not defined.");
	}

	// --- function bodies, activities, iterators

	@Test
	void functionBodySeesParameterLocalAndComponentField() throws Exception {
		assertGenerates("function_body", "r=8");
	}

	@Test
	void activitySeesActionFieldAndHandle() throws Exception {
		assertGenerates("activity_names", "val=1", "val=1", "val=2");
	}

	/** PSS 2.0 17.4.6: a handle's fields are read only after the activity traverses it. */
	@Test
	void activityDoesNotReadAHandleBeforeItsTraversal() throws Exception {
		assertFails("handle_not_yet_traversed", "PSS-ERROR: [PSSMemberPathElemExpression] c.y is not defined.");
	}

	@Test
	void foreachSeesItsIteratorAndIndex() throws Exception {
		assertGenerates("foreach_iterators", "sum=8", "val=1", "val=3", "val=5");
	}

	// --- the redeclaration check of procedural data declarations

	@Test
	void functionLocalMayTakeTheNameOfAComponentField() throws Exception {
		assertGenerates("redeclaration_function", "r=1");
	}

	/** PSS 2.0 22.7.1: an exec block is a nested scope; its local hides the action's field there. */
	@Test
	void execBlockLocalMayTakeTheNameOfAnActionField() throws Exception {
		assertGenerates("redeclaration_exec_block", "x=2");
	}

	/** A local is no member of a namespace (PSS 2.0 21.3), so it may take the name of an enum item. */
	@Test
	void localMayTakeTheNameOfAnEnumItem() throws Exception {
		assertGenerates("redeclaration_enum_item", "r=1");
	}

	@Test
	void twoLocalsOfOneNameAreARedeclaration() throws Exception {
		assertFails("redeclaration_same_scope", "PSS-ERROR: [PSSDataDeclProcStmt] t is redeclared in the same scope.");
	}
}
