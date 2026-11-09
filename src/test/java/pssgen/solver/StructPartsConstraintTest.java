package pssgen.solver;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import pssgen.cli.PSSGenMain;

/**
 * #55: the constraints of a struct type hold in every randomized struct, also in the
 * elements of a rand array and in a rand struct field of a rand struct, whatever the
 * seed and the solver.
 */
class StructPartsConstraintTest {

	@TempDir
	Path tmp;

	private void assertHoldsForSeeds(String solver) throws Exception {
		Path in = tmp.resolve("test.pss");
		try (InputStream s = getClass().getResourceAsStream("struct_parts.pss")) {
			assertNotNull(s);
			Files.copy(s, in, StandardCopyOption.REPLACE_EXISTING);
		}
		Path out = tmp.resolve("test.s");
		Pattern pair = Pattern.compile("(\\d+)<(\\d+)");
		for (int seed = 1; seed <= 20; seed++) {
			String[] args = { "-solver", solver, "-seed", String.valueOf(seed), in.toString(), "-o", out.toString(),
				"-root", "pss_top::root_a" };
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			int status;
			try (PrintStream o = new PrintStream(bo, true, "UTF-8")) {
				status = PSSGenMain.run(args, o, o);
			}
			assertEquals(0, status, "seed " + seed + ": " + bo.toString("UTF-8"));
			String text = new String(Files.readAllBytes(out), StandardCharsets.UTF_8);
			Matcher m = pair.matcher(text);
			int pairs = 0;
			while (m.find()) {
				assertTrue(Integer.parseInt(m.group(1)) < Integer.parseInt(m.group(2)), "seed " + seed + ": " + text);
				pairs++;
			}
			assertEquals(4, pairs, text);
		}
	}

	@Test
	void retrySolver() throws Exception {
		assertHoldsForSeeds("retry");
	}

	@Test
	void propagateSolver() throws Exception {
		assertHoldsForSeeds("propagate");
	}
}
