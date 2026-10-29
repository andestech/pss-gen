package pssgen.solver;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import pssgen.cli.PSSGenMain;

/**
 * The warning for a constraint whose operands are all non-random: one text per
 * relation operator, none for other expressions. Each constraint is validated
 * twice, so each warning comes out twice.
 */
class ConstraintWarningTest {

	@TempDir
	Path tmp;

	private static final String IN = "PSS-WARNING: Use 'in' expression to non-random variable in constraint block is meaningless: ";
	private static final String NOT_IN = "PSS-WARNING: Use '%s' expression without random variable in constraint block is meaningless: ";

	private List<String> warnings() throws Exception {
		Path in = tmp.resolve("test.pss");
		try (InputStream s = getClass().getResourceAsStream("constraint_warnings.pss")) {
			assertNotNull(s);
			Files.copy(s, in, StandardCopyOption.REPLACE_EXISTING);
		}
		String[] args = { "-seed", "1", in.toString(), "-o", tmp.resolve("test.s").toString(), "-root", "pss_top::root_a" };
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		int status;
		try (PrintStream o = new PrintStream(bo, true, "UTF-8")) {
			status = PSSGenMain.run(args, o, o);
		}
		String text = bo.toString("UTF-8").replaceAll("(?m)^\\[\\d+\\.\\d{3}s\\] ", "");
		assertEquals(0, status, text);
		List<String> res = new ArrayList<String>();
		for (String l : text.split("\n")) {
			if (l.startsWith("PSS-WARNING: "))
				res.add(l);
		}
		return res;
	}

	@Test
	void eachRelationOperatorHasItsWarning() throws Exception {
		List<String> once = List.of(
			IN + "a in [ 4..6 ]",
			String.format(NOT_IN, "==") + "a==5",
			String.format(NOT_IN, "!=") + "a!=4",
			String.format(NOT_IN, ">") + "a>1",
			String.format(NOT_IN, ">=") + "a>=5",
			String.format(NOT_IN, "<") + "a<9",
			String.format(NOT_IN, "<=") + "a<=5");
		List<String> expected = new ArrayList<String>(once);
		expected.addAll(once);
		assertEquals(expected, warnings());
	}

	/** The text of 'a != 4' reads 'a!=4'. */
	@Test
	void notEqualTextReadsAsNotEqual() throws Exception {
		assertTrue(warnings().contains(String.format(NOT_IN, "!=") + "a!=4"));
	}
}
