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

/** A constraint on a[i] with a random index i holds on both solvers, whatever the seed. */
class RandomIndexTest {

	@TempDir
	Path tmp;

	@Test
	void constraintOnAnElementAtARandomIndexHolds() throws Exception {
		Path in = tmp.resolve("test.pss");
		try (InputStream s = getClass().getResourceAsStream("random_index.pss")) {
			assertNotNull(s);
			Files.copy(s, in, StandardCopyOption.REPLACE_EXISTING);
		}
		Path out = tmp.resolve("test.s");
		Pattern p = Pattern.compile("i=(\\d+) a=(\\d+),(\\d+),(\\d+),(\\d+)");
		for (String solver : new String[] { "retry", "propagate" }) {
			for (int seed = 1; seed <= 20; seed++) {
				String[] args = { "-solver", solver, "-seed", String.valueOf(seed), in.toString(), "-o", out.toString(),
					"-root", "pss_top::root_a" };
				ByteArrayOutputStream bo = new ByteArrayOutputStream();
				int status;
				try (PrintStream o = new PrintStream(bo, true, "UTF-8")) {
					status = PSSGenMain.run(args, o, o);
				}
				assertEquals(0, status, solver + " seed " + seed + ": " + bo.toString("UTF-8"));
				Matcher m = p.matcher(new String(Files.readAllBytes(out), StandardCharsets.UTF_8));
				assertTrue(m.find());
				int i = Integer.parseInt(m.group(1));
				assertTrue(Integer.parseInt(m.group(2 + i)) < 3, solver + " seed " + seed + ": " + m.group());
			}
		}
	}
}
