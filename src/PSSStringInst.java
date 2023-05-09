
public class PSSStringInst extends PSSInst {
	public String m_str;

	PSSStringDomain m_domain;

	public PSSStringInst (String id, boolean rand) {
		super(id, "string", rand);
		m_domain = new PSSStringDomain(false);
	}
	public PSSStringDomain getInitDomain() {
		return new PSSStringDomain(false);
	}
	public void assignDomain(PSSDomain domain) {
		m_domain = (PSSStringDomain) domain;
		if (m_domain.isEmpty()) {
			PSSMessage.Error("OVERCONSTRAINT", "'" + getHierarchyId() + "' is over-constrained");
		}
	}
	public PSSVal toVal() {
		if (!m_initialized) {
			PSSMessage.Error("StringInst", "'" + getHierarchyId()+ "' is not initialized");
		}
		return new PSSStringVal(m_str);
	}
	public String toTargetCode() {
		if (!m_initialized) {
			PSSMessage.Error("StringInst", "'" + getHierarchyId() + "' is not initialized");
		}
		return m_str;
	}

	public boolean validate() {
		if (!m_domain.InRange(new PSSStringVal(m_str)).toBool()) {
			return false;
		}
		return true;
	}
	public void randomize_one() {
		m_str = m_domain.random().toStr();
		//PSSMessage.Debug("randomize_one: str = " + m_str);
	}
	public void randomize() {
		if (m_rand) {
			boolean validated = false;
			int retry = 0;

			while (!validated) {
				PSSRandom.limitRetry(retry);
				randomize_one();
				validated = validate();
				retry ++;
			}
			m_initialized = true;
		}
	}
	public void assign(PSSVal val) {
		m_str = val.toStr();
		m_initialized = true;
	}

	public void dump(String indent) {
		System.out.println(indent + m_id + " <" + m_type + ">" + "\"" + m_str + "\"");
	}

}
