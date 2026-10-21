package pssgen.value;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import pssgen.model.PSSIntModel;

/**
 * Characterization of PSSIntVal: the values below are what the code computes, not
 * what the LRM asks for. A test that fails after a change shows a behaviour change.
 */
class PSSIntValTest {

	private static BigInteger big(long v) {
		return BigInteger.valueOf(v);
	}

	private static PSSIntVal bits(long v, int width) {
		return new PSSIntVal(big(v), width, false);
	}

	private static PSSIntVal ints(long v, int width) {
		return new PSSIntVal(big(v), width, true);
	}

	private static long val(PSSVal v) {
		return v.toBigInteger().longValueExact();
	}

	private static PSSIntModel model(PSSVal v) {
		return (PSSIntModel) v.getTypeModel();
	}

	@Test
	void unsignedConstructionTruncatesToWidth() {
		assertEquals(2, val(bits(18, 4)));
		assertEquals(255, val(bits(-1, 8)));
		assertEquals(0, val(bits(256, 8)));
	}

	@Test
	void signedConstructionDoesNotWrap() {
		assertEquals(200, val(ints(200, 8)));
		assertEquals(-300, val(ints(-300, 8)));
	}

	@Test
	void defaultModelIsSigned65Bits() {
		PSSIntVal v = new PSSIntVal(-5);
		assertEquals(PSSIntModel.DEFAULT_INT_SIZE, model(v).getSize());
		assertTrue(model(v).isSigned());
		assertEquals(-5, val(v));
		assertEquals("int", model(v).getText());
	}

	@Test
	void unsigned65BitConstructionDoesNotTruncate() {
		BigInteger huge = BigInteger.ONE.shiftLeft(70);
		assertEquals(huge, new PSSIntVal(huge, PSSIntModel.DEFAULT_INT_SIZE, false).toBigInteger());
	}

	@Test
	void castToNarrowerWidthTruncatesAndChangesType() {
		PSSIntVal v = bits(0xAB, 8);
		v.setTypeModel(new PSSIntModel(4, false));
		assertEquals(0xB, val(v));
		assertEquals(4, model(v).getSize());
	}

	@Test
	void castToNarrowerSignedTypeGivesTheUnsignedBitPattern() {
		PSSIntVal v = bits(15, 8);
		v.setTypeModel(new PSSIntModel(4, true));
		assertEquals(15, val(v));
		assertTrue(model(v).isSigned());
	}

	@Test
	void castOfNegativeToWiderWidthZeroPads() {
		PSSIntVal v = ints(-1, 4);
		v.setTypeModel(new PSSIntModel(8, true));
		assertEquals(255, val(v));
		assertEquals(8, model(v).getSize());
	}

	@Test
	void castToSameWidthKeepsValueAndType() {
		PSSIntVal v = ints(-1, 8);
		v.setTypeModel(new PSSIntModel(8, false));
		assertEquals(-1, val(v));
		assertTrue(model(v).isSigned(), "the type model is not replaced at equal width");
	}

	@Test
	void arithmeticUsesTheDefaultModelWithoutTruncation() {
		PSSVal sum = bits(15, 4).Add(bits(15, 4));
		assertEquals(30, val(sum));
		assertEquals(PSSIntModel.DEFAULT_INT_SIZE, model(sum).getSize());
		assertEquals(-1, val(bits(0, 4).Sub(bits(1, 4))));
		assertEquals(225, val(bits(15, 4).Mul(15)));
	}

	@Test
	void divisionTruncatesTowardZeroAndModIsNonNegative() {
		assertEquals(-3, val(new PSSIntVal(-7).Div(2)));
		assertEquals(2, val(new PSSIntVal(-7).Mod(3)));
		assertThrows(ArithmeticException.class, () -> new PSSIntVal(1).Div(0));
	}

	@Test
	void leftShiftKeepsTheLeftOperandType() {
		assertEquals(14, val(bits(15, 4).LeftShift(1)));
		assertEquals(30, val(ints(15, 4).LeftShift(1)));
	}

	@Test
	void rightShiftIsArithmetic() {
		assertEquals(-4, val(new PSSIntVal(-7).RightShift(1)));
		assertEquals(3, val(bits(15, 4).RightShift(2)));
	}

	@Test
	void negativeShiftAmountIsAnError() {
		RuntimeException e = assertThrows(RuntimeException.class, () -> new PSSIntVal(1).LeftShift(-1));
		assertEquals("shift operand should not be negative value: -1", e.getMessage());
	}

	@Test
	void bitwiseNotIsSignedAndUntruncated() {
		assertEquals(-6, val(bits(5, 4).BitwiseNot()));
		assertEquals(0b0001, val(bits(0b0101, 4).BitwiseAnd(0b0011)));
		assertEquals(0b0111, val(bits(0b0101, 4).BitwiseOr(0b0011)));
		assertEquals(0b0110, val(bits(0b0101, 4).BitwiseXor(bits(0b0011, 4))));
	}

	@Test
	void extractReturnsAnUnsignedSlice() {
		PSSVal s = ints(-1, 8).extract(5, 2);
		assertEquals(15, val(s));
		assertEquals(4, model(s).getSize());
		assertFalse(model(s).isSigned());
		assertThrows(RuntimeException.class, () -> new PSSIntVal(1).extract(1, 2));
	}

	@Test
	void comparisonsReturnTheSharedBooleans() {
		assertSame(PSSBoolVal.TRUE, new PSSIntVal(3).GreaterThan(2));
		assertSame(PSSBoolVal.FALSE, new PSSIntVal(3).LessThan(2));
		assertSame(PSSBoolVal.TRUE, bits(255, 8).Equal(ints(255, 9)));
		assertSame(PSSBoolVal.TRUE, bits(-1, 8).NotEqual(new PSSIntVal(-1)));
	}

	@Test
	void textIsDecimal() {
		assertEquals("-12", new PSSIntVal(-12).getText());
		assertEquals("-12", new PSSIntVal(-12).toTargetCode());
		assertEquals("255", bits(-1, 8).toTargetCode());
	}

	@Test
	void joinWithRangeOrValue() {
		PSSVal r = new PSSRangeVal(big(1), big(5));
		assertEquals(3, val(new PSSIntVal(3).join(r)));
		assertNull(new PSSIntVal(6).join(r));
		assertNull(new PSSIntVal(6).join(new PSSIntVal(7)));
	}
}
