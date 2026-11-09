package pssgen.model;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import pssgen.cli.PSSGenMain;
import pssgen.value.PSSBoolVal;
import pssgen.value.PSSStringVal;

/** The type models that all runs share are not changed by a run. */
class SharedTypeModelTest {

	@TempDir
	Path tmp;

	private void generate(String pss) throws Exception {
		Path in = tmp.resolve("test.pss");
		Files.write(in, pss.getBytes(StandardCharsets.UTF_8));
		PSSGenMain.main(new String[] { in.toString(), "-o", tmp.resolve("test.s").toString(), "-root", "pss_top::root_a" });
	}

	@Test
	void boolAndStringDeclarationsDoNotTouchTheValueTypes() throws Exception {
		generate("component pss_top { action root_a { bool b = true; string s = \"x\"; "
			+ "exec body ASM = \"\"\"{{b}} {{s}}\"\"\"; } }");
		assertNull(PSSBoolModel.getInstance().getParent());
		assertNull(PSSStringModel.getInstance().getParent());
		assertTrue(PSSBoolModel.getInstance().getChildren().isEmpty());
		assertEquals("bool", PSSBoolModel.getInstance().getHierarchyId());
		assertSame(PSSBoolModel.getInstance(), PSSBoolVal.TRUE.getTypeModel());
		assertSame(PSSStringModel.getInstance(), new PSSStringVal("y").getTypeModel());
	}

	@Test
	void castOfABoolLiteralDoesNotRetypeTheSharedBooleans() throws Exception {
		generate("component pss_top { action root_a { bit [4] n = 0; "
			+ "exec post_solve { n = (bit [4]) true; } exec body ASM = \"\"\"{{n}}\"\"\"; } }");
		assertEquals("1", Files.readString(tmp.resolve("test.s")).trim());
		assertSame(PSSBoolModel.getInstance(), PSSBoolVal.TRUE.getTypeModel());
		assertSame(PSSBoolModel.getInstance(), PSSBoolVal.FALSE.getTypeModel());
	}

	@Test
	void eachBoolLiteralIsANewValue() {
		assertNotSame(PSSBoolVal.literal("true"), PSSBoolVal.literal("true"));
		assertTrue(PSSBoolVal.literal("TRUE").toBool());
		assertFalse(PSSBoolVal.literal("false").toBool());
	}
}
