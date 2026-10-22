package pssgen.frontend;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import pssgen.cli.PSSGenMain;

/** Inputs with several constructs that PSSGenVisitor rejects report all of them at once. */
class PSSUnsupportedScanTest {

	@TempDir
	Path tmp;

	private String run(String... files) throws Exception {
		List<String> args = new ArrayList<>();
		for (int i = 0; i < files.length; i++) {
			Path f = tmp.resolve("f" + i + ".pss");
			Files.write(f, files[i].getBytes(StandardCharsets.UTF_8));
			args.add(f.toString());
		}
		args.add("-o");
		args.add(tmp.resolve("test.s").toString());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int status;
		try (PrintStream o = new PrintStream(out, true, "UTF-8")) {
			status = PSSGenMain.run(args.toArray(new String[0]), o, o);
		}
		String text = out.toString("UTF-8").replaceAll("(?m)^\\[\\d+\\.\\d{3}s\\] ", "[T] ");
		assertEquals(1, status, text);
		assertFalse(Files.exists(tmp.resolve("test.s")), text);
		return text.replace(tmp.toString() + "/", "");
	}

	private static final String TWO =
		"component pss_top {\n"
		+ "    typedef bit[8] byte_t;\n"
		+ "    action sub_a { }\n"
		+ "    action root_a {\n"
		+ "        activity {\n"
		+ "            parallel {\n"
		+ "                do sub_a;\n"
		+ "                do sub_a;\n"
		+ "            }\n"
		+ "        }\n"
		+ "    }\n"
		+ "}\n";

	@Test
	void twoUnsupportedReportedTogether() throws Exception {
		assertEquals("[T] PSS-ERROR: [UNSUPPORTED] 2 unsupported constructs:\n"
			+ "f0.pss:2:4: PSSGenVisitor.visitTypedef_declaration(): Syntax is not yet supported: 'typedefbit[8]byte_t;'\n"
			+ "f0.pss:6:12: PSSGenVisitor.visitActivity_parallel_stmt(): Syntax is not yet supported: 'parallel{dosub_a;dosub_a;}'\n",
			run(TWO));
	}

	@Test
	void singleUnsupportedKeepsTheVisitorMessage() throws Exception {
		String text = run("component pss_top {\n    typedef bit[8] byte_t;\n    action root_a { }\n}\n");
		assertTrue(text.matches("PSSGenVisitor\\.visitTypedef_declaration\\(\\), Ln\\d+: Syntax is not yet supported: 'typedefbit\\[8\\]byte_t;'\n"), text);
	}

	@Test
	void unsupportedAcrossFilesInFileOrder() throws Exception {
		String text = run("component pss_top {\n    action root_a {\n        activity { replicate (2) { do root_a; } }\n    }\n}\n",
			"extend component pss_top { }\n");
		assertTrue(text.startsWith("[T] PSS-ERROR: [UNSUPPORTED] 2 unsupported constructs:\nf0.pss:3:19: PSSGenVisitor.visitActivity_replicate_stmt()"), text);
		assertTrue(text.contains("\nf1.pss:1:0: PSSGenVisitor.visitExtend_stmt(): "), text);
	}

	@Test
	void nestedConstructIsNotReportedSeparately() throws Exception {
		String text = run("component pss_top {\n    action sub_a { }\n    action root_a {\n        activity {\n"
			+ "            parallel { parallel { do sub_a; } }\n            replicate (2) { do sub_a; }\n        }\n    }\n}\n");
		assertTrue(text.startsWith("[T] PSS-ERROR: [UNSUPPORTED] 2 unsupported constructs:\n"), text);
	}

	@Test
	void inheritanceAndAbstractActionsReportedTogether() throws Exception {
		assertEquals("[T] PSS-ERROR: [UNSUPPORTED] 3 unsupported constructs:\n"
			+ "f0.pss:1:18: PSSGenVisitor.visitComponent_super_spec(): Syntax is not yet supported: ':base_c'\n"
			+ "f0.pss:2:4: PSSGenVisitor.visitAbstract_action_declaration(): Syntax is not yet supported: 'abstractactionbase_a{}'\n"
			+ "f0.pss:3:17: PSSGenVisitor.visitStruct_super_spec(): Syntax is not yet supported: ':base_s'\n",
			run("component pss_top : base_c {\n    abstract action base_a { }\n    buffer der_b : base_s { }\n"
				+ "    struct base_s { }\n    action root_a { }\n}\ncomponent base_c { }\n"));
	}

	@Test
	void eachInheritanceConstructAloneKeepsTheVisitorMessage() throws Exception {
		String[][] cases = {
			{ "component base_c { }\ncomponent pss_top : base_c {\n    action root_a { }\n}\n", "visitComponent_super_spec", ":base_c" },
			{ "component pss_top {\n    action base_a { }\n    action root_a : base_a { }\n}\n", "visitAction_super_spec", ":base_a" },
			{ "component pss_top {\n    struct base_s { }\n    struct der_s : base_s { }\n    action root_a { }\n}\n",
				"visitStruct_super_spec", ":base_s" },
			{ "component pss_top {\n    abstract action base_a { }\n    action root_a { }\n}\n",
				"visitAbstract_action_declaration", "abstractactionbase_a{}" },
		};
		for (String[] c : cases) {
			String text = run(c[0]);
			assertEquals("PSSGenVisitor." + c[1] + "(), Ln" + text.replaceAll("(?s)^.*\\(\\), Ln(\\d+):.*$", "$1")
				+ ": Syntax is not yet supported: '" + c[2] + "'\n", text);
		}
	}
}
