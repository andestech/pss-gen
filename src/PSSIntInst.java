import java.util.*;
import java.math.*;

public class PSSIntInst extends PSSInst {
	PSSIntVal m_val;
	int m_width;
	boolean m_sign;

	PSSIntDomain m_domain;	// Solution domain

	public PSSIntInst (String id, boolean rand, int width, boolean sign) {
		super(id, "int", rand);
		m_width = width;
		m_sign = sign;
		m_domain = new PSSIntDomain(width, sign);
	}
	public PSSIntDomain getInitDomain() {
		return new PSSIntDomain(m_width, m_sign);
	}
	public PSSVal toVal() {
		if (!m_initialized) {
			PSSMessage.Error("IntInst", "'" + getHierarchyId() + "' is not initialized");
		}
		return m_val;
	}
	public boolean validate() {
		if (!m_domain.InRange(m_val).toBool()) {
			return false;
		}
		return true;
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
	public void randomize_one() {
		BigInteger rand_val = m_domain.random().toBigInteger();
		m_val = new PSSIntVal(rand_val);
		//PSSMessage.Debug("randomize_one: val = " + m_val.toInt());
	}
	public void assignDomain(PSSDomain domain) {
		m_domain = (PSSIntDomain) domain;
		if (m_domain.isEmpty()) {
			PSSMessage.Error("OVERCONSTRAINT", "'" + getHierarchyId() + "' is over-constrained");
		}
	}
	public void assign(PSSVal val) {
		m_initialized = true;
		BigInteger int_val = val.toBigInteger();
		m_val = new PSSIntVal(int_val);
	}

	public String toTargetCode() {
		if (!m_initialized) {
			PSSMessage.Error("INTVAR-0001", "variable '" + getHierarchyId() + "' is not initialized");
		}
		return m_val.toTargetCode();
	}

	public void dump(String indent) {
		System.out.println(indent + m_id + " <" + m_type + ">" + "\"" + m_val.getText() + "\"");
	}

}
