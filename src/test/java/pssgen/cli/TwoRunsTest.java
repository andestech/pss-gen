package pssgen.cli;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Generations run one after the other in one JVM give exactly what a separate
 * command line gives: the same exit status, test.s and stdout and stderr (apart from
 * the elapsed-time prefix of the messages).
 */
class TwoRunsTest {

	/** what one generation produced */
	static final class Result {
		final int status;
		final String out;
		final String err;
		final byte[] testS;

		Result(int status, String out, String err, byte[] testS) {
			this.status = status;
			this.out = normalize(out);
			this.err = err;
			this.testS = testS;
		}

		@Override
		public String toString() {
			return "status " + status + "\n--- stdout\n" + out + "--- stderr\n" + err;
		}
	}

	/** One input directory and the arguments that run it. */
	static final class Job {
		final Path dir;
		final List<String> args;

		Job(Path dir, String... options) {
			this.dir = dir;
			List<String> a = new ArrayList<>(Arrays.asList(options));
			a.add(dir.resolve("test.pss").toString());
			a.add("-o");
			a.add(dir.resolve("test.s").toString());
			a.add("-root");
			a.add("pss_top::root_a");
			this.args = a;
		}
	}

	@TempDir
	Path tmp;

	Job a, b, c;

	@BeforeEach
	void copyInputs() throws IOException {
		a = new Job(input("a"), "-info", "-set_random_limit", "50", "-seed", "11");
		b = new Job(input("b"), "-set_random_limit", "100", "-info", "-debug", "-seed", "22");
		c = new Job(input("c"), "-info", "-seed", "33");
	}

	private Path input(String name) throws IOException {
		Path dir = Files.createDirectories(tmp.resolve(name));
		try (InputStream in = getClass().getResourceAsStream("two_runs/" + name + "/test.pss")) {
			assertNotNull(in, "resource two_runs/" + name);
			Files.copy(in, dir.resolve("test.pss"), StandardCopyOption.REPLACE_EXISTING);
		}
		return dir;
	}

	private static String normalize(String text) {
		return text.replaceAll("(?m)^\\[\\d+\\.\\d{3}s\\] (?=PSS-(INFO|WARNING|ERROR|DEBUG): )", "[T] ");
	}

	private static byte[] takeTestS(Path dir) throws IOException {
		Path f = dir.resolve("test.s");
		if (!Files.exists(f)) {
			return null;
		}
		byte[] bytes = Files.readAllBytes(f);
		Files.delete(f);
		return bytes;
	}

	/** The generation as a separate command line: java ... PSSGenMain args. */
	private static Result cli(Job job) throws Exception {
		List<String> cmd = new ArrayList<>();
		cmd.add(Path.of(System.getProperty("java.home"), "bin", "java").toString());
		cmd.add("-cp");
		cmd.add(System.getProperty("java.class.path"));
		cmd.add("PSSGenMain");
		cmd.addAll(job.args);
		Path outFile = job.dir.resolve("cli.out");
		Path errFile = job.dir.resolve("cli.err");
		Process p = new ProcessBuilder(cmd).directory(job.dir.toFile())
			.redirectOutput(outFile.toFile()).redirectError(errFile.toFile()).start();
		int status = p.waitFor();
		String out = Files.readString(outFile, StandardCharsets.UTF_8);
		String err = Files.readString(errFile, StandardCharsets.UTF_8);
		Files.delete(outFile);
		Files.delete(errFile);
		return new Result(status, out, err, takeTestS(job.dir));
	}

	/** The generation in this JVM. */
	private static Result inProcess(Job job) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayOutputStream err = new ByteArrayOutputStream();
		int status;
		try (PrintStream o = new PrintStream(out, true, "UTF-8"); PrintStream e = new PrintStream(err, true, "UTF-8")) {
			status = PSSGenMain.run(job.args.toArray(new String[0]), o, e);
		}
		return new Result(status, out.toString("UTF-8"), err.toString("UTF-8"), takeTestS(job.dir));
	}

	private static void assertSameResult(Result expected, Result actual, String what) {
		assertEquals(expected.toString(), actual.toString(), what);
		assertArrayEquals(expected.testS, actual.testS, what + ": test.s");
	}

	private void inOrder(Job first, Job second) throws Exception {
		Result cli1 = cli(first);
		Result cli2 = cli(second);
		Result run1 = inProcess(first);
		Result run2 = inProcess(second);
		assertSameResult(cli1, run1, "first run");
		assertSameResult(cli2, run2, "second run");
	}

	@Test
	void runAThenBMatchesCli() throws Exception {
		inOrder(a, b);
	}

	@Test
	void runBThenAMatchesCli() throws Exception {
		inOrder(b, a);
	}

	@Test
	void inputsExerciseWhatTheyAreFor() throws Exception {
		Result ra = inProcess(a);
		assertEquals(0, ra.status, ra.toString());
		assertTrue(ra.out.contains("[T] PSS-INFO: Change retry limit: 2000 -> 50"), ra.out);
		String testS = new String(ra.testS, StandardCharsets.UTF_8);
		assertTrue(testS.contains("from_bool=1"), testS);
		Result rb = inProcess(b);
		assertEquals(0, rb.status, rb.toString());
		assertFalse(rb.out.contains("Change retry limit"), "-info comes after -set_random_limit in run B");
		assertTrue(rb.out.contains("[T] PSS-DEBUG: solve iteration: "), rb.out);
	}

	@Test
	void secondRunAfterFailedRunMatchesCli() throws Exception {
		Result cliC = cli(c);
		assertEquals(1, cliC.status, cliC.toString());
		assertTrue(cliC.out.contains("PSS-ERROR: [PSSMemberPathElemExpression] missing is not defined."), cliC.out);
		inOrder(c, b);
	}

	@Test
	void sameRunTwiceIsIdentical() throws Exception {
		Result first = inProcess(a);
		Result second = inProcess(a);
		assertSameResult(first, second, "run A again");
	}
}
