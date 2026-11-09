package pssgen.inst;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import pssgen.model.PSSEnumModel;
import pssgen.value.PSSIntVal;

public class PSSEnumInst extends PSSIntInst {

	/** the enum type, kept apart from the type model of PSSInst, which name lookup walks */
	private final PSSEnumModel m_enum_model;

	public PSSEnumInst(String id, String type, PSSEnumModel type_decl, boolean rand) {
		super(id, rand, type_decl.fieldWidth(), true);
		m_enum_model = type_decl;

		/*
		 * PSS 2.0, Section 8.4 Enumeration types:
		 * When not initialized, the default value of an enum field shall be the first
		 * enum_item in the list. This is not necessarily the value 0 nor the enum_item
		 * with the minimum value.
		 */
		String[] items = type_decl.getEnumItems();
		if (items.length > 0) {
			m_val = new PSSIntVal(type_decl.getEnumItemValue(items[0]));
		}
	}

	/**
	 * PSS 2.0 Section 8.4: an enum field holds one of its items. The draw from the
	 * domain is kept, so the random stream does not change; a drawn value that is no
	 * item is replaced by one of the items in the domain, chosen by the drawn value.
	 */
	@Override
	public void randomize_one() {
		super.randomize_one();
		if (getItemName() != null)
			return;
		List<BigInteger> items = new ArrayList<BigInteger>();
		for (String item : m_enum_model.getEnumItems()) {
			PSSIntVal v = new PSSIntVal(m_enum_model.getEnumItemValue(item));
			if (getDomain().InRange(v).toBool())
				items.add(v.toBigInteger());
		}
		if (!items.isEmpty())
			m_val = new PSSIntVal(items.get(m_val.toBigInteger().mod(BigInteger.valueOf(items.size())).intValue()));
	}

	/**
	 * Returns the value of an item of this field's enum type.
	 *
	 * @param name an item name
	 * @return the value, or null when the type has no such item
	 */
	public PSSIntVal itemValue(String name) {
		for (String item : m_enum_model.getEnumItems()) {
			if (item.equals(name))
				return new PSSIntVal(m_enum_model.getEnumItemValue(item));
		}
		return null;
	}

	/**
	 * Returns the name of the enum item this field holds.
	 *
	 * @return the item name, or null when no item has the value of this field
	 */
	public String getItemName() {
		BigInteger v = m_val.toBigInteger();
		for (String item : m_enum_model.getEnumItems()) {
			if (m_enum_model.getEnumItemValue(item).equals(v))
				return item;
		}
		return null;
	}
}
