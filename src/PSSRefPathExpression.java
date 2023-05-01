import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class PSSRefPathExpression extends PSSExpression {
	String		m_id;
	String		m_index;
    ArrayList<PSSHierarchicalIDExpression> m_hierarchical_id_list;

	public PSSRefPathExpression(String text) {
		Pattern pattern = Pattern.compile("(.*)\\[(.*)\\]");
		Matcher m = pattern.matcher(text);
        m_hierarchical_id_list = new ArrayList<PSSHierarchicalIDExpression>();

		if (m.find()) {
			m_id = m.group(1);
			m_index = m.group(2);
            if (m_index.matches("^\".*\"$"))
                m_index = m_index.substring(1, m_index.length() - 1);
		}
		else {
			m_id = text;
			m_index = null;
		}

	}

    public void addHierarchicalID(PSSExpression h) {
        PSSHierarchicalIDExpression _h = (PSSHierarchicalIDExpression) h;
        m_hierarchical_id_list.add(_h);
    }

	private PSSInst getScalarInst(PSSInst var) {
		PSSInst id_var = var.findInstance(m_id);
		if (id_var == null) {
			PSSMessage.Error("REF-0001", "Cannot find variable '" + m_id + "'");
		}
		return id_var;
	}

	public PSSInst getInst(PSSInst var) {
		if (m_index != null) {
            PSSInst id_var = var.findInstance(m_id);
            if (id_var == null) {
                PSSMessage.Error("REF-0002", "Cannot find variable '" + m_id + "'");
            }
            PSSInst element_var = null;
            if (id_var instanceof PSSArrayInst || id_var instanceof PSSListInst)
                element_var = getArrayElement(id_var);
            else if (id_var instanceof PSSMapInst)
                element_var = getMapElement(id_var);
            else
                PSSMessage.Fatal("Unknown reference type");
            return element_var;
		}
		else {
			return getScalarInst(var);
		}
	}

    private PSSInst getArrayElement(PSSInst array_var) {
        if (!m_index.matches("[0-9]+")) {
            PSSMessage.Error("REF-0003", m_index + "is not an integer reference.");
        }
        return array_var.indexOf(Integer.valueOf(m_index));
    }

    private PSSInst getMapElement(PSSInst id_var) {
		PSSMessage.Debug("[" + this.getClass().getName() + "] Access map index " + m_index + " of " + m_id);
        PSSMapInst mapInst = (PSSMapInst) id_var;
        PSSInst elementInst = mapInst.get(m_index);
        if (elementInst == null) {
            PSSMessage.Error("REF-0004", "Failed to get the element of "+m_id+"["+m_index+"].");
        }
        return elementInst;
    }

	public PSSVal eval(PSSInst var) {
		PSSInst inst = getInst(var);
        for (PSSHierarchicalIDExpression h: m_hierarchical_id_list) {
            inst = h.getInst(inst);
        }
		return inst.toVal();
	}

	public String getText() {
		if (m_index != null) {
			return m_id;
		}
		else {
			return m_id + "[" + m_index + "]";
		}
	}

}
