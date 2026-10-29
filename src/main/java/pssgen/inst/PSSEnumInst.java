package pssgen.inst;

import java.math.BigInteger;

import pssgen.model.PSSEnumModel;
import pssgen.value.PSSIntVal;

public class PSSEnumInst extends PSSIntInst {

	/** the enum type, kept apart from the type model of PSSInst, which name lookup walks */
	private final PSSEnumModel m_enum_model;

	public PSSEnumInst(String id, String type, PSSEnumModel type_decl, boolean rand) {
		super(id, rand, 32, true);
		m_enum_model = type_decl;

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

	/**
	 * Returns the name of the enum item this field holds.
	 *
	 * @return the item name, or null when no item has the value of this field
	 */
	public String getItemName() {
		BigInteger v = m_val.toBigInteger();
		for (String item : m_enum_model.getEnumItems()) {
			if (BigInteger.valueOf(m_enum_model.getEnumItemValue(item)).equals(v))
				return item;
		}
		return null;
	}
}
