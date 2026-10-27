package pssgen;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.spi.ToolProvider;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import pssgen.cli.PSSGenMain;

/**
 * The package layering of PSSGen:
 *
 * <pre>
 * cli      -&gt; frontend, core, parser, util
 * frontend -&gt; core, parser, util
 * core     -&gt; util       core = model, inst, value, expr, solver, activity, proc
 * parser   -&gt; (none)
 * util     -&gt; (none)
 * </pre>
 *
 * The core packages make one cluster: the type model creates its instances, an
 * instance points back to its model and holds values, values refer to instances,
 * and expressions, constraints, activities and statements are evaluated against
 * instances. Inside the cluster only the package pairs listed in {@link #ALLOWED}
 * may depend on each other, so the cluster does not grow new cycles. The check
 * reads the compiled classes with jdeps and fails on any other package edge.
 */
class PackageLayeringTest {

	private static final List<String> CORE = Arrays.asList("model", "inst", "value", "expr", "solver", "activity",
		"proc");

	/** package -> the packages it may depend on (besides itself) */
	private static final Map<String, Set<String>> ALLOWED = new HashMap<String, Set<String>>();

	private static void allow(String from, String... to) {
		ALLOWED.computeIfAbsent(from, k -> new TreeSet<String>()).addAll(Arrays.asList(to));
	}

	static {
		allow("cli", "frontend", "parser", "util");
		allow("cli", CORE.toArray(new String[0]));
		allow("frontend", "parser", "util");
		allow("frontend", CORE.toArray(new String[0]));
		allow("parser");
		allow("util");
		// The core cluster: the package pairs that exist; a new pair needs a reason.
		allow("activity", "expr", "inst", "model", "solver", "util", "value");
		allow("expr", "inst", "model", "solver", "util", "value");
		allow("inst", "activity", "expr", "model", "proc", "solver", "util", "value");
		allow("model", "activity", "expr", "inst", "proc", "solver", "util", "value");
		allow("proc", "expr", "inst", "model", "util", "value");
		allow("solver", "expr", "inst", "model", "util", "value");
		allow("value", "inst", "model", "util");
	}

	private static final Pattern EDGE = Pattern.compile("^\\s+pssgen\\.(\\S+)\\s+->\\s+pssgen\\.(\\S+)\\s");

	@Test
	void packageEdgesFollowTheLayering() throws Exception {
		Path classes = Paths.get(PSSGenMain.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		ToolProvider jdeps = ToolProvider.findFirst("jdeps")
			.orElseThrow(() -> new AssertionError("jdeps is not available in this JDK"));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayOutputStream err = new ByteArrayOutputStream();
		int rc;
		try (PrintStream o = new PrintStream(out, true, "UTF-8"); PrintStream e = new PrintStream(err, true, "UTF-8")) {
			rc = jdeps.run(o, e, "-verbose:class", "-filter:none", classes.toString());
		}
		assertEquals(0, rc, err.toString("UTF-8"));

		Map<String, Set<String>> bad = new TreeMap<String, Set<String>>();
		Set<String> packages = new HashSet<String>();
		for (String line : out.toString("UTF-8").split("\n")) {
			Matcher m = EDGE.matcher(line);
			if (!m.find())
				continue;
			String from = pkg(m.group(1));
			String to = pkg(m.group(2));
			packages.add(from);
			if (from.equals(to) || ALLOWED.getOrDefault(from, new TreeSet<String>()).contains(to))
				continue;
			bad.computeIfAbsent(from + " -> " + to, k -> new TreeSet<String>())
				.add("pssgen." + m.group(1) + " -> pssgen." + m.group(2));
		}
		assertTrue(packages.containsAll(CORE), "jdeps output not understood: " + packages);
		StringBuilder msg = new StringBuilder();
		for (Map.Entry<String, Set<String>> e : bad.entrySet())
			msg.append(e.getKey()).append(":\n  ").append(String.join("\n  ", e.getValue())).append('\n');
		assertTrue(bad.isEmpty(), "package edges outside the layering:\n" + msg);
	}

	/** The sub-package of a class name below pssgen, or "" for a class directly in pssgen. */
	private static String pkg(String name) {
		int dot = name.indexOf('.');
		return dot < 0 ? "" : name.substring(0, dot);
	}
}
