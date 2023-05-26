
public class PSSBoolInst extends PSSInst implements PSSIScalarInst {
	public boolean m_val;

	public PSSBoolInst(String id, boolean rand) {
		super(id, "bool", rand);
	}

	public PSSVal toVal() {
		return PSSBoolVal.valueOf(m_val);
	}

	public void randomize() {
		if (m_rand) {
			int rand_int = PSSRandom.nextInt(0, 1);
			m_val = (rand_int == 1);
			m_initialized = true;
		}
	}

	public void randomizeEqual(PSSVal val) {
		m_val = val.toBool();
	}

	public void assign(PSSVal val) {
		m_initialized = true;
		m_val = val.toBool();
	}

	public String toTargetCode() {
		if (!m_initialized) {
			PSSMessage.Error("INTVAR-0001", "variable '" + m_id + "' is not initialized");
		}
		return String.valueOf(m_val);
	}

	public void dump(String indent) {
		System.out.println(indent + m_id + " <" + m_type + ">" + "\"" + m_val + "\"");
	}

}
