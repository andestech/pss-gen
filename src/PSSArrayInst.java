import java.util.*;

public class PSSArrayInst extends PSSInst {
    PSSModel m_type_model;
	ArrayList<PSSInst> m_array_elems;

	PSSArrayInst(String id, PSSModel type_model, int dim, boolean rand) {
		super(id, "", false);
        m_type_model = type_model;
		m_array_elems = new ArrayList<PSSInst>();
		for (int i=0; i<dim; i++) {
			PSSInst inst = type_model.declInst(id + "[" + i + "]", rand);
			m_array_elems.add(inst);
		}
	}

	public void assign(PSSVal val) {
		for (int i=0; i<m_array_elems.size(); i++) {
			PSSInst item = m_array_elems.get(i);
			PSSVal item_val = val.indexOf(i);
			item.assign(item_val);
		}
	}

	public void randomize() {
		for (int i=0; i<m_array_elems.size(); i++) {
			PSSInst item = m_array_elems.get(i);
			item.randomize();
		}
	}

	public PSSInst indexOf(int index) {
		return m_array_elems.get(index);
	}

    public PSSInst evalMethod(String identifier, List<PSSVal> args) {
        PSSInst inst = null;
        if (identifier.equals("size")) {
            PSSIntInst intInst = new PSSIntInst(m_id+".size()", false, 32, false);
            intInst.assign(PSSNumber.newDecNumber(Integer.toString(m_array_elems.size())));
            inst = intInst;
        } else if (identifier.equals("sum")) {
            if (m_type_model instanceof PSSIntModel) {
                int sum = 0;
                for (PSSInst elem: m_array_elems)
                    sum += elem.toVal().toInt();
                PSSIntInst intInst = new PSSIntInst(m_id+".size()", false, 32, false);
                intInst.assign(PSSNumber.newDecNumber(Integer.toString(sum)));
                inst = intInst;
            } else {
                PSSMessage.Error("",
                    "Array's sum() method can only be used on numeric data type.");
            }
        } else if (identifier.equals("to_list")) {
            PSSSetVal collectedVal = new PSSSetVal();
            for (PSSInst elem: m_array_elems)
                collectedVal.addVal(elem.toVal());
            PSSListInst listInst = new PSSListInst(m_id+".to_list()", m_type_model, false);
            listInst.assign(collectedVal);
            inst = listInst;
        } else if (identifier.equals("to_set")) {
            PSSMessage.Fatal("Currently unsupported");
        } else
            PSSMessage.Fatal("Unsupported Array method");
        return inst;
    }

};

