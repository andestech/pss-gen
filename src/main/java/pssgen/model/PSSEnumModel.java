package pssgen.model;

import java.math.BigInteger;
import java.util.*;

import pssgen.inst.PSSEnumInst;
import pssgen.inst.PSSInst;
import pssgen.inst.PSSIntInst;
import pssgen.value.PSSIntVal;
import pssgen.value.PSSVal;

public class PSSEnumModel extends PSSModel {

	/** PSS 2.0 Section 8.4: each item has the integer value of its constant expression */
	private LinkedHashMap<String, BigInteger> m_items;

	private BigInteger m_default_val;

	public PSSEnumModel(String id) {
		super(id);
		m_items = new LinkedHashMap<String, BigInteger>();
		m_default_val = BigInteger.ZERO;
	}

	/**
	 * Returns items in this enum.
	 *
	 * @return items in this enum.
	 */
	public String[] getEnumItems() {
		return m_items.keySet().toArray(new String[0]);
	}

	/**
	 * Returns the value of a specified item in this enum.
	 *
	 * @param item an item in this enum
	 * @return the value of item in this enum
	 */
	public BigInteger getEnumItemValue(String item) {
		return m_items.get(item);
	}

	public void addEnumItem(String id) {
		m_items.put(id, m_default_val);
		m_default_val = m_default_val.add(BigInteger.ONE);
	}

	public void addEnumItem(String id, PSSVal val) {
		m_default_val = val.toBigInteger();
		m_items.put(id, m_default_val);
		m_default_val = m_default_val.add(BigInteger.ONE);
	}

	/**
	 * The width of a field of this type: 32 bits (signed) when every item fits, else the
	 * width of {@code int}, so that an item keeps its value (PSS 2.0 Section 8.4 d): the
	 * values of the items are distinct).
	 */
	public int fieldWidth() {
		BigInteger min = BigInteger.ONE.shiftLeft(31).negate();
		BigInteger max = BigInteger.ONE.shiftLeft(31).subtract(BigInteger.ONE);
		for (BigInteger v : m_items.values()) {
			if (v.compareTo(min) < 0 || v.compareTo(max) > 0)
				return PSSIntModel.DEFAULT_INT_SIZE;
		}
		return 32;
	}

	public PSSEnumInst declInst(String id, boolean rand) {
		PSSEnumInst enum_var = new PSSEnumInst(id, getId(), this, rand);
		return enum_var;
	}

	public void declEnumItem(PSSInst inst) {
		m_items.forEach((key, v) -> {
			PSSIntInst item = new PSSIntInst(key, false, PSSIntModel.DEFAULT_INT_SIZE, true);
			PSSIntVal val = new PSSIntVal(v);
			item.assign(val);

			// Enum items can be declared in a package or a component. Enum items are
			// considered static constant members of the enumeration type in which they are
			// declared (PSS 2.0 Section 8.4.1).
			addStaticInst(item);
		});
	}

	@Override
	public void dump(String indent) {

		System.out.println(indent + "enum " + getId() + " {");

		m_items.forEach((key, v) -> {
			System.out.println(indent + key + "=" + v);
		});
		System.out.println(indent + "}");
	}

	@Override
	public boolean isCompatible(PSSModel model) {
		if (model instanceof PSSEnumModel) {
			PSSEnumModel m = (PSSEnumModel) model;
			if (m_items.size() != m.m_items.size())
				return false;
			for (String str : m_items.keySet()) {
				if (!m.m_items.containsKey(str))
					return false;
				if (m_items.get(str) != m.m_items.get(str))
					return false;
			}
			return true;
		}
		return false;
	}

}
