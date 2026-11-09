package pssgen.inst;

import pssgen.solver.PSSDomain;
import pssgen.solver.PSSStringDomain;
import pssgen.util.PSSMessage;
import pssgen.value.PSSStringVal;
import pssgen.value.PSSVal;

public class PSSStringInst extends PSSInst implements PSSIScalarInst {
	/** PSS 2.0 Section 8.5: a string that is not initialized is the empty string. */
	private String m_str = "";

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
	@Override
	public PSSDomain getDomain() {
		return m_domain;
	}
	@Override
	public void restoreDomain(PSSDomain domain) {
		m_domain = (PSSStringDomain) domain;
	}
	public PSSVal toVal() {
		return new PSSStringVal(m_str);
	}
	public String toTargetCode() {
		return m_str;
	}

	public boolean validate() {
		if (!m_domain.InRange(new PSSStringVal(m_str)).toBool()) {
			return false;
		}
		return true;
	}
	public void randomize_one() {
		m_str = m_domain.random(run().random()).toStr();
		//PSSMessage.Debug("randomize_one: str = " + m_str);
	}
	public void randomize() {
		if (isRand()) {
			boolean validated = false;
			int retry = 0;

			while (!validated) {
				run().random().limitRetry(retry);
				randomize_one();
				validated = validate();
				retry ++;
			}
			setInitialized(true);
		}
	}
	public void assign(PSSVal val) {
		m_str = val.toStr();
		setInitialized(true);
	}

	public void dump(String indent) {
		System.out.println(indent + getId() + " <" + getType() + ">" + "\"" + m_str + "\"");
	}

	public final String getStr() {
		return m_str;
	}
}
