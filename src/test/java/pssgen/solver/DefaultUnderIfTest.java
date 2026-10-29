package pssgen.solver;

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
 * #31, PSS 2.0 17.1.10 f): a default constraint under if / else, nested ifs included,
 * applies only in the branch its constant conditions select, on both solvers.
 */
class DefaultUnderIfTest {

	@TempDir
	Path tmp;

	private String generate(String solver, int seed) throws Exception {
		Path in = tmp.resolve("test.pss");
		try (InputStream s = getClass().getResourceAsStream("default_under_if.pss")) {
			assertNotNull(s);
			Files.copy(s, in, StandardCopyOption.REPLACE_EXISTING);
		}
		Path out = tmp.resolve("test.s");
		String[] args = { "-solver", solver, "-seed", String.valueOf(seed), in.toString(), "-o", out.toString(),
			"-root", "pss_top::root_a" };
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		int status;
		try (PrintStream o = new PrintStream(bo, true, "UTF-8")) {
			status = PSSGenMain.run(args, o, o);
		}
		assertEquals(0, status, bo.toString("UTF-8"));
		return new String(Files.readAllBytes(out), StandardCharsets.UTF_8);
	}

	@Test
	void onlyTheSelectedBranchesDefaultsApply() throws Exception {
		for (String solver : new String[] { "retry", "propagate" }) {
			for (int seed = 1; seed <= 10; seed++) {
				String text = generate(solver, seed);
				assertTrue(text.startsWith("a=1 b=4 c="), solver + " seed " + seed + ": " + text);
				assertTrue(text.matches("(?s)a=1 b=4 c=[78].*"), solver + " seed " + seed + ": " + text);
			}
		}
	}
}
