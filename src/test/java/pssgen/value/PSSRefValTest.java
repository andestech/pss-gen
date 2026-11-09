package pssgen.value;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import pssgen.inst.PSSListInst;
import pssgen.model.PSSIntModel;
import pssgen.model.PSSRefModel;

/** Characterization of PSSRefVal. */
class PSSRefValTest {

	@Test
	void derefGivesTheValueOfTheReferencedInstance() {
		PSSListInst l = PSSListInst.ofElementDataType("l", new PSSIntModel(32, true));
		PSSVal v = PSSRefVal.deref(new PSSRefVal(new PSSRefModel(l.getTypeModel()), l));
		assertTrue(v instanceof PSSListVal);
		assertTrue(((PSSListVal) v).isEmpty());
	}

	@Test
	void derefKeepsAValueThatIsNotAReference() {
		PSSVal one = PSSIntVal.ONE();
		assertSame(one, PSSRefVal.deref(one));
		assertNull(PSSRefVal.deref(null));
	}

	@Test
	void derefOfANullReferenceThrows() {
		PSSRefVal r = new PSSRefVal(new PSSRefModel(new PSSIntModel(32, true)), null);
		assertThrows(NullPointerException.class, () -> PSSRefVal.deref(r));
	}
}
