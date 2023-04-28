import java.util.*;

public class PSSListInst extends PSSInst {
    PSSModel m_type_model;
	ArrayList<PSSInst> m_list;

	PSSListInst(String id, PSSModel type_model, boolean rand) {
		super(id, "list<"+type_model.m_id+">", rand);
        super.m_rand = false;
        m_type_model = type_model;
		m_list = new ArrayList<PSSInst>();
	}

	public void assign(PSSVal val) {
        if (!PSSSetVal.class.isInstance(val))
            PSSMessage.Fatal("The list type should be assigned as a list_literal.");

        PSSSetVal valset = (PSSSetVal) val;
        m_list.clear();
        for (int i = 0; i < valset.getValList().size(); i++) {
            PSSInst item_inst = m_type_model.declInst("", m_rand);
            PSSVal item_val = val.indexOf(i);
            item_inst.assign(item_val);
            m_list.add(item_inst);
        }
	}

	public void randomize() {
        // PSS Standard V2.0 - 8.1 Data types General
        // The list collection type is not randomizable.
	}

	public PSSInst indexOf(int index) {
		if (index >= m_list.size()) {
			PSSMessage.Error("LISTINDEX", "'"+getHierarchyId()+"' index '"+index+"' is out of range");
		}
		return m_list.get(index);
	}
};

