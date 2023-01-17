import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class PSSRefPathExpression extends PSSExpression {
	String		m_id;
	String		m_index;

	public PSSRefPathExpression(String text) {
		Pattern pattern = Pattern.compile("(.*)\\[(.*)\\]");
		Matcher m = pattern.matcher(text);

		if (m.find()) {
			m_id = m.group(1);
			m_index = m.group(2);
		}
		else {
			m_id = text;
			m_index = null;
		}

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
			return getArrayElement(var);
		}
		else {
			return getScalarInst(var);
		}
	}


	private PSSInst getArrayElement(PSSInst var) {
		PSSInst array_var = var.findInstance(m_id);
		if (array_var == null) {
			PSSMessage.Error("REF-0002", "Cannot find variable '" + m_id + "'");
		}
		int index;

		if (m_index.matches("[0-9]+")) {
			index = Integer.valueOf(m_index);
		}
		else {
			PSSInst index_var = var.findInstance(m_index);
			if (array_var == null) {
				PSSMessage.Error("REF-0003", "Cannot find variable '" + m_index + "'");
			}
			index = index_var.toVal().toInt();
		}

		
		return array_var.indexOf(index);
	}

	public PSSVal eval(PSSInst var) {
		PSSInst inst = getInst(var);
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
