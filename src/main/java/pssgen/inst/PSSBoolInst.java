package pssgen.inst;

import pssgen.solver.PSSDomain;
import pssgen.solver.PSSIntDomain;
import pssgen.util.PSSMessage;
import pssgen.util.PSSRandom;
import pssgen.value.PSSBoolVal;
import pssgen.value.PSSVal;

public class PSSBoolInst extends PSSInst implements PSSIScalarInst {
	public boolean m_val;

	/** solution domain over 0 (false) and 1 (true), as for a bit field */
	PSSIntDomain m_domain;

	public PSSBoolInst(String id, boolean rand) {
		super(id, "bool", rand);
		m_domain = getInitDomain();
	}

	public PSSIntDomain getInitDomain() {
		return new PSSIntDomain(1, false);
	}

	public void assignDomain(PSSDomain domain) {
		m_domain = (PSSIntDomain) domain;
		if (m_domain.isEmpty()) {
			PSSMessage.Error("OVERCONSTRAINT", "'" + getHierarchyId() + "' is over-constrained");
		}
	}

	public boolean validate() {
		return m_domain.InRange(toVal()).toBool();
	}

	public PSSVal toVal() {
		return PSSBoolVal.valueOf(m_val);
	}

	public void randomize() {
		if (m_rand) {
			int retry = 0;
			do {
				PSSRandom random = run().random();
				random.limitRetry(retry++);
				m_val = m_domain.random(random).toBigInteger().signum() != 0;
			} while (!validate());
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
