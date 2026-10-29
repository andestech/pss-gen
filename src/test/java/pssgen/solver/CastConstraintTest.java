package pssgen.solver;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import pssgen.cli.PSSGenMain;

/**
 * #44: a cast in an equality constraint, in-line and member, is solved like any other
 * operand, whatever the seed and the solver.
 */
class CastConstraintTest {

	@TempDir
	Path tmp;

	private static final String INPUT = String.join("\n",
		"component pss_top {",
		"    action root_a {",
		"        rand bit msb;",
		"        activity {",
		"            do example with { val == ((bit [4])msb << 3); };",
		"        }",
		"        exec body ASM = \"\"\"msb={{msb}}\"\"\";",
		"    }",
		"    action example {",
		"        rand bit [4] val;",
		"        rand bit [8] w;",
		"        rand bit [4] low;",
		"        constraint low == (bit [4]) w;",
		"        exec body ASM = \"\"\" val={{val}} w={{w}} low={{low}}\"\"\";",
		"    }",
		"}",
		"");

	private void assertSolved(String solver) throws Exception {
		Path in = tmp.resolve("test.pss");
		Files.write(in, INPUT.getBytes(StandardCharsets.UTF_8));
		Path out = tmp.resolve("test.s");
		Pattern p = Pattern.compile("msb=(\\d+)\\s+val=(\\d+) w=(\\d+) low=(\\d+)");
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
			Matcher m = p.matcher(text);
			assertTrue(m.find(), text);
			assertEquals(Integer.parseInt(m.group(1)) * 8, Integer.parseInt(m.group(2)), text);
			assertEquals(Integer.parseInt(m.group(3)) % 16, Integer.parseInt(m.group(4)), text);
		}
	}

	@Test
	void retrySolver() throws Exception {
		assertSolved("retry");
	}

	@Test
	void propagateSolver() throws Exception {
		assertSolved("propagate");
	}
}
