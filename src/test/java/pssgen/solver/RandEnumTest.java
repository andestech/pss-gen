package pssgen.solver;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import pssgen.cli.PSSGenMain;

/**
 * PSS 2.0 Section 8.4: a rand enum field holds one of its items, whether it is
 * unconstrained, partly constrained or an array element, whatever the seed and the
 * solver.
 */
class RandEnumTest {

	@TempDir
	Path tmp;

	private static final List<String> ITEMS = Arrays.asList("5", "7", "9");

	private void assertItemsForSeeds(String solver) throws Exception {
		Path in = tmp.resolve("test.pss");
		try (InputStream s = getClass().getResourceAsStream("rand_enum.pss")) {
			assertNotNull(s);
			Files.copy(s, in, StandardCopyOption.REPLACE_EXISTING);
		}
		Path out = tmp.resolve("test.s");
		for (int seed = 1; seed <= 20; seed++) {
			String[] args = { "-solver", solver, "-seed", String.valueOf(seed), in.toString(), "-o", out.toString(),
				"-root", "pss_top::root_a" };
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			int status;
			try (PrintStream o = new PrintStream(bo, true, "UTF-8")) {
				status = PSSGenMain.run(args, o, o);
			}
			assertEquals(0, status, "seed " + seed + ": " + bo.toString("UTF-8"));
			String[] lines = new String(Files.readAllBytes(out), StandardCharsets.UTF_8).trim().split("\n");
			String[] v = lines[0].trim().split(" ");
			assertEquals("v", v[0]);
			for (int i = 1; i < v.length; i++)
				assertTrue(ITEMS.contains(v[i]), "seed " + seed + ": " + lines[0]);
			String g = lines[1].trim().split(" ")[1];
			assertTrue(g.equals("7") || g.equals("9"), "seed " + seed + ": " + lines[1]);
			String h = lines[2].trim().split(" ")[1];
			assertTrue(h.equals("7") || h.equals("9"), "seed " + seed + ": " + lines[2]);
		}
	}

	@Test
	void retrySolver() throws Exception {
		assertItemsForSeeds("retry");
	}

	@Test
	void propagateSolver() throws Exception {
		assertItemsForSeeds("propagate");
	}
}
