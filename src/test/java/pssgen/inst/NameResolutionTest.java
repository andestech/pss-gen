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

	/** #49: the LRM does not look a member constraint's names up in the traversing action. */
	@Test
	void knownBug49MemberConstraintSeesTraversingActionField() throws Exception {
		assertGenerates("traversing_member_constraint", "val=7");
	}

	/** #49: two traversals down, the root action's field is still found. */
	@Test
	void knownBug49MemberConstraintSeesFieldTwoTraversalsUp() throws Exception {
		assertGenerates("traversing_two_levels", "val=10");
	}

	/** The exec block analog of #49. */
	@Test
	void knownBug49ExecBlockSeesTraversingActionField() throws Exception {
		assertGenerates("traversing_exec_block", "v=8");
	}

	/** The target template analog of #49. */
	@Test
	void knownBug49TemplateSeesTraversingActionField() throws Exception {
		assertGenerates("traversing_template", "root_val=7");
	}

	/** PSS 2.0 17.1.3: an inline constraint may name fields of the traversing action. */
	@Test
	void inlineConstraintSeesTraversingActionField() throws Exception {
		assertGenerates("traversing_inline_constraint", "val=9");
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

	/** Not found, although the LRM resolves pkg::type::item. */
	@Test
	void enumItemQualifiedWithAnotherPackageIsNotFound() throws Exception {
		assertFails("package_statics_qualified", "PSS-ERROR: [PSSMemberPathElemExpression] M1 is not defined.");
	}

	/** Not found, although the LRM makes the items of an imported package visible. */
	@Test
	void enumItemOfAnImportedPackageIsNotFound() throws Exception {
		assertFails("package_statics_import", "PSS-ERROR: [PSSMemberPathElemExpression] M0 is not defined.");
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

	@Test
	void foreachSeesItsIteratorAndIndex() throws Exception {
		assertGenerates("foreach_iterators", "sum=8", "val=1", "val=3", "val=5");
	}

	// --- the redeclaration check of procedural data declarations

	@Test
	void functionLocalMayTakeTheNameOfAComponentField() throws Exception {
		assertGenerates("redeclaration_function", "r=1");
	}

	/** The check does not stop at an exec block: the action's field counts as a declaration of the scope. */
	@Test
	void execBlockLocalWithTheNameOfAnActionFieldIsARedeclaration() throws Exception {
		assertFails("redeclaration_exec_block", "PSS-ERROR: [PSSDataDeclProcStmt] x is redeclared in the same scope.");
	}

	/** The check also finds enum items. */
	@Test
	void localWithTheNameOfAnEnumItemIsARedeclaration() throws Exception {
		assertFails("redeclaration_enum_item", "PSS-ERROR: [PSSDataDeclProcStmt] RED is redeclared in the same scope.");
	}

	@Test
	void twoLocalsOfOneNameAreARedeclaration() throws Exception {
		assertFails("redeclaration_same_scope", "PSS-ERROR: [PSSDataDeclProcStmt] t is redeclared in the same scope.");
	}
}
