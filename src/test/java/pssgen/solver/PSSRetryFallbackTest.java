package pssgen.solver;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import pssgen.cli.PSSGenMain;

/** The default solver: retry first, propagate when retry fails, the retry failure when both fail. */
class PSSRetryFallbackTest {

	@TempDir
	Path tmp;

	/** status, stdout (time prefix removed), test.s and raw stdout of one generation of root_a's fields */
	private String[] run(String fields, String template, String... options) throws Exception {
		Path in = tmp.resolve("test.pss");
		Files.write(in, ("component pss_top {\n    action root_a {\n" + fields + "\n        exec body ASM = \"\"\"\n"
			+ template + "\n\"\"\";\n    }\n}\n").getBytes(StandardCharsets.UTF_8));
		Path out = tmp.resolve("test.s");
		Files.deleteIfExists(out);
		List<String> args = new ArrayList<>(Arrays.asList(options));
		args.addAll(Arrays.asList(in.toString(), "-o", out.toString()));
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		int status;
		try (PrintStream o = new PrintStream(bo, true, "UTF-8")) {
			status = PSSGenMain.run(args.toArray(new String[0]), o, o);
		}
		String raw = bo.toString("UTF-8");
		String text = raw.replaceAll("(?m)^\\[\\d+\\.\\d{3}s\\] ", "");
		String testS = Files.exists(out) ? new String(Files.readAllBytes(out), StandardCharsets.UTF_8).trim() : null;
		return new String[] { String.valueOf(status), text, testS, raw };
	}

	@Test
	void retryFailureIsSolvedByPropagate() throws Exception {
		// retry empties the domain of a whenever its first draw of b is 0
		int fallbacks = 0;
		for (int seed = 1; seed <= 40; seed++) {
			String[] r = run("        rand bit [3] a;\n        rand bit [3] b;\n        constraint a < b;", "{{a}} {{b}}",
				"-info", "-seed", String.valueOf(seed));
			assertEquals("0", r[0], r[1]);
			String[] v = r[2].split(" ");
			assertTrue(Integer.parseInt(v[0]) < Integer.parseInt(v[1]), r[2]);
			if (r[1].contains("PSS-INFO: retry solver: 'pss_top.root_a.a' is over-constrained; solving again with -solver propagate\n"))
				fallbacks++;
		}
		assertTrue(fallbacks > 0);
	}

	@Test
	void modelThatRetrySolvesKeepsTheRetryOutput() throws Exception {
		String fields = "        rand bit [16] a;\n        rand bit [16] b;\n        constraint a < b;";
		String[] expected = { "40120 63821", "47386 49967", "1530 59172" };
		for (int seed = 1; seed <= 3; seed++) {
			String[] r = run(fields, "{{a}} {{b}}", "-info", "-seed", String.valueOf(seed));
			assertEquals("0", r[0], r[1]);
			assertEquals(expected[seed - 1], r[2]);
			assertFalse(r[1].contains("retry solver:"), r[1]);
		}
	}

	@Test
	void whenBothFailTheRetryFailureIsReported() throws Exception {
		// propagate alone reports OVERCONSTRAINT on y; the default reports what retry reported
		String[] r = run("        rand bit [4] x;\n        rand bit [4] y;\n        constraint x + y > 40;", "{{x}}", "-info");
		assertEquals("1", r[0], r[1]);
		assertTrue(r[1].contains("PSS-INFO: retry solver: randomize fail; solving again with -solver propagate\n"), r[1]);
		assertTrue(r[1].endsWith("randomize fail\n"), r[1]);
		assertFalse(r[1].contains("PSS-ERROR"), r[1]);
		assertNull(r[2]);
	}

	@Test
	void anEmptyDomainOfRetryIsReportedWithTheTimeOfTheSecondAttempt() throws Exception {
		String[] r = run("        rand bit [3] a;\n        rand bit [3] b;\n        constraint a < b;\n        constraint b == 0;",
			"{{a}}", "-info");
		assertEquals("1", r[0], r[1]);
		Matcher info = Pattern.compile("(?m)^\\[(\\d+\\.\\d{3})s\\] PSS-INFO: retry solver: ").matcher(r[3]);
		Matcher error = Pattern.compile("(?m)^\\[(\\d+\\.\\d{3})s\\] PSS-ERROR: \\[OVERCONSTRAINT\\] ").matcher(r[3]);
		assertTrue(info.find(), r[3]);
		assertTrue(error.find(), r[3]);
		assertTrue(Double.parseDouble(error.group(1)) >= Double.parseDouble(info.group(1)), r[3]);
		assertNull(r[2]);
	}
}
