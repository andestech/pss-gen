package pssgen.value;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import pssgen.model.PSSBoolModel;

/** Characterization of PSSBoolVal. */
class PSSBoolValTest {

	@Test
	void valueOfReturnsTheSharedConstants() {
		assertSame(PSSBoolVal.TRUE, PSSBoolVal.valueOf(true));
		assertSame(PSSBoolVal.FALSE, PSSBoolVal.valueOf("false"));
		assertSame(PSSBoolVal.TRUE, PSSBoolVal.valueOf("TRUE"));
		assertSame(PSSBoolVal.FALSE, PSSBoolVal.valueOf("yes"));
	}

	@Test
	void typeIsTheBoolModel() {
		assertTrue(PSSBoolVal.TRUE.getTypeModel() instanceof PSSBoolModel);
	}

	@Test
	void textAndIntegerValue() {
		assertEquals("true", PSSBoolVal.TRUE.getText());
		assertEquals(1, PSSBoolVal.TRUE.toBigInteger().intValue());
		assertEquals(0, PSSBoolVal.FALSE.toBigInteger().intValue());
	}
}
