import java.util.*;

public class PSSEnumModel extends PSSModel {
	private HashMap<String, Integer> m_items;
	private Integer m_default_val;

	public PSSEnumModel (String id) {
		super(id);
		m_items = new HashMap<String, Integer>();
		m_default_val = 0;
	}
	public void addEnumItem(String id) {
		m_items.put(id, m_default_val);
		m_default_val++;
	}
	public void addEnumItem(String id, PSSVal val) {
		m_default_val = val.toInt();
		m_items.put(id, m_default_val);
		m_default_val++;
	}
	public PSSEnumInst declInst(String id, boolean rand) {
		PSSEnumInst enum_var = new PSSEnumInst(id, m_id, this, rand);
		return enum_var;
	}
	public void declEnumItem(PSSInst inst) {
		m_items.forEach((key, v) -> {
			PSSIntInst item = new PSSIntInst(key, false, 32, true);
			PSSIntVal val = new PSSIntVal(v);
			item.assign(val);
			inst.addInst(item);
		});
	}

	public void dump (String indent) {
		
		System.out.println(indent + "enum " +  m_id + " {");

		m_items.forEach((key, v) -> {
			System.out.println(indent + key + "=" + v);
		});
		System.out.println(indent + "}");
	}
}
