import java.util.*;

public class PSSStringVal extends PSSVal {
	String m_str;
	boolean m_bound;

	public PSSStringVal(String str) {
		m_str = str;
		m_bound = true;
	}

	public PSSStringVal(String str, boolean bound) {
		m_str = str;
		m_bound = bound;
	}

	public boolean isBound() {
		return m_bound;
	}

	public String getText() {
		if (m_bound) {
			return m_str;
		} else {
			return ".*";
		}
	}

	public int toInt() {
		PSSMessage.Error("STRINTVAL", "StrintVal cannot be converted to string");
		return 0;
	}

	public String toString() {
		return m_str;
	}

	public boolean toBool() {
		return true; // # string is always true
	}

	//
	public PSSBoolVal Equal(PSSVal rhs) {
		String rhs_str = rhs.toString();
		return new PSSBoolVal(m_str.equals(rhs_str));
	}

	public PSSBoolVal NotEqual(PSSVal rhs) {
		String rhs_str = rhs.toString();
		return new PSSBoolVal(!m_str.equals(rhs_str));
	}

	public PSSBoolVal InRange(PSSVal rhs) {
		return Equal(rhs);
	}

	public PSSStringVal join(PSSVal rhs) {
		if (m_bound) {
			if (!rhs.isBound()) {
				return new PSSStringVal(m_str);
			} else if (m_str.equals(rhs.toString())) {
				return new PSSStringVal(m_str);
			} else {
				return null;
			}
		} else {
			return new PSSStringVal(rhs.toString());
		}
	}

	@Override
	public int compareTo(PSSVal o) {
		if (o instanceof PSSStringVal) {
			PSSStringVal s = (PSSStringVal) o;
			if (m_bound == s.m_bound)
				return m_str.compareTo(s.m_str);
			else if (m_bound)
				return -1;
			else
				return 1;
		}
		return super.compareTo(o);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof PSSStringVal) {
			PSSStringVal s = (PSSStringVal) o;
			return m_bound == s.m_bound && m_str.equals(s.m_str);
		}
		return false;
	}

}
