package pssgen.frontend;

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

/** #48, PSS 2.0 4.2: the fields of one type have unique names. */
class FieldUniquenessTest {

	@TempDir
	Path tmp;

	/** exit status, stdout without the elapsed-time prefix, and test.s (null if not written) */
	private String[] run(String name) throws Exception {
		Path in = tmp.resolve("test.pss");
		try (InputStream s = getClass().getResourceAsStream("field_uniqueness/" + name + ".pss")) {
			assertNotNull(s, name);
			Files.copy(s, in, StandardCopyOption.REPLACE_EXISTING);
		}
		Path out = tmp.resolve("test.s");
		Files.deleteIfExists(out);
		String[] args = { "-seed", "1", in.toString(), "-o", out.toString(), "-root", "pss_top::root_a" };
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		int status;
		try (PrintStream o = new PrintStream(bo, true, "UTF-8")) {
			status = PSSGenMain.run(args, o, o);
		}
		String text = bo.toString("UTF-8").replaceAll("(?m)^\\[\\d+\\.\\d{3}s\\] ", "");
		String testS = Files.exists(out) ? new String(Files.readAllBytes(out), StandardCharsets.UTF_8) : null;
		return new String[] { String.valueOf(status), text, testS };
	}

	@Test
	void sameNameTwiceInOneDeclarationIsAnError() throws Exception {
		String[] r = run("fields_in_one_declaration");
		assertEquals("1", r[0], r[1]);
		assertTrue(r[1].endsWith("PSS-ERROR: [PSS 2.0 Section 4.2] 'a' is declared more than once in 's_t'\n"), r[1]);
	}

	/** Both branches of a compile if are visited; their fields are not duplicates. */
	@Test
	void sameNameInCompileIfBranchesAndInOtherTypesIsAccepted() throws Exception {
		String[] r = run("fields_in_compile_if");
		assertEquals("0", r[0], r[1]);
		assertEquals("v=4 c=1\n", r[2], r[1]);
	}
}
