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

/** -solver propagate: domains narrowed before the first draw, re-draws of violated variables only. */
class PSSPropagateSolverTest {

	@TempDir
	Path tmp;

	/** status, stdout (time prefix removed) and test.s of one generation of root_a's fields */
	private String[] run(String fields, String template, String... options) throws Exception {
		Path in = tmp.resolve("test.pss");
		Files.write(in, ("component pss_top {\n    action root_a {\n" + fields + "\n        exec body ASM = \"\"\"\n"
			+ template + "\n\"\"\";\n    }\n}\n").getBytes(StandardCharsets.UTF_8));
		Path out = tmp.resolve("test.s");
		Files.deleteIfExists(out);
		List<String> args = new ArrayList<>(Arrays.asList(options));
		args.addAll(Arrays.asList("-solver", "propagate", in.toString(), "-o", out.toString()));
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		int status;
		try (PrintStream o = new PrintStream(bo, true, "UTF-8")) {
			status = PSSGenMain.run(args.toArray(new String[0]), o, o);
		}
		String text = bo.toString("UTF-8").replaceAll("(?m)^\\[\\d+\\.\\d{3}s\\] ", "");
		String testS = Files.exists(out) ? new String(Files.readAllBytes(out), StandardCharsets.UTF_8).trim() : null;
		return new String[] { String.valueOf(status), text, testS };
	}

	private static long[] numbers(String s) {
		return Arrays.stream(s.trim().split("\\s+")).mapToLong(Long::parseLong).toArray();
	}

	@Test
	void sumNarrowsBothSides() throws Exception {
		String[] r = run("        rand bit [8] a;\n        rand bit [8] b;\n        constraint a + b == 400;", "{{a}} {{b}}", "-info");
		assertEquals("0", r[0], r[1]);
		assertTrue(r[1].contains("PSS-INFO: a domain: {145..255}\n"), r[1]);
		assertTrue(r[1].contains("PSS-INFO: b domain: {145..255}\n"), r[1]);
		long[] v = numbers(r[2]);
		assertEquals(400, v[0] + v[1]);
	}

	@Test
	void chainNarrowsToTheOnlyRoom() throws Exception {
		String[] r = run("        rand bit [2] a;\n        rand bit [2] b;\n        rand bit [2] c;\n        rand bit [2] d;\n"
			+ "        constraint a < b && b < c;\n        constraint c < d;", "{{a}} {{b}} {{c}} {{d}}", "-info");
		assertEquals("0", r[0], r[1]);
		assertEquals("0 1 2 3", r[2]);
		assertTrue(r[1].contains("PSS-INFO: a domain: {0}\n"), r[1]);
	}

	@Test
	void inListAndReversedComparison() throws Exception {
		String[] r = run("        rand bit [4] x;\n        rand bit [4] y;\n        constraint x in [3, 7..9];\n"
			+ "        constraint 8 > y;\n        constraint y + 1 == x;", "{{x}} {{y}}", "-info");
		assertEquals("0", r[0], r[1]);
		// bounds, not holes: x keeps 3 and 7..8; y gets the bounds 2..7 of x - 1
		assertTrue(r[1].contains("PSS-INFO: x domain: {3, 7..8}\n"), r[1]);
		assertTrue(r[1].contains("PSS-INFO: y domain: {2..7}\n"), r[1]);
		long[] v = numbers(r[2]);
		assertEquals(v[0], v[1] + 1);
	}

	@Test
	void equalityOfTwoWideFieldsTakesOneDraw() throws Exception {
		String[] r = run("        rand bit [16] a;\n        rand bit [16] b;\n        constraint a == b;", "{{a}} {{b}}", "-debug");
		assertEquals("0", r[0], r[1]);
		assertTrue(r[1].contains("PSS-DEBUG: solve iteration: 1\n"), r[1]);
		long[] v = numbers(r[2]);
		assertEquals(v[0], v[1]);
	}

	@Test
	void emptyDomainIsOverconstrainedBeforeAnyDraw() throws Exception {
		String[] r = run("        rand bit [4] x;\n        rand bit [4] y;\n        constraint x + y > 40;", "{{x}}");
		assertEquals("1", r[0], r[1]);
		assertTrue(r[1].contains("PSS-ERROR: [OVERCONSTRAINT] "), r[1]);
		assertNull(r[2]);
	}

	@Test
	void onlyTheVariablesOfViolatedConstraintsAreDrawnAgain() throws Exception {
		for (int seed = 1; seed <= 5; seed++) {
			String[] r = run("        rand bit [3] a;\n        rand bit [3] b;\n        rand bit [4] c;\n"
				+ "        constraint a * b == 6;\n        constraint c == 5;", "{{a}} {{b}} {{c}}", "-debug_random", "-seed",
				String.valueOf(seed));
			assertEquals("0", r[0], r[1]);
			// c: once in the full first draw and once in its sequential draw, never again;
			// a (no linear comparison): once in each round
			assertEquals(2, count(r[1], "c' = "), r[1]);
			int rounds = Integer.parseInt(find(r[1], "solve iteration: (\\d+)"));
			assertEquals(rounds, count(r[1], "a' = "), r[1]);
			long[] v = numbers(r[2]);
			assertEquals(6, v[0] * v[1]);
		}
	}

	@Test
	void stringsAndIfConstraintsGetTheDeducedDomains() throws Exception {
		String[] r = run("        rand string s;\n        rand int data;\n        int flag = 1;\n"
			+ "        constraint s == \"hello\";\n        constraint { if (flag == 1) data == 100; else data == 200; }",
			"{{s}} {{data}}", "-set_random_limit", "5");
		assertEquals("0", r[0], r[1]);
		assertEquals("hello 100", r[2]);
	}

	@Test
	void unknownSolverIsAnOptionError() throws Exception {
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		int status;
		try (PrintStream o = new PrintStream(bo, true, "UTF-8")) {
			status = PSSGenMain.run(new String[] { "-solver", "exact", "x.pss", "-o", "x.s" }, o, o);
		}
		assertEquals(1, status);
		assertTrue(bo.toString("UTF-8").contains("PSS-ERROR: [OPT] invalid value 'exact' for option '-solver': expected retry or propagate"));
	}

	private static int count(String text, String what) {
		int n = 0;
		for (int i = text.indexOf(what); i >= 0; i = text.indexOf(what, i + 1))
			n++;
		return n;
	}

	private static String find(String text, String re) {
		Matcher m = Pattern.compile(re).matcher(text);
		assertTrue(m.find(), text);
		return m.group(1);
	}
}
