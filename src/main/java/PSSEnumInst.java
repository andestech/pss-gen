import java.math.BigInteger;

public class PSSEnumInst extends PSSIntInst {

	public PSSEnumInst(String id, String type, PSSEnumModel type_decl, boolean rand) {
		super(id, rand, 32, true);

		/*
		 * PSS 2.0, Section 8.4 Enumeration types:
		 * When not initialized, the default value of an enum field shall be the first
		 * enum_item in the list. This is not necessarily the value 0 nor the enum_item
		 * with the minimum value.
		 */
		String[] items = type_decl.getEnumItems();
		if (items.length > 0) {
			m_val = new PSSIntVal(BigInteger.valueOf(type_decl.getEnumItemValue(items[0])));
		}
	}
}
