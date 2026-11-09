package pssgen.frontend;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import pssgen.cli.PSSGenMain;
import pssgen.model.PSSModel;
import pssgen.util.PSSException;
import pssgen.util.PSSRun;

/**
 * The model that the frontend builds for each case is the one recorded in its
 * model.txt: the same object graph (see {@link ModelDump}), the
 * same messages, the same failure. With PSSGEN_UPDATE_DUMPS=1 in the environment
 * the test writes the files instead of comparing.
 */
class ModelDumpTest {

	private static final Path CASES = Paths.get("src/test/resources/pssgen/frontend/model_dump");

	/**
	 * The cases: each directory under model_dump/ with a test.pss, and each input
	 * that model_dump/repo_cases.txt names (a path from the repository root), whose
	 * model.txt is in model_dump/ under the path with '/' replaced by '__'.
	 */
	static Stream<String> cases() throws IOException {
		List<String> names = new ArrayList<String>();
		try (Stream<Path> s = Files.list(CASES)) {
			s.filter(p -> Files.isRegularFile(p.resolve("test.pss"))).map(p -> p.getFileName().toString())
				.sorted().forEach(names::add);
		}
		for (String line : Files.readAllLines(CASES.resolve("repo_cases.txt"), StandardCharsets.UTF_8)) {
			if (!line.trim().isEmpty() && !line.startsWith("#"))
				names.add(line.trim());
		}
		assertFalse(names.isEmpty());
		return names.stream();
	}

	/** The input of a case. */
	private static Path input(String name) {
		return name.contains("/") ? Paths.get(name).toAbsolutePath() : CASES.resolve(name).resolve("test.pss").toAbsolutePath();
	}

	/** The recorded model of a case. */
	private static Path golden(String name) {
		return CASES.resolve(name.replace("/", "__")).resolve("model.txt");
	}

	/** The model of in, preceded by the messages of the build and followed by its failure, if any. */
	static String build(Path in) throws Exception {
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		StringBuilder res = new StringBuilder();
		try (PrintStream o = new PrintStream(bo, true, "UTF-8")) {
			PSSRun run = new PSSRun(o);
			ArrayList<String> files = new ArrayList<String>();
			files.add(in.toString());
			String model = null;
			String failure = null;
			try {
				ArrayList<ParseTree> trees = PSSGenMain.parseFiles(run, files);
				PSSModel m = PSSGenMain.createModel(run, trees);
				model = ModelDump.of(m);
			} catch (PSSException e) {
				e.setStartTime(run.log().startTime());
				failure = e.getReport();
			}
			res.append("--- messages\n").append(bo.toString("UTF-8"));
			if (model != null)
				res.append("--- model\n").append(model);
			if (failure != null)
				res.append("--- failure\n").append(failure);
		}
		// The elapsed time and the Fatal source line are not part of the model.
		return res.toString().replace(in.toString(), "<input>").replaceAll("(?m)^\\[\\d+\\.\\d{3}s\\] ", "[T] ")
			.replaceAll("\\(\\), Ln\\d+: ", "(), Ln#: ");
	}

	@ParameterizedTest
	@MethodSource("cases")
	void modelIsUnchanged(String name) throws Exception {
		String actual = build(input(name));
		Path golden = golden(name);
		if ("1".equals(System.getenv("PSSGEN_UPDATE_DUMPS"))) {
			Files.createDirectories(golden.getParent());
			Files.write(golden, actual.getBytes(StandardCharsets.UTF_8));
			return;
		}
		String expected = new String(Files.readAllBytes(golden), StandardCharsets.UTF_8);
		assertEquals(expected, actual, name);
	}
}
